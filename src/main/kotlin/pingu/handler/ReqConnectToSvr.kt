package pingu.handler

import pingu.netty.Decode1
import pingu.netty.Decode2
import pingu.netty.Decode4
import pingu.netty.PKTHandler

import pingu.packet.ResConnectToSvr

// 選擇頻道
// server = CLoginPool::OnReqConnectToSvr
val ReqConnectToSvr = PKTHandler { c ->
    val ChannelID = Decode1 // 10
    Decode4 // 0

//    if (ChannelID != 11) { // 11 = 商城
//        val ClientVer = Decode2 // 5
//    }

    val res = 1
    c send ResConnectToSvr(res, ChannelID)
}