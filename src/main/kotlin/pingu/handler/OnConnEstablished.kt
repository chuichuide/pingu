package pingu.handler

import pingu.netty.Decode1
import pingu.netty.Decode4
import pingu.netty.DecodeStr
import pingu.netty.PKTHandler
import pingu.netty.send
import pingu.packet.EnterLobbyStage
import pingu.packet.Unk210

// 進頻道後第一個包
// server = CConnectPool::OnConnEstablished
val OnConnEstablished = PKTHandler { c ->
    val userCount = Decode1

    if (userCount in 1..2) {
        repeat(userCount) { i ->
            val name = DecodeStr
            val userId = Decode4 // 3
            Decode4 // 0
        }
        val checksum = Decode4 xor Decode4
        Decode1 // 1

        c.send(
            Unk210(),
            EnterLobbyStage()
        )
    } else {
        println("Connect Established UserNo Invalid")
        c.close()
    }
}