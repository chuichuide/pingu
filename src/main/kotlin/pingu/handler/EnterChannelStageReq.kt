package pingu.handler

import pingu.netty.Decode4
import pingu.netty.PKTHandler
import pingu.netty.send
import pingu.packet.ChannelsExtraInfo
import pingu.packet.ChannelsGameState
import pingu.packet.ChannelsInfo
import pingu.packet.ChannelsState

// server = RequestEnterChannelStage
val EnterChannelStageReq = PKTHandler { c ->
    val ChannelCache_HashValue = Decode4

    c.send(
        ChannelsInfo(),
        ChannelsState(),
        ChannelsGameState(),
        ChannelsExtraInfo()
    )
}