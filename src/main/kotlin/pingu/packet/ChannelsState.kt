package pingu.packet

import pingu.isTW
import pingu.netty.PKT

// server = CChannelCache::MakeStatePacket
fun ChannelsState() = PKT {
    val channelSize = if (isTW) 5 else 1
    Encode2(channelSize)

    repeat(channelSize) {i ->
        Encode2() // 開啟 = 0 關閉 = -1
    }
}