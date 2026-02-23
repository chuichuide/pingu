package pingu.packet.room

import pingu.isJP
import pingu.netty.Decode1
import pingu.netty.PKT
import pingu.netty.ReceivedPacketBase

// server = CGameSession::OnMovableBoxMove
fun ReceivedPacketBase.MovableBoxMove() = PKT {
    Encode1(Decode1) // 2
    Encode1(Decode1) // 1
    Encode1(Decode1) // 6
    Encode1(Decode1) // 2
    if (isJP) {
        Encode1(Decode1) // 6
        Encode1(Decode1) // 0
    }
}