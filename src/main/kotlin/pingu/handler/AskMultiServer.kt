package pingu.handler

import pingu.isTW
import pingu.netty.Decode4
import pingu.netty.PKTHandler
import pingu.netty.send
import pingu.packet.MultiServerInit

// server = CLoginPool::OnAskMultiServer
val AskMultiServer = PKTHandler { c ->
    val multisvr_HashValue = Decode4
    if (isTW) {
        val multisvrn_HashValue = Decode4
    }

    c send MultiServerInit()
}