package pingu.packet

import pingu.netty.PKT

// 開房間的回應
// server = CGameSessionTable::CreateSession
fun CreateSessionResult(mode: Int) = PKT {
    Encode1() // Res

    Encode2(0) // 房間號
    Encode4()
    Encode1(mode)
}