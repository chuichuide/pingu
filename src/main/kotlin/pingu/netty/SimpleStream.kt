package pingu.netty

import io.netty.buffer.ByteBuf

fun ByteBuf.simpleStreamEncrypt3(key: Int, offset: Int = 0, size: Int = readableBytes()) {
    val blockCount = size shr 2
    val remaining = size and 3

    var modifiedKey = key xor 0x53351D9C

    // 處理 4-byte 區塊
    if (blockCount > 0) {
        // 處理第一個 DWORD
        var pastBlockPlain = getIntLE(offset)
        setIntLE(offset, modifiedKey xor pastBlockPlain)

        repeat(blockCount - 1) { i ->
            modifiedKey -= 0x63CAACE3
            val currentIdx = offset + ((i + 1) * 4)

            val currentBlock = getIntLE(currentIdx)
            setIntLE(currentIdx,
                modifiedKey xor currentBlock xor pastBlockPlain
            )

            pastBlockPlain = currentBlock // 更新為下一次使用的原始值
        }
        modifiedKey = pastBlockPlain
    }

    // 處理剩餘的 1-3 bytes
    if (remaining > 0) {
        val startByteIdx = offset + (blockCount * 4)
        repeat(remaining) { i ->
            val mask = modifiedKey shr (i * 8)
            val originalByte = getByte(startByteIdx + i).toInt()
            setByte(startByteIdx + i, originalByte xor mask)
        }
    }
}

fun ByteBuf.simpleStreamDecrypt3(key: Int, offset: Int = 0, size: Int = readableBytes()) {
    val blockCount = size shr 2
    val remaining = size and 3

    var modifiedKey = key xor 0x53351D9C

    // 處理 4-byte 區塊
    if (blockCount > 0) {
        // 處理第一個 DWORD
        val firstBlockCipher = getIntLE(offset)
        var pastBlockPlain = modifiedKey xor firstBlockCipher // 計算出原本的明文
        setIntLE(offset, pastBlockPlain) // 寫回明文

        repeat(blockCount - 1) { i ->
            modifiedKey -= 0x63CAACE3
            val currentIdx = offset + ((i + 1) * 4)

            val currentBlockCipher = getIntLE(currentIdx)

            val currentBlockPlain = modifiedKey xor currentBlockCipher xor pastBlockPlain

            setIntLE(currentIdx, currentBlockPlain)

            // 更新 pastBlockPlain 為剛剛解出來的明文，供下一輪使用
            pastBlockPlain = currentBlockPlain
        }

        // 加密結束時 modifiedKey 會變成最後一個 block 的明文
        // 這裡剛剛還原了最後一個明文，直接賦值即可
        modifiedKey = pastBlockPlain
    }

    // 處理剩餘的 1-3 bytes
    if (remaining > 0) {
        val startByteIdx = offset + (blockCount * 4)
        repeat(remaining) { i ->
            val mask = modifiedKey shr (i * 8)
            val cipherByte = getByte(startByteIdx + i).toInt()
            setByte(startByteIdx + i, cipherByte xor mask)
        }
    }
}