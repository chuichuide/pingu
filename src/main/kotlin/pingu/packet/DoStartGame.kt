package pingu.packet

import pingu.netty.PKT
import pingu.tickCount

// server = CGameSession::DoStartGame
fun DoStartGame() = PKT {
    Encode4(tickCount)
    Encode4(1175)
    Encode4()
    Encode4()
}