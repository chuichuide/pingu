package pingu.netty

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import io.netty.handler.codec.MessageToByteEncoder
import pingu.*

// ReceivedPacketBase可以省略CipherDegree狀態 所以可以直接用這個
inline val m_nCipherDegreeInit get() = if (isTH || isVN || isNA) 3 else 1

val headerLen = when (m_nCipherDegreeInit) {
    3 -> 4
    else -> 3
}
val payloadLenIdx = when (m_nCipherDegreeInit) {
    3 -> 2
    else -> 1
}
val opcodeLen = when (m_nCipherDegreeInit) {
    3 -> 2
    else -> 1
}
val crcLen = when (m_nCipherDegreeInit) {
    1 -> 4
    2, 3 -> 1
    else -> 0
}

val minimumLen = headerLen + opcodeLen + crcLen

// CPacketSocket::SetHeaderType
const val m_nHeaderType = 0

// CPacketSocket::SetHeaderCode
const val m_nHeaderCodeRcvBase = 192
const val m_nHeaderCodeSndBase = 102
const val m_nHeaderCodeModifier = 231

// CPacketSocket::SetSequence
const val m_nPacketRcvSeqDelta = 3
const val m_nPacketSndSeqDelta = 3

class Decoder : ByteToMessageDecoder() {
    var m_nPacketRcvSeq = 40

    // CReceivedPacketBase::DecodePacket
    override fun decode(ctx: ChannelHandlerContext, buf: ByteBuf, out: MutableList<Any>) {
        while (buf.readableBytes() >= minimumLen) {
            val readerIdx = buf.readerIndex()

            // 使用get可以省略當進來的長度不夠時需要resetReaderIndex的步驟等

            // CPacketSocket::CheckCode
            val headerCode = (m_nHeaderType + m_nHeaderCodeModifier) xor (m_nHeaderCodeRcvBase + m_nPacketRcvSeq)
            if (buf.getByte(readerIdx) != headerCode.toByte()) {
                println("HeaderCode 錯誤，斷開連接: ${ctx.channel().remoteAddress()}")
                buf.clear()
                ctx.close()
                return
            }

            val payloadLen = buf.getUnsignedShort(readerIdx + payloadLenIdx) xor 0xA569

            val totalLen = headerLen + payloadLen + crcLen

            if (buf.readableBytes() < totalLen) return

            buf.skipBytes(headerLen) // 前面已經用get來取值了 跳過header

            // (Zero-Copy Slice, 因為要傳給 Handler 所以要retain )
            val payload = buf.readRetainedSlice(payloadLen)
            payload.simpleStreamDecrypt3(m_nPacketRcvSeq) // 解密

            // CRC驗證
            val isCrcValid = when (m_nCipherDegreeInit) {
                1 -> buf.readInt() ==
                        CRC32.UpdateCRC(dwCrcKey = m_nPacketRcvSeq, payload)

                2, 3 -> buf.readByte() ==
                        CRC8.UpdateCRC(dwCrcKey = m_nPacketRcvSeq, payload).toByte()

                else -> true
            }

            if (!isCrcValid) {
                println("CRC 錯誤，斷開連接: ${ctx.channel().remoteAddress()}")
                payload.release()
                ctx.close()
                return
            }

            out += payload

            m_nPacketRcvSeq += m_nPacketRcvSeqDelta
        }
    }
}

//class Encoder : MessageToMessageEncoder<PKT>() {
class Encoder : MessageToByteEncoder<PKT>() {
    var m_nCipherDegree = 0 // 給SendPacketBase用的
    var m_nPacketSndSeq = 40
    inline val headerLen
        get() = when (m_nCipherDegree) {
            3 -> 6
            else -> 3
        }

