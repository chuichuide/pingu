package pingu.handler.room

import pingu.netty.Decode1
import pingu.netty.Decode2
import pingu.netty.PKTHandler

// 角色死掉或遊戲結束時會發的
// server = CGameSession::OnSetBomberEvent
val SetBomberEventReq = PKTHandler { c ->
    val eventTypeAndSlot = Decode1
    val v1 = Decode1
    val v2 = Decode2

    val v3 = eventTypeAndSlot and 0xF
    val v4 = eventTypeAndSlot ushr 4

    when (v4) {
        0 -> {}
        1, 7 -> {}
        2 -> {}
        3 -> {
            val v5 = Decode1
        }
        4 -> {
            val v6 = Decode1
        }
        5 -> {}
        6 -> {}
        8 -> {
            val v7 = Decode1
            val v8 = Decode1
        }
    }
}