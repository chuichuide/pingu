package pingu.handler.room

import pingu.netty.Decode1
import pingu.netty.PKTHandler
import pingu.packet.room.SelectBomber
import pingu.server.Room
import pingu.server.bomber

val SelectBomberReq = PKTHandler { c ->
    val data = Decode1
    val selfIdx = data shr 4
    val bomberIdx = data and 0x0F

    Room.slots[selfIdx].bomber = bomberIdx
    Room BC SelectBomber(data)
}