package pingu.handler

import pingu.netty.Decode1
import pingu.netty.Decode4
import pingu.netty.DecodeStr
import pingu.netty.PKTHandler
import pingu.nextUserId

import pingu.packet.EnterLobbyStage
import pingu.packet.EnterShopStage
import pingu.packet.Unk210
import pingu.server.User
import sun.nio.ch.Net.localAddress
import java.net.InetSocketAddress

// 進頻道後第一個包
// server = CConnectPool::OnConnEstablished
val OnConnEstablished = PKTHandler { c ->
    val userCount = Decode1
    if (userCount in 1..2) {
        repeat(userCount) { i ->
            val name = DecodeStr
            val userId = Decode4 // 3
            Decode4 // 0

            c.users.add(
                User(c, userId, name)
            )
        }

        val port = (c.ch.localAddress() as InetSocketAddress).port
        when (port) {
            4848 -> {
                val checksum = Decode4 xor Decode4
                Decode1 // 1
                c.send(
                    Unk210(),
                    EnterLobbyStage()
                )
            }
            4849 -> {
                c.send(
                    EnterShopStage()
                )
            }
        }

    }
}