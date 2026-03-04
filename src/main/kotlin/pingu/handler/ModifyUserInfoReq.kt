package pingu.handler

import pingu.netty.Decode1
import pingu.netty.DecodeStr
import pingu.netty.PKTHandler
import pingu.packet.MyInfoModifyResult

val ModifyUserInfoReq = PKTHandler { c ->
    val reqSlotIdx = Decode1
    val user = c.users.getOrNull(reqSlotIdx) ?: return@PKTHandler
    val nickName = DecodeStr
    val gender = Decode1
    val address = DecodeStr
    val profile = DecodeStr
    val birthday = DecodeStr
    val reveal = Decode1 == 1
    user.apply {
        this.nickName = nickName
        this.gender = gender
        this.address = address
        this.profile = profile
        this.birthday = birthday
        this.reveal = reveal
    }
    c send(MyInfoModifyResult())
}