package pingu.packet

import pingu.netty.PKT

// server = CMainPool::OnCenterResConnEstablished
fun EnterShopStage() = PKT {
    Encode1() // size

    Encode4(0x286D6440) // fxstock.ini hashvalue
    Encode4(0xDB1BEBAB.toInt()) // stringtable.ini hashValue
    Encode4(0x0F36E0AA) // packagemap.ini hashValue

    EncodeStr()
    EncodeStr()

    Encode1(1)
    Encode1()
    Encode1(1)
    Encode1Bool()

    // CItemMan::MakeBonusMap
    Encode2() // size
}