package pingu.packet.room

import pingu.netty.Decode1
import pingu.netty.PKT
import pingu.netty.ReceivedPacketBase

fun ReceivedPacketBase.Airplane(count: Int, scheduledTime: Int) = PKT {
    Encode4(scheduledTime)
    Encode1(count)

    repeat(count) {
        Encode1(Decode1)
        Encode1(Decode1)
        Encode1(Decode1)
        Encode1(Decode1)
    }
}