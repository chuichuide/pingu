package pingu.packet.room

import pingu.netty.PKT

fun DoOpenCloseSlot(slotIdx: Int, slotClose: Boolean, aiLevel: Int? = null) = PKT {
    val flag = if (slotClose) 1 else 0
    Encode1((slotIdx shl 4) or flag)
    if (!slotClose && aiLevel != null) {
        Encode2(aiLevel)
    }
}