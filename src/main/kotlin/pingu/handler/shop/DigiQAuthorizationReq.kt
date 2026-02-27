package pingu.handler.shop

import pingu.netty.DecodeStr
import pingu.netty.PKTHandler
import pingu.packet.shop.DigiQAuthorizationResult

val DigiQAuthorizationReq = PKTHandler { c ->
    val acc = DecodeStr
    val pw = DecodeStr

    val res = 12
    c send DigiQAuthorizationResult(12)
}