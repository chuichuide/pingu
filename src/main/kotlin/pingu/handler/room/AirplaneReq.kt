package pingu.handler.room

import pingu.netty.Decode1
import pingu.netty.PKTHandler
import pingu.packet.room.Airplane
import pingu.server.AirplaneItem
import pingu.server.Room
import pingu.server.Room.lastAirplaneTime

// CGameSession::OnRequestAirplane
val AirplaneReq = PKTHandler { c ->
    val now = System.currentTimeMillis()

    // 檢查：距離上次請求需超過 7000ms
    if (lastAirplaneTime + 7000 > now)
        return@PKTHandler

    val count = Decode1

    if (count in 1..3) {
        val airplaneItems = mutableListOf<AirplaneItem>()
        repeat(count) { i ->
            val v1 = Decode1 // 0
            val v2 = Decode1 // 42
            val v3 = Decode1 // 9
            val v4 = Decode1 // 0
            airplaneItems.add(
                AirplaneItem(v1, v2, v3, v4)
            )
        }

        val scheduledTime = (now + 2000)

        lastAirplaneTime = scheduledTime

        Room BC Airplane(scheduledTime.toInt(), airplaneItems)
    } else {
        error("Strange Request Airplane, nCount=$count")
    }
}