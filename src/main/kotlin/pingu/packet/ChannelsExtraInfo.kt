package pingu.packet

import kotlinx.serialization.encoding.Encoder
import pingu.isJP
import pingu.isTW
import pingu.netty.PKT

// server = CCenterProcess::MakeChannelsExtraInfoPacket
fun ChannelsExtraInfo() = PKT {
    EncodeStr("http://bnb.digicell.com.tw/webpage/BnB/EVENT/20040618/index.htm")
    Encode2(600)
    Encode2(525)
    Encode2(60)

    if (isTW) {
        Encode1(1) //size
        Encode4(0x7F000001)
        Encode2(9898)

        // NDA
        Encode1(1) //size
        Encode4(0x7F000001)

        // Relay
        Encode1(1) //size
        Encode4(0x7F000001)

        val size = 0
        Encode1(size)
        repeat(size) {
            Encode1()
            Encode1()
            EncodeStr()
        }

        // JUM Server
        Encode1(1)
        Encode4(0x7F000001)
        Encode2(7360)
    } else if (isJP) {
        Encode1(1)
        Encode4(0x7F000001)

        val size = 0
        Encode1(size)
        repeat(size) {
            Encode1()
            Encode1()
            EncodeStr()
        }

        Encode2(1)
    }
}