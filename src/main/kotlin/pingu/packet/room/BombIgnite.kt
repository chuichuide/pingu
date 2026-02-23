package pingu.packet.room

import pingu.isJP
import pingu.netty.PKT

fun BombIgnite(slotId: Int, pos: Int, bombAttr: Int, bombId: Int, unk: Int) = PKT {
    Encode1(slotId)
    if (isJP) {
        Encode1(unk)
    }
    Encode1(pos)
    Encode1(bombAttr)

    Encode2(bombId)
}