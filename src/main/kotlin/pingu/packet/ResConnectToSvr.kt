package pingu.packet

import pingu.netty.PKT

// 選擇頻道的回應
// server = CLoginPool::OnCenterResConnectToSvr
fun ResConnectToSvr(res: Int, ChannelID: Int) = PKT {
    Encode1(res)
    if (res == 1) {
        Encode1(ChannelID)
        Encode4()
        Encode4(0x7F000001)
        Encode2(4848)
    }
}