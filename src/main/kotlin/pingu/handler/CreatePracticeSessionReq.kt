package pingu.handler

import pingu.netty.Decode1
import pingu.netty.PKTHandler
import pingu.netty.send
import pingu.packet.*

// 開啟練習模式房間
// server = CGameSessionTable::CreateSessionPractice
val CreatePracticeSessionReq = PKTHandler { c ->
    val userCount = Decode1 // 1
    if (userCount > 0) {
        Decode1 // 10
    }

    c.send(
        CreatePracticeSessionResult(),
        SetPlayer(0),
        SetSlotState(1, true)
    )
}