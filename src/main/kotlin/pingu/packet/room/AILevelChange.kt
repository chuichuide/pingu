package pingu.packet.room

import pingu.netty.PKT

fun AILevelChange(slotIdx: Int, aiLevel: Int) = PKT {
    Encode1(slotIdx)
    Encode2(aiLevel)
}