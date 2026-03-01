package pingu.handler.room

import pingu.netty.Decode1
import pingu.netty.PKTHandler
import pingu.packet.room.DoOpenCloseSlot
import pingu.server.Room
import pingu.server.bomber
import pingu.server.color

val OpenCloseSlotReq = PKTHandler { c ->
    val data = Decode1
    val slotIdx = data ushr 4
    val slotClose = (data and 0xF) != 0

    // TODO slotIdx 檢查, 檢查角色當前是否在練習模式

    val slot = Room.slots[slotIdx]

    if (!slotClose) {
        slot.apply {
            bomber = 1
            color = 7
            isAI = true
        }
    } else {
        slot.apply {
            bomber = 0
            isAI = false
        }
    }
    Room BC DoOpenCloseSlot(slotIdx, slotClose, 1)
}