package pingu.handler.room

import pingu.netty.Decode2
import pingu.netty.PKTHandler

import pingu.packet.room.SelectMap
import pingu.server.Room

// CGameSession::OnSelectMap
val SelectMapReq = PKTHandler { c ->
    val map = Decode2
    println("mapId: $map")

    Room.mapId = map
    Room BC SelectMap(map)
}