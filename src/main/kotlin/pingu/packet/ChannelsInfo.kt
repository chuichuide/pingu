package pingu.packet

import pingu.netty.PKT

// server = CChannelCache::MakeInfoPacket
fun ChannelsInfo() = PKT {
    Encode1Bool()
    // TODO
}