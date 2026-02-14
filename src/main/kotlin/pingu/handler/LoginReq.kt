package pingu.handler

import pingu.isTW
import pingu.netty.*
import pingu.packet.LoginResult
import pingu.packet.LoginResult_JP
import pingu.server.User


// client = CMainSystem::Login | TW_5 = 430A80
// server = CLoginPool::OnLogin
val LoginReq = PKTHandler { c ->
    val users: MutableList<User> = ArrayList() // 之後移到別的地方放

    val userCount = Decode1
    repeat(userCount) { i ->
        val name = DecodeEncryptedStr(0x11223344)
        val pw = DecodeEncryptedStr(0x44332211)

        users.add(
            User(i, name)
        )
    }

    val unk = Decode4

    c.send(
        if (isTW) LoginResult(users)
        else LoginResult_JP(users)
    )
}