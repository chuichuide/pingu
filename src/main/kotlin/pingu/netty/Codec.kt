package pingu.netty

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageCodec
import pingu.CRC32
import pingu.CRC8
import pingu.debugMode

val ByteBuf.m_nCipherDegree
    get() = 1 // ReceivedPacketBase可以省略狀態 所以放這

// CPacketSocket::SetHeaderType
val m_nHeaderType = 0

// CPacketSocket::SetHeaderCode
val m_nHeaderCodeRcvBase = 192
val m_nHeaderCodeSndBase = 102
val m_nHeaderCodeModifier = 231

// CPacketSocket::SetSequence
val m_nPacketRcvSeqDelta = 3
val m_nPacketSndSeqDelta = 3

class Codec : ByteToMessageCodec<PKT>() {
    var m_nCipherDegree = 0 // 給SendPacketBase用的
    var m_nPacketRcvSeq = 40
    var m_nPacketSndSeq = 40

    val reusableWriter = SendPacketBase()

    // CSendPacketBase::EncodePacket
    override fun encode(ctx: ChannelHandlerContext, pkt: PKT, out: ByteBuf) {
        // 重複使用SendPacketBase 減少GC負擔
        reusableWriter.reset(out, m_nCipherDegree)

        val headerCode = (m_nHeaderType + m_nHeaderCodeModifier) xor (m_nHeaderCodeSndBase + m_nPacketSndSeq)
        out.writeByte(headerCode)

        val headerIdx = out.writerIndex()
        out.writeShort(0) // 長度佔位符

        val bodyStartIdx = out.writerIndex()
        val opcode = OpcodeManager.getSendOp(pkt.javaClass) // m_nPacketType

        reusableWriter.Encode1(opcode)
        pkt.encode(reusableWriter)

        val payloadLen = out.writerIndex() - bodyStartIdx

        if (debugMode) {
            println(
                "[" + "${
                    pkt.javaClass.name.substringAfter('$').substringBefore('$')
                }] " + "$opcode | 0x${opcode.toString(16).uppercase()} | Send"
            )
        }

        // (Zero-Copy Slice)
        val payload = out.slice(bodyStartIdx, payloadLen)

        if (m_nCipherDegree in 1..2) {
            out.setShort(headerIdx, payloadLen xor 0xA569) // 回填長度

            // 根據明文產生CRC
            when (m_nCipherDegree) {
                1 -> out.writeInt(CRC32.UpdateCRC(dwCrcKey = m_nPacketSndSeq, payload, Size = payload.writerIndex()))

                2 -> out.writeByte(CRC8.UpdateCRC(dwCrcKey = m_nPacketSndSeq, payload, Size = payload.writerIndex()))
            }

            payload.simpleStreamEncrypt3(m_nPacketSndSeq) // 加密
        } else { // 握手
            out.setShort(headerIdx, payloadLen) // 回填長度
            m_nCipherDegree = out.m_nCipherDegree
        }

        m_nPacketSndSeq += m_nPacketSndSeqDelta
    }

    // CReceivedPacketBase::DecodePacket
    override fun decode(ctx: ChannelHandlerContext, buf: ReceivedPacketBase, out: MutableList<Any>) {
        while (buf.readableBytes() >= 4) {
            val serverHeaderCode = (m_nHeaderType + m_nHeaderCodeModifier) xor (m_nHeaderCodeRcvBase + m_nPacketRcvSeq)

            val clientHeaderCode = buf.readByte()
            val payloadLen = buf.Decode2

            if (serverHeaderCode.toByte() != clientHeaderCode || buf.readableBytes() < payloadLen) {
                println("HeaderCode不正確或decode可讀長度不足 關閉連接")
                ctx.close()
                return
            }

            // (Zero-Copy Slice, 因為要傳給 Handler 所以要retain )
            val payload = buf.readRetainedSlice(payloadLen)
            payload.simpleStreamDecrypt3(m_nPacketRcvSeq) // 解密

            // CRC驗證
            val isCrcValid = when (buf.m_nCipherDegree) {
                1 -> buf.readInt() ==
                        CRC32.UpdateCRC(dwCrcKey = m_nPacketRcvSeq, payload, Size = payload.writerIndex())

                2 -> buf.readByte() ==
                        CRC8.UpdateCRC(dwCrcKey = m_nPacketRcvSeq, payload, Size = payload.writerIndex()).toByte()

                else -> true
            }

            if (!isCrcValid) {
                println("CRC不正確 關閉連接")
                payload.release()
                ctx.close()
                return
            }

            m_nPacketRcvSeq += m_nPacketRcvSeqDelta

            out += payload
        }
    }
}