package pingu.packet

import pingu.netty.PKT
import pingu.server.User

// client = CLoginStage::OnLoginResult | TW_5 = 474F70
fun LoginResult(users: List<User>) = PKT {
    Encode1() // Res

    Encode2(1)
    EncodeStr()

    Encode1(users.size)
    users.forEach { user ->
        Encode4(user.id)
        EncodeStr(user.name)
        Encode1()
        Encode2()

        Encode1(1)
        Encode1(10)
        Encode2(user.level)

        Encode4(user.lucci)
        Encode4(user.exp)
        Encode1()
        EncodeStr() // mail

        Encode1(2)
        Encode1(2)

        Encode4()

        // 這個v7沒有
        val size = 0
        Encode1(size)
        repeat(size) {
            EncodeStr()
            Encode4()
            Encode1()
        }
    }
    EncodeStr()
}

fun LoginResult_JP(users: List<User>) = PKT {
    Encode1() // Res

    Encode2(1)
    EncodeStr()

    val size = 0
    Encode1(size)
    repeat(size) {
        Encode1()
        val unk = true
        Encode1Bool(unk)
        if (!unk) {
            EncodeStr()
            EncodeStr()
            EncodeStr()
            Encode1()
            EncodeStr()
            Encode1()
            Encode1()
        }
    }

    Encode1(users.size)
    users.forEach { user ->
        Encode4(user.id)
        EncodeStr(user.name)
        Encode1()
        EncodeStr()
        Encode4(user.level)
    }
    Encode1()
    Encode1()
}