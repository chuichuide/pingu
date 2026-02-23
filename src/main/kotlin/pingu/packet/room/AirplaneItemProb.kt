package pingu.packet.room

import pingu.netty.PKT
import pingu.tickCount

// server = CGameSession::SendAirplaneItemProb
fun AirplaneItemProb() = PKT {
    Encode4(60000)
    Encode4(22000)

    Encode1(4)

    Encode1(0)
    Encode1(50)

    Encode1(1)
    Encode1(40)

    Encode1(2)
    Encode1(5)

    Encode1(5)
    Encode1(5)
}