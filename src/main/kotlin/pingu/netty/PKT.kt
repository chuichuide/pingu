package pingu.netty

import io.netty.buffer.ByteBuf
import jdk.internal.org.jline.keymap.KeyMap.key
import jdk.internal.org.jline.utils.Colors.s
import pingu.ACP
import pingu.debugMode

fun interface PKTHandler {
    fun ReceivedPacketBase.handle(c: Client)
}

fun interface PKT {
//    fun SendPacketBase.encode() // 如果用這個每個Packet函數要inline才看的到函數名字 不然會只有object+lambda名字
    fun encode(out: SendPacketBase)
}

inline fun PKT(crossinline block: SendPacketBase.() -> Unit) = PKT { out ->
    block(out)
}

typealias ReceivedPacketBase = ByteBuf

val ReceivedPacketBase.Decode1
    get() = Decode1At()
val ReceivedPacketBase.Decode2
    get() = Decode2At()
val ReceivedPacketBase.Decode4
    get() = Decode4At()
val ReceivedPacketBase.DecodeStr
    get() = DecodeStrAt()
fun ReceivedPacketBase.DecodeEncryptedStr(key: Int) =
    DecodeEncryptedStrAt(key)

private fun ReceivedPacketBase.Decode1At(): Int {
    if (m_nCipherDegree in 1..2) {
        val value = BufferManipulator.Decrypt1(this)
        if (debugMode)
            println("decrypt value = $value")
        return value
    } else {
        return BufferManipulator.Decode1(this)
    }
}

private fun ReceivedPacketBase.Decode2At(): Int {
    if (m_nCipherDegree in 1..2) {
        val value = BufferManipulator.Decrypt2(this)
        if (debugMode)
            println("decrypt value = $value")
        return value
    } else {
        return BufferManipulator.Decode2(this)
    }
}

private fun ReceivedPacketBase.Decode4At(): Int {
    if (m_nCipherDegree in 1..2) {
        val value = BufferManipulator.Decrypt4(this)
        if (debugMode)
            println("decrypt value = $value")
        return value
    } else {
        return BufferManipulator.Decode4(this)
    }
}

private fun ReceivedPacketBase.DecodeStrAt(): String {
    val length = Decode2At()
    return readCharSequence(length, ACP).toString()
}

private fun ReceivedPacketBase.DecodeEncryptedStrAt(key: Int): String {
    val length = Decode2At()
    simpleStreamDecrypt3(key, readerIndex(), length)
    val decryptString = readCharSequence(length, ACP).toString()
    if (debugMode)
        println("decryptString: $decryptString")
    return decryptString
}

class SendPacketBase {
    private lateinit var buf: ByteBuf
    private var m_nCipherDegree = 0

    fun reset(buf: ByteBuf, m_nCipherDegree: Int) {
        this.buf = buf
        this.m_nCipherDegree = m_nCipherDegree
    }

    fun Encode1(n: Int = 0) =
        Encode1At(n)

    fun Encode1Bool(b: Boolean = false) =
        Encode1At(if (b) 1 else 0)

    fun Encode2(n: Int = 0) =
        Encode2At(n)

    fun Encode4(n: Int = 0) =
        Encode4At(n)

    fun EncodeStr(s: String = "") {
        val SA = s.toByteArray(ACP)
        Encode2(SA.size)
        buf.writeBytes(SA)
    }

    fun EncodeBuffer(ba: ByteArray) {
        buf.writeBytes(ba)
    }

    fun EncodeBuffer(s: String) {
        buf.writeBytes(s.toBA())
    }

    private fun Encode1At(n: Int) {
        if (this.m_nCipherDegree in 1..2) {
            BufferManipulator.Encrypt1(buf, n)
        } else {
            BufferManipulator.Encode1(buf, n)
        }
    }

    private fun Encode2At(n: Int) {
        if (this.m_nCipherDegree in 1..2) {
            BufferManipulator.Encrypt2(buf, n)
        } else {
            BufferManipulator.Encode2(buf, n)
        }
    }

    private fun Encode4At(n: Int) {
        if (this.m_nCipherDegree in 1..2) {
            BufferManipulator.Encrypt4(buf, n)
        } else {
            BufferManipulator.Encode4(buf, n)
        }
    }
}

private object BufferManipulator {
    fun Decode1(buf: ByteBuf) = buf.readUnsignedByte().toInt()
    fun Decode2(buf: ByteBuf) = buf.readUnsignedShort()
    fun Decode4(buf: ByteBuf) = buf.readInt()

    fun Decrypt1(buf: ByteBuf) = buf.readUnsignedByte().toInt() xor 0x5A
    fun Decrypt2(buf: ByteBuf) = buf.readUnsignedShort() xor 0xA569
    fun Decrypt4(buf: ByteBuf) = buf.readInt() xor 0x96CA5395.toInt()

    fun Encode1(buf: ByteBuf, n: Int) = buf.writeByte(n)
    fun Encode2(buf: ByteBuf, n: Int) = buf.writeShort(n)
    fun Encode4(buf: ByteBuf, n: Int) = buf.writeInt(n)

    fun Encrypt1(buf: ByteBuf, n: Int) = buf.writeByte(n xor 0x5A)
    fun Encrypt2(buf: ByteBuf, n: Int) = buf.writeShort(n xor 0xA569)
    fun Encrypt4(buf: ByteBuf, n: Int) = buf.writeInt(n xor 0x96CA5395.toInt())
}

fun String.toBA() =
    replace("|", "").replace("\\s+".toRegex(), "").chunked(2).map { it.toInt(16).toByte() }.toByteArray()