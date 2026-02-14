package pingu.netty

import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufUtil
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import pingu.debugMode
import java.io.IOException

@ChannelHandler.Sharable
object HandlerRouter : SimpleChannelInboundHandler<ByteBuf>() {
    val handlerArray = arrayOfNulls<PKTHandler>(0xFFFF)

    override fun channelRead0(ctx: ChannelHandlerContext, buf: ByteBuf) {
        val client = ctx.channel() ?: return
        val opcode = buf.Decode1 // m_nPacketType
        val handler = handlerArray[opcode]

        if (debugMode) {
            logPacket(opcode, handler, buf)
        }

        if (handler == null) return

        try {
            handler.run {
                buf.handle(client)
            }
        } catch (e: Throwable) {
            error("業務邏輯錯誤 [${OpcodeManager.recvOps[opcode]}] | ${e.message}")
        }
        //SimpleChannelInboundHandler是<ByteBuf>的話會自己釋放 不用手動釋放
    }

    private fun logPacket(opcode: Int, handler: PKTHandler?, pkt: ByteBuf) {
        val opName = OpcodeManager.recvOps[opcode] ?: "UNKNOWN"
        val hexOp = "0x${opcode.toString(16).uppercase()}"
        val status = if (handler != null) "Recv" else "找不到 Handler"

        println("[$opName] $opcode | $hexOp | $status")

        val remaining = pkt.readableBytes()
        if (remaining in 2..99) {
            println(ByteBufUtil.hexDump(pkt).uppercase().chunked(2).joinToString(" "))
        }
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        println("${cause.message}")

        if (cause !is IOException)
            cause.printStackTrace()

        ctx.close()
    }
}