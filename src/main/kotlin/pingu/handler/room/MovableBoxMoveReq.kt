package pingu.handler.room

import pingu.isJP
import pingu.netty.Decode1
import pingu.netty.PKTHandler

import pingu.packet.room.MovableBoxMove
import pingu.server.Room

val MovableBoxMoveReq = PKTHandler { c ->
    val v1 = Decode1 // 2
    val v2 = Decode1 // 1
    val v3 = Decode1 // 6
    val v4 = Decode1 // 2

    val v5 = if (isJP) Decode1 else 0 // 6
    val v6 = if (isJP) Decode1 else 0 // 0

    Room BC MovableBoxMove(v1, v2, v3, v4, v5, v6)
}