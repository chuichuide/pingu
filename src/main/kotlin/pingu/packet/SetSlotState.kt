package pingu.packet

import pingu.netty.PKT

fun SetSlotState(slotId: Int, AI: Boolean = false, AILevel: Int = 1) = PKT {
    Encode1(slotId)

    EncodeBuffer("00 06 07")

    Encode1Bool(AI)
    if (AI)
        Encode2(AILevel)
}