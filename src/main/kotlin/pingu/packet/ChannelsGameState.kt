package pingu.packet

import pingu.Ver
import pingu.netty.PKT

// server = CCenterProcess::MakeChannelsGameStatePacket
fun ChannelsGameState() = PKT {
    val availableChannelSize = 1
    Encode1(availableChannelSize)

    repeat(availableChannelSize) { i ->
        Encode1(10) // ChannelID
        Encode1(1) // State
        Encode2(Ver) // 版本
        Encode2(3)
    }
}