package pingu.handler.room

import pingu.netty.Decode1
import pingu.netty.Decode2
import pingu.netty.PKTHandler

import pingu.packet.room.BombKickThrow
import pingu.server.Room

// server = CGameSession::OnBombKickThrow
val BombKickThrowReq = PKTHandler { c ->
    val slotId = Decode1
    val bombId = Decode2
    val fromPos =  Decode1

    val targetPos = Decode1
    val speed = Decode2

    Room BC BombKickThrow(slotId, bombId, targetPos)
}