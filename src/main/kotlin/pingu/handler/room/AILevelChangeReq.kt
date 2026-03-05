package pingu.handler.room

import pingu.netty.Decode1
import pingu.netty.PKTHandler
import pingu.packet.room.AILevelChange
import pingu.server.Room

val AILevelChangeReq  = PKTHandler { c ->
    val slotIdx = Decode1
    val increaseFlag = Decode1 != 0

    val slot = Room.slots.getOrNull(slotIdx) ?: return@PKTHandler
    if (!slot.isAI) return@PKTHandler

    val current = slot.AILevel

    val newLevel = if (increaseFlag) {
        if (current < 45) current + 15
        else current + 45
    } else {
        if (current <= 46) current - 15
        else current - 45
    }

    if (newLevel !in 1..31) {
        return@PKTHandler
    }

    slot.AILevel = newLevel

    Room BC AILevelChange(slotIdx, newLevel)
}