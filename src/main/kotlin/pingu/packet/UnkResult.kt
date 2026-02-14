package pingu.packet

import pingu.netty.PKT

fun UnkResult() = PKT {
    Encode1(1) // Res

    Encode1()
    Encode4()
    Encode4(0x7F000001)
    Encode2(3838)
}