package pingu.packet

import pingu.netty.PKT

// 開啟練習模式房間的回應
// server = CGameSessionTable::DoCreatePracticeSession
fun CreatePracticeSessionResult() = PKT {
    Encode1()
    Encode2()
    Encode4(2)
}