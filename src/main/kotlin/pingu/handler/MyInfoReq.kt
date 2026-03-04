package pingu.handler

import pingu.netty.Decode1
import pingu.netty.PKTHandler
import pingu.packet.MyInfoResult

val MyInfoReq = PKTHandler { c ->
    val reqSlotIdx = Decode1
    val user = c.users.getOrNull(reqSlotIdx) ?: return@PKTHandler
    c send(MyInfoResult(reqSlotIdx, user))
}