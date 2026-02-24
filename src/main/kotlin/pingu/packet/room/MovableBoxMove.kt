package pingu.packet.room

import pingu.isJP
import pingu.netty.Decode1
import pingu.netty.PKT
import pingu.netty.ReceivedPacketBase

// server = CGameSession::OnMovableBoxMove
fun MovableBoxMove(v1: Int, v2: Int, v3: Int, v4: Int, v5: Int, v6: Int) = PKT {
    Encode1(v1) // 2
    Encode1(v2) // 1
    Encode1(v3) // 6
    Encode1(v4) // 2
    if (isJP) {
        Encode1(v5) // 6
        Encode1(v6) // 0
    }
}