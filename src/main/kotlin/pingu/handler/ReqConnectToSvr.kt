package pingu.handler

import pingu.netty.Decode1
import pingu.netty.Decode2
import pingu.netty.Decode4
import pingu.netty.PKTHandler
import pingu.netty.send
import pingu.packet.ResConnectToSvr

// 選擇頻道
// server = CLoginPool::OnReqConnectToSvr
val ReqConnectToSvr = PKTHandler { c ->
    val ChannelID = Decode1 // 10
    Decode4 // 0
    val ClientVer = Decode2 // 5

    c send ResConnectToSvr(1, ChannelID)
}