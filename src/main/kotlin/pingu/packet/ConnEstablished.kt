package pingu.packet

import pingu.Ver
import pingu.netty.PKT

// 握手
// server = CMainSystem::OnConnEstablished
fun ConnEstablished() = PKT {
    Encode2(3) // 好像是能更新的最低版本
    Encode2(Ver)
    Encode4() //  ReceivedPacket Opcode offset
    EncodeStr() // PatchURL
}