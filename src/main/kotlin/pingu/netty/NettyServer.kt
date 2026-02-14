package pingu.netty

import io.netty.bootstrap.Bootstrap
import io.netty.bootstrap.ServerBootstrap
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufUtil
import io.netty.channel.*
import io.netty.channel.epoll.Epoll
import io.netty.channel.epoll.EpollEventLoopGroup
import io.netty.channel.epoll.EpollServerSocketChannel
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.DatagramPacket
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioDatagramChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import pingu.CRC32
import pingu.CRC8
import pingu.isJP
import pingu.isTW
import pingu.packet.ConnEstablished
import java.net.InetSocketAddress

object NettyServer {
    private val useEpoll = Epoll.isAvailable() // 檢查是否為 Linux 環境且 Epoll 可用

    fun start() {
        val bossGroup = createGroup(1)
        val workerGroup = createGroup(0)

        try {
            val boot = ServerBootstrap().apply {
                group(bossGroup, workerGroup)
                channel(serverChannelClass())

                childOption(ChannelOption.TCP_NODELAY, true)
                childOption(ChannelOption.SO_KEEPALIVE, true)
                childOption(ChannelOption.ALLOCATOR, io.netty.buffer.PooledByteBufAllocator.DEFAULT)
            }

            bind(boot, 3838) // Login
            startUdpServer(3839, HandlerRouterUDP)
            bind(boot, 4848) // GameServer
            startUdpServer(4849, HandlerRouterUDP)

        } catch (e: Exception) {
            e.printStackTrace()
            bossGroup.shutdownGracefully()
            workerGroup.shutdownGracefully()
        }
    }

    private fun bind(boot: ServerBootstrap, port: Int) {
        boot.childHandler(object : ChannelInitializer<SocketChannel>() {
            override fun initChannel(ch: SocketChannel) {
                val pipe = ch.pipeline()

                pipe.addLast("codec", Codec())
                pipe.addLast("handler", HandlerRouter)

                ch send ConnEstablished()
            }
        })

        boot.bind(port).addListener { future ->
            if (future.isSuccess) {
                println("Bind on TCP port $port")
            } else {
                System.err.println("Bind failed on UDP port $port: ${future.cause()}")
            }
        }
    }

    private fun createGroup(nThreads: Int): EventLoopGroup =
        if (useEpoll) EpollEventLoopGroup(nThreads) else NioEventLoopGroup(nThreads)

    private fun serverChannelClass(): Class<out io.netty.channel.ServerChannel> =
        if (useEpoll) EpollServerSocketChannel::class.java else NioServerSocketChannel::class.java

    fun startUdpServer(port: Int, handler: ChannelHandler): Channel {
        val group = createGroup(1)
        return Bootstrap().apply {
            group(group)
            channel(NioDatagramChannel::class.java)
            option(ChannelOption.SO_BROADCAST, true)
            option(ChannelOption.SO_RCVBUF, 65535)
            handler(handler)
        }.bind(port).addListener { future ->
            if (future.isSuccess) {
                println("Bind on UDP port $port")
            } else {
                System.err.println("Bind failed on UDP port $port: ${future.cause()}")
            }
        }.sync().channel()
    }
}

@ChannelHandler.Sharable
private object HandlerRouterUDP : SimpleChannelInboundHandler<DatagramPacket>() {

    override fun channelRead0(ctx: ChannelHandlerContext, packet: DatagramPacket) {
        val buf = packet.content()
        if (buf.readableBytes() < 1) return

        val size = buf.readByte()
        val opcode = buf.readUnsignedByte().toInt()

        val hexOp = "0x${opcode.toString(16).uppercase()}"

//        println("$opcode | $hexOp | UDP Recv")
//        println(ByteBufUtil.hexDump(buf).uppercase().chunked(2).joinToString(" "))

        val sender = packet.sender()

        when (opcode) {
            0 -> { //CWaitForLoginDlg::OnTimer
                val userId = buf.readInt()
                // CWaitForLoginDlg::OnAckUDPCommunication
                ctx.channel().sendUDP(sender, 2) {}
            }

            1 -> {
                if (buf.readableBytes() == 35) {
                }
            }

            11 -> {
                if (buf.readableBytes() == 23) {
                }
            }

            else -> {}
        }

        // CRC
        if (isTW)
            buf.readByte()
        else if (isJP)
            buf.readByte()
    }

    private fun Channel.sendUDP(
        target: InetSocketAddress,
        opcode: Int,
        block: ByteBuf.() -> Unit
    ) {
        val buf = alloc().ioBuffer()
        try {
            buf.writeByte(0) // size

            if (isJP)
                buf.writeInt(0)

            buf.writeByte(opcode) // 寫入 Opcode
            buf.block() // 執行業務寫入邏輯

            buf.setByte(0, buf.readableBytes()) // 回填長度

            if (isTW)
                buf.writeByte(CRC8.UpdateCRC(dwCrcKey = 0, buf, Size = buf.writerIndex()))
            else if (isJP)
                buf.writeInt(CRC32.UpdateCRC(dwCrcKey = 0, buf, Size = buf.writerIndex()))

            writeAndFlush(DatagramPacket(buf, target))
        } catch (e: Exception) {
            buf.release()
            throw e
        }
    }
}