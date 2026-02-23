package pingu.handler.room

import pingu.netty.Decode1
import pingu.netty.PKTHandler
import pingu.packet.room.Airplane
import pingu.server.Room
import pingu.server.Room.lastAirplaneTime

// CGameSession::OnRequestAirplane
val AirplaneReq = PKTHandler { c->
    val now = System.currentTimeMillis()

    // 檢查：距離上次請求需超過 7000ms
    if (lastAirplaneTime + 7000 > now)
        return@PKTHandler

    val count = Decode1
    if (count !in 1..3) {
        error("Strange Request Airplane, nCount=$count")
    }

    val scheduledTime = (now + 2000)

    lastAirplaneTime = scheduledTime

    Room BC Airplane(count, scheduledTime.toInt())
}