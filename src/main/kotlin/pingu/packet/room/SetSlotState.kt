package pingu.packet.room

import pingu.netty.PKT
import pingu.server.Encode
import pingu.server.Slot

fun SetSlotState(slotId: Int, slot: Slot) = PKT {
    Encode1(slotId)

    slot.Encode(this)

    Encode1Bool(slot.isAI)
    if (slot.isAI)
        Encode2(slot.AILevel)
}