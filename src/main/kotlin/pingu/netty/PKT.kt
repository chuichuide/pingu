package pingu.netty

import io.netty.buffer.ByteBuf
import io.netty.util.ReferenceCounted
import pingu.ACP
import pingu.debugMode
import pingu.showDecValue

fun interface PKTHandler {
    fun ReceivedPacketBase.handle(c: ClientSocket)
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
    if (m_nCipherDegreeInit in 1..3) {
        val value = BufferManipulator.Decrypt1(this)
        if (debugMode && showDecValue)
            println("decrypt value = $value")
        return value
    } else {
        return BufferManipulator.Decode1(this)
    }
}

private fun ReceivedPacketBase.Decode2At(): Int {
    if (m_nCipherDegreeInit in 1..3) {
        val value = BufferManipulator.Decrypt2(this)
        if (debugMode && showDecValue)
            println("decrypt value = $value")
        return value
    } else {
        return BufferManipulator.Decode2(this)
    }
}

private fun ReceivedPacketBase.Decode4At(): Int {
    if (m_nCipherDegreeInit in 1..3) {
        val value = BufferManipulator.Decrypt4(this)
        if (debugMode && showDecValue)
            println("decrypt value = $value")
        return value
    } else {
        return BufferManipulator.Decode4(this)
    }
}

private fun ReceivedPacketBase.DecodeStrAt(): String {
    val length = when (m_nCipherDegreeInit) {
        3 -> Decode4At()
        else -> Decode2At()
    }
    return readString(length, ACP)
}

private fun ReceivedPacketBase.DecodeEncryptedStrAt(key: Int): String {
    val length = when (m_nCipherDegreeInit) {
        3 -> Decode4At()
        else -> Decode2At()
    }

    simpleStreamDecrypt3(key, readerIndex(), length)

    val decryptString = readString(length, ACP)

    if (debugMode && showDecValue)
        println("decryptString: $decryptString")
    return decryptString
}

class SendPacketBase(
    val buf: ByteBuf,
    private val m_nCipherDegree: Int
) : ReferenceCounted by buf {

    fun Encode1(n: Number = 0) =
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

    private fun Encode1At(n: Number) {
        if (this.m_nCipherDegree in 1..3) {
            BufferManipulator.Encrypt1(buf, n)
        } else {
            BufferManipulator.Encode1(buf, n)
        }
    }

    private fun Encode2At(n: Int) {
        if (this.m_nCipherDegree in 1..3) {
            BufferManipulator.Encrypt2(buf, n)
        } else {
            BufferManipulator.Encode2(buf, n)
        }
    }

    private fun Encode4At(n: Int) {
        if (this.m_nCipherDegree in 1..3) {
            BufferManipulator.Encrypt4(buf, n)
        } else {
            BufferManipulator.Encode4(buf, n)
        }
    }
}

private object BufferManipulator {
    fun Decode1(buf: ByteBuf): Int = buf.readUnsignedByte().toInt()
    fun Decode2(buf: ByteBuf): Int = buf.readUnsignedShort()
    fun Decode4(buf: ByteBuf): Int = buf.readInt()

    fun Decrypt1(buf: ByteBuf): Int = buf.readUnsignedByte().toInt() xor 0x5A
    fun Decrypt2(buf: ByteBuf): Int = buf.readUnsignedShort() xor 0xA569
    fun Decrypt4(buf: ByteBuf): Int = buf.readInt() xor 0x96CA5395.toInt()

    fun Encode1(buf: ByteBuf, n: Number) = buf.writeByte(n.toInt())
    fun Encode2(buf: ByteBuf, n: Int) = buf.writeShort(n)
    fun Encode4(buf: ByteBuf, n: Int) = buf.writeInt(n)

    fun Encrypt1(buf: ByteBuf, n: Number) = buf.writeByte(n.toInt() xor 0x5A)
    fun Encrypt2(buf: ByteBuf, n: Int) = buf.writeShort(n xor 0xA569)
    fun Encrypt4(buf: ByteBuf, n: Int) = buf.writeInt(n xor 0x96CA5395.toInt())
}

fun String.toBA() =
    replace("|", "").replace("\\s+".toRegex(), "").chunked(2).map { it.toInt(16).toByte() }.toByteArray()