    override fun encode(ctx: ChannelHandlerContext, pkt: PKT, out: ByteBuf) {
        val opcode = OpcodeManager.getSendOp(pkt.javaClass) // m_nPacketType
        SendPacketBase(out, m_nCipherDegree).apply { // 操作buf = 操作out
            // 把writerIndex設在header結束的位置 來寫入payload
            buf.writerIndex(headerLen)

            if (m_nCipherDegree == 3) {
                Encode2(opcode)
            } else {
                Encode1(opcode)
            }
            pkt.encode(this)

            val payloadLen = buf.writerIndex() - headerLen

            // 根據明文payload產生CRC
            when (m_nCipherDegree) {
                1 ->
                    buf.writeInt(CRC32.UpdateCRC(dwCrcKey = m_nPacketSndSeq, buf, headerLen, payloadLen))

                2, 3 ->
                    buf.writeByte(CRC8.UpdateCRC(dwCrcKey = m_nPacketSndSeq, buf, headerLen, payloadLen))
            }

            val totalLen = buf.writerIndex()

            if (debugMode) {
                pkt.logPacket(opcode)
            }

            // 把writerIndex設在0 來寫入header
            buf.writerIndex(0)

            val headerCode = (m_nHeaderType + m_nHeaderCodeModifier) xor (m_nHeaderCodeSndBase + m_nPacketSndSeq)
            buf.writeByte(headerCode)

            if (m_nCipherDegree in 1..3) {
                // 寫入長度
                if (m_nCipherDegree == 3) {
                    buf.writeByte(0)
                    Encode4(payloadLen)
                } else {
                    Encode2(payloadLen)
                }
                // 加密
                buf.simpleStreamEncrypt3(m_nPacketSndSeq, headerLen, payloadLen)
            } else { // 握手
                Encode2(payloadLen)
                m_nCipherDegree = m_nCipherDegreeInit
            }

            // 將writerIndex還原
            buf.writerIndex(totalLen)
        }

        m_nPacketSndSeq += m_nPacketSndSeqDelta
    }

    /*    override fun encode(ctx: ChannelHandlerContext, pkt: PKT, out: MutableList<Any>) {
            val opcode = OpcodeManager.getSendOp(pkt.javaClass) // m_nPacketType
            val payload = ctx.alloc().ioBuffer()

            SendPacketBase(payload, m_nCipherDegree).apply {
                if (m_nCipherDegree == 3) {
                    Encode2(opcode)
                } else {
                    Encode1(opcode)
                }
                pkt.encode(this)
            }

            val payloadLen = payload.readableBytes()

            if (debugMode) {
                pkt.logPacket(opcode)
            }

            val header = ctx.alloc().directBuffer(headerLen)

            val headerCode = (m_nHeaderType + m_nHeaderCodeModifier) xor (m_nHeaderCodeSndBase + m_nPacketSndSeq)
            header.writeByte(headerCode)

            if (m_nCipherDegree in 1..3) {
                // 寫入長度
                if (m_nCipherDegree == 3) {
                    header.writeByte(0)
                    header.writeInt(payloadLen xor 0x96CA5395.toInt())
                } else {
                    header.writeShort(payloadLen xor 0xA569)
                }
                // 根據明文產生CRC
                when (m_nCipherDegree) {
                    1 -> payload.writeInt(CRC32.UpdateCRC(dwCrcKey = m_nPacketSndSeq, payload, Size = payloadLen))
                    2, 3 -> payload.writeByte(CRC8.UpdateCRC(dwCrcKey = m_nPacketSndSeq, payload, Size = payloadLen))
                }
                // 加密
                payload.simpleStreamEncrypt3(m_nPacketSndSeq, Size = payloadLen)
            } else { // 握手
                header.writeShort(payloadLen)
                m_nCipherDegree = m_nCipherDegreeInit
            }

            out += header
            out += payload

            m_nPacketSndSeq += m_nPacketSndSeqDelta
        }*/
}

fun PKT.logPacket(opcode: Int) {
    println(
        "[" + "${
            javaClass.name.substringAfter('$').substringBefore('$')
        }] " + "$opcode | 0x${opcode.toString(16).uppercase()} | 發送"
    )
}