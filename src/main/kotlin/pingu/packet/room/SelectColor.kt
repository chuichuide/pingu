package pingu.packet.room

import pingu.netty.PKT

fun SelectColor(data: Int) = PKT {
    Encode1(data)
}