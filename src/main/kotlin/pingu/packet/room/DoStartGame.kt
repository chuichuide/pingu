package pingu.packet.room

import pingu.netty.PKT
import pingu.tickCount

// server = CGameSession::DoStartGame
// client = CGameStage::OnStartGame
fun DoStartGame(random: Int) = PKT {
    Encode4(tickCount)
    Encode4(random)
    Encode4()
    Encode4()
}