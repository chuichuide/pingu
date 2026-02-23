package pingu.handler

import pingu.netty.Decode4
import pingu.netty.PKTHandler
import pingu.packet.FirstInfoInit

// server = CLoginPool::OnAskFirstInfo
val AskFirstInfo = PKTHandler { c ->
    c send FirstInfoInit()
}