package pingu.packet.room

import pingu.netty.PKT

fun BombKickThrow(slotId: Int, bombId: Int, targetPos: Int) = PKT {
    Encode1(slotId)
    Encode2(bombId)
    Encode1(targetPos)
}