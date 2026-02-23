package pingu.handler.room

import io.netty.handler.codec.smtp.SmtpRequests.data
import pingu.netty.Decode1
import pingu.netty.PKTHandler
import pingu.packet.room.SelectColor
import pingu.server.Room
import pingu.server.color

val SelectColorReq = PKTHandler { c ->
    val data = Decode1
    val selfIdx = data shr 4
    val colorIdx = data and 0x0F

    Room.slots[selfIdx].color = colorIdx
    Room BC SelectColor(data)
}