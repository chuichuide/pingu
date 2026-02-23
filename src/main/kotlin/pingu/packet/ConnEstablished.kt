package pingu.packet

import pingu.Ver
import pingu.isNA
import pingu.isTH
import pingu.isVN
import pingu.netty.PKT

// 握手
// client = CMainSystem::OnConnect
// server = CMainSystem::OnConnEstablished
fun ConnEstablished() = PKT {
    Encode2() // 好像是能更新的最低版本
    Encode2(Ver)
    Encode4() //  ReceivedPacket Opcode offset
    EncodeStr() // PatchURL

    // NDA
    if (isTH || isVN || isNA) {
        Encode1Bool()
    }
}