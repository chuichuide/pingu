package pingu.handler.room

import pingu.isJP
import pingu.netty.Decode1
import pingu.netty.PKTHandler
import pingu.server.Room

val BombIgniteReq = PKTHandler { c ->
    val slotId = Decode1 // 1
    val unk = if (isJP) Decode1 else 0
    val pos = Decode1 // 21
    val bombAttr = Decode1 // 2

    // not sure
    val x = pos shr 4
    val y = pos and 0x0F

    val power = bombAttr and 0x0F
    val bSpecial = ((bombAttr shr 6) and 1) == 1

    Room.addBomb(slotId, pos, bombAttr, bSpecial, unk)
}