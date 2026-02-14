package pingu

import io.netty.buffer.ByteBuf

object CRC8 {
    private val crc8_table = IntArray(256)
    private var bInit = false

    private fun GenCRCTable() {
        repeat(256) { i ->
            var v = i
            repeat(8) {
                v = if (v and 0x80 != 0) {
                    (v shl 1) xor 7
                } else {
                    v shl 1
                }
            }
            crc8_table[i] = v and 0xFF
        }
        bInit = true
    }

    fun UpdateCRC(dwCrcKey: Int, p: ByteArray, Size: Int): Int {
        if (!bInit)
            GenCRCTable()

        var crc = dwCrcKey and 0xFF

        repeat(Size) { i ->
            val index = p[i].toInt() and 0xFF
            crc = crc8_table[crc xor index]
        }

        return crc
    }

    fun UpdateCRC(dwCrcKey: Int, p: ByteBuf, Size: Int): Int {
        if (!bInit)
            GenCRCTable()

        var crc = dwCrcKey and 0xFF

        repeat(Size) { i ->
            val index = p.getUnsignedByte(i).toInt()
            crc = crc8_table[crc xor index]
        }

        return crc
    }
}