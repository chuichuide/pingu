package pingu.packet.room

import pingu.netty.PKT

fun SelectBomber(data: Int) = PKT {
    Encode1(data)
}