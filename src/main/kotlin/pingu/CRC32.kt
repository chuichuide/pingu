package pingu

import io.netty.buffer.ByteBuf

object CRC32 {
    private val crc_table = IntArray(256)
    private var bInit = false

    private fun GenCRCTable() {
        repeat(256) { i ->
            var v = i shl 24
            repeat(8) {
                v = if ((v and Int.MIN_VALUE) != 0) {
                    (v shl 1) xor 0x04C11DB7
                } else {
                    v shl 1
                }
            }
            crc_table[i] = v
        }
        bInit = true
    }

    fun UpdateCRC(dwCrcKey: Int, data: ByteArray, Size: Int): Int {
        if (!bInit)
            GenCRCTable()

        var crc = dwCrcKey

        repeat(Size) { i ->
            val index = ((data[i].toInt() and 0xFF) xor (crc ushr 24)) and 0xFF
            crc = crc_table[index] xor (crc shl 8)
        }

        return crc
    }

    fun UpdateCRC(
        dwCrcKey: Int,
        buf: ByteBuf,
        offset: Int = 0,
        Size: Int = buf.readableBytes()
    ): Int {
        if (!bInit)
            GenCRCTable()

        var crc = dwCrcKey

        repeat(Size) { i ->
            val index = (buf.getUnsignedByte(offset + i).toInt() xor (crc ushr 24)) and 0xFF
            crc = crc_table[index] xor (crc shl 8)
        }

        return crc
    }
}