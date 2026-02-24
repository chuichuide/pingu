package pingu.packet.room

import pingu.netty.PKT
import pingu.server.AirplaneItem

fun Airplane(scheduledTime: Int, items: List<AirplaneItem>) = PKT {
    Encode4(scheduledTime)
    Encode1(items.size)

    items.forEach { item ->
        Encode1(item.v1)
        Encode1(item.v2)
        Encode1(item.v3)
        Encode1(item.v4)
    }
}