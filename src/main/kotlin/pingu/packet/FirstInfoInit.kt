package pingu.packet

import pingu.netty.PKT

// server = CLoginPool::OnAskFirstInfo
fun FirstInfoInit() = PKT {
//    if (Decode4 == 0)
        EncodeStr("http://mail.nexonclub.com/clubadmin/confirmuser")
        EncodeStr("http://id.nexonclub.com/nxidquery.idsocno")
}