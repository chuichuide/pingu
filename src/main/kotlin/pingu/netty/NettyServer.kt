package pingu.netty

import io.netty.bootstrap.Bootstrap
import io.netty.bootstrap.ServerBootstrap
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufUtil
import io.netty.buffer.PooledByteBufAllocator
import io.netty.channel.*
import io.netty.channel.epoll.Epoll
import io.netty.channel.epoll.EpollDatagramChannel
import io.netty.channel.epoll.EpollIoHandler
import io.netty.channel.epoll.EpollServerSocketChannel
import io.netty.channel.group.ChannelGroup
import io.netty.channel.group.ChannelMatchers
import io.netty.channel.nio.NioIoHandler
import io.netty.channel.socket.DatagramPacket
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioDatagramChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import pingu.*
import pingu.packet.ConnEstablished
import java.net.InetSocketAddress

object NettyServer {
    private val useEpoll = Epoll.isAvailable() // 檢查是否為 Linux 環境且 Epoll 可用
    private val factory = when {
        useEpoll -> EpollIoHandler.newFactory()
        else -> NioIoHandler.newFactory()
    }

    private val bossGroup = MultiThreadIoEventLoopGroup(1, factory)
    val workerGroup = MultiThreadIoEventLoopGroup(factory)

    private val TCP_PORTS = listOf(3838, 4848, 4849)
    private val UDP_PORTS = listOf(3839, 4849)

    fun start() {
        startTcpServers()
        UDP_PORTS.forEach { port ->
            startUdpServer(port, HandlerRouterUDP)
        }
    }

    private fun startTcpServers() {
        val tcpBoot = ServerBootstrap().apply {
            group(bossGroup, workerGroup)

            channelFactory(ChannelFactory<ServerChannel> {
                if (useEpoll) EpollServerSocketChannel() else NioServerSocketChannel()
            })

            option(ChannelOption.SO_BACKLOG, 1024)
            childOption(ChannelOption.TCP_NODELAY, true)
            childOption(ChannelOption.SO_KEEPALIVE, true)
            childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)

            childHandler(object : ChannelInitializer<SocketChannel>() {
                override fun initChannel(ch: SocketChannel) {
                    ch.pipeline().addLast(
                        Decoder(),
                        Encoder(),
                        ClientSocket(),
                    )

                    ch.writeAndFlush(ConnEstablished())
                }
            })
        }

        TCP_PORTS.forEach { port ->
            tcpBoot.bind(port).addListener { future ->
                if (future.isSuccess) {
                    println("Bind to TCP port: $port")
                } else {
                    println("Failed to bind TCP port: $port")
                }
            }
        }
    }

    fun startUdpServer(port: Int, handler: ChannelHandler) {
        Bootstrap().apply {
            group(workerGroup)

            channelFactory(ChannelFactory<Channel> {
                if (useEpoll) EpollDatagramChannel() else NioDatagramChannel()
            })

            option(ChannelOption.SO_BROADCAST, true)
            option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
            handler(handler)
        }.bind(port).addListener { future ->
            if (future.isSuccess) {
                println("Bind to UDP port: $port")
            } else {
                println("Failed to bind UDP port: $port")
            }
        }
    }
}

fun ChannelGroup.BC(packet: PKT, exclude: Channel? = null) {
    if (exclude == null) {
        writeAndFlush(packet)
    } else {
        writeAndFlush(packet, ChannelMatchers.isNot(exclude))
    }
}

@ChannelHandler.Sharable
private object HandlerRouterUDP : SimpleChannelInboundHandler<DatagramPacket>() {
    val crcLenUDP = when {
        isTW -> 1
        isJP -> 4
        else -> 0
    }

    override fun channelRead0(ctx: ChannelHandlerContext, packet: DatagramPacket) {
        val buf = packet.content()

        val size = buf.readUnsignedByte().toInt() - crcLenUDP

        if (buf.readableBytes() < size) return

        val payload = buf.readSlice(size)
        val opcode = payload.readUnsignedByte().toInt()

        // CRC
        when {
            isTW -> buf.readByte()
            isJP -> buf.readInt()
        }

        if (debugMode) {
            val hexOp = "0x${opcode.toString(16).uppercase()}"
            println("$opcode | $hexOp | UDP Recv")
            println(ByteBufUtil.hexDump(payload).uppercase().chunked(2).joinToString(" "))
        }

        val sender = packet.sender()
        when (opcode) {
            0 -> { //CWaitForLoginDlg::OnTimer
                val userId = payload.readInt()
                // CWaitForLoginDlg::OnAckUDPCommunication
                ctx.channel().sendUDP(sender, 2) {}
            }

            1 -> {
                if (size == 35) {
                }
            }

            11 -> {
                if (size == 23) {
                }
            }

            else -> {}
        }
    }

    private inline fun Channel.sendUDP(
        target: InetSocketAddress,
        opcode: Int,
        block: ByteBuf.() -> Unit
    ) {
        val buf = alloc().ioBuffer()

        try {
            // 佔位
            buf.writeByte(0) // size

            if (isJP)
                buf.writeInt(0)

            buf.writeByte(opcode) // 寫入 Opcode
            buf.block() // 執行業務寫入邏輯

            val packetSize = buf.readableBytes()
            buf.setByte(0, packetSize) // 回填長度

            when {
                isTW -> buf.writeByte(CRC8.UpdateCRC(dwCrcKey = 0, buf, Size = buf.readableBytes()))
                isJP -> buf.writeInt(CRC32.UpdateCRC(dwCrcKey = 0, buf, Size = buf.readableBytes()))
            }

            writeAndFlush(DatagramPacket(buf, target))
        } catch (e: Exception) {
            buf.release()
        }
    }
}