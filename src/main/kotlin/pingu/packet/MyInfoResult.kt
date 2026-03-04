package pingu.packet

import pingu.netty.PKT
import pingu.server.User

fun MyInfoResult(slotIdx: Int, user: User) = PKT {
    Encode1(slotIdx)
    EncodeStr(user.nickName)
    Encode1(user.gender)
    EncodeStr(user.address)
    EncodeStr(user.profile)
    EncodeStr(user.birthday)
    Encode1Bool(user.reveal)
}