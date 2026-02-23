package pingu.handler.room

import pingu.isJP
import pingu.netty.Decode1
import pingu.netty.PKTHandler

import pingu.packet.room.EatItemResult
import pingu.packet.room.EatItemResult_JP
import pingu.server.Room

// server = CGameSession::OnRequestEatItem
val EatItemReq = PKTHandler { c ->
    val slotId = Decode1
    val ItemID = Decode1
    val ItemType = Decode1

    if (isJP) {
        val v4 = Decode1
        Room BC EatItemResult_JP(slotId, ItemID, ItemType, v4)
    } else {
        Room BC EatItemResult(slotId, ItemID, ItemType)
    }
}