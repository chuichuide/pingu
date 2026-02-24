package pingu.netty

import com.sun.tools.javac.resources.ct
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufUtil
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import pingu.debugMode
import pingu.server.User
import java.io.IOException

val handlerArray = arrayOfNulls<PKTHandler>(1000)
class ClientSocket : SimpleChannelInboundHandler<ByteBuf>() {
    lateinit var ctx: ChannelHandlerContext
    lateinit var ch: Channel

//    val users = arrayOfNulls<User>(2)
    val users = mutableListOf<User>()

    override fun handlerAdded(ctx: ChannelHandlerContext) {
        this.ctx = ctx
        this.ch = ctx.channel()
    }

    override fun channelRead0(ctx: ChannelHandlerContext, buf: ByteBuf) {
        val opcode = when (m_nCipherDegreeInit) {
            3 -> buf.Decode2
            else -> buf.Decode1
        }

        val handler = handlerArray[opcode]
        val opName = OpcodeManager.recvOps[opcode]
        if (debugMode || opName == null || handler == null) {
            logPacket(opcode, opName, handler, buf)
        }

        if (handler == null) {
            return
        }

        try {
            handler.run {
                buf.handle(this@ClientSocket)
            }
        } catch (e: Throwable) {
            error("業務邏輯錯誤 [${OpcodeManager.recvOps[opcode]}] | ${e.message}")
        }
    }

    private fun logPacket(opcode: Int, opName: String?, handler: PKTHandler?, pkt: ByteBuf) {
        val hexOp = "0x${opcode.toString(16).uppercase()}"
        val status = if (handler != null) "接收" else "找不到 Handler"

        println("[${opName ?: "UNKNOWN"}] $opcode | $hexOp | $status | ${ch.remoteAddress()}")

        val remaining = pkt.readableBytes()
        if (remaining in 1..99) {
            println(ByteBufUtil.hexDump(pkt).uppercase().chunked(2).joinToString(" "))
        }
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        if (debugMode) {
            println("${cause.message}")
            if (cause !is IOException) {
                cause.printStackTrace()
            }
        }
        ctx.close()
    }

    fun send(vararg packets: PKT) {
        packets.forEach(ctx::write)
        ctx.flush()
    }

    infix fun send(packet: PKT) {
        ctx.writeAndFlush(packet)
    }

    fun close() {
        ctx.close()
    }
}