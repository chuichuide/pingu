package pingu.packet.room

import pingu.netty.PKT

// CGameSession::OnSelectMap
fun SelectMap(map: Int) = PKT {
    Encode2(map)
}