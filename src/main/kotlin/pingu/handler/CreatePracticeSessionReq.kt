package pingu.handler

import pingu.netty.Decode1
import pingu.netty.PKTHandler
import pingu.packet.CreatePracticeSessionResult
import pingu.server.CheifMask
import pingu.server.Room
import pingu.server.bomber
import pingu.server.color
import pingu.server.state

// 開啟練習模式房間
// server = CGameSessionTable::CreateSessionPractice
val CreatePracticeSessionReq = PKTHandler { c ->
    val userCount = Decode1 // 1
    repeat(userCount) { i ->
        Decode1 // 10

        Room.slots[i].apply {
            user = c.users[i]

            this.state = CheifMask
            bomber = 5
            color = 0
        }
    }

    repeat(4/*Room.slots.size*/ - userCount) { i ->
        Room.slots[userCount + i].apply {
            bomber = 5
            color = 7
            isAI = true
        }
    }

    c send CreatePracticeSessionResult()
    Room.encodeSlots(c)
}