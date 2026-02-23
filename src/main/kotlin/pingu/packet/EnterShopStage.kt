package pingu.packet

import pingu.netty.PKT

fun EnterShopStage() = PKT {
    Encode1() // size

    Encode4() // fxstock.ini hashvalue
    Encode4() // stringtable.ini hashValue
    Encode4() // packagemap.ini hashValue

    EncodeStr()
    EncodeStr()

    Encode1()
    Encode1()
    Encode1()
    Encode1Bool()

    Encode2() // size
}