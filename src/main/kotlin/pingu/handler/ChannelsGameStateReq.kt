package pingu.handler

import pingu.netty.PKTHandler

import pingu.packet.ChannelsGameState

// 只有日版有這個 在發完MultiServerInit後不發AskFirstInfo而是發這個
val ChannelsGameStateReq = PKTHandler { c ->
    c send ChannelsGameState()
}