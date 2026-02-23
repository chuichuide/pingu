package pingu.packet.room

import pingu.netty.PKT

fun StartGameResult(res: Int) = PKT {
    Encode1(res)
}