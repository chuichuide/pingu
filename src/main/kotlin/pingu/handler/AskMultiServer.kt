package pingu.handler

import pingu.isJP
import pingu.isTW
import pingu.netty.Decode4
import pingu.netty.PKTHandler
import pingu.packet.MultiServerInit

// server = CLoginPool::OnAskMultiServer
val AskMultiServer = PKTHandler { c ->
    val multisvr_HashValue = Decode4
    if (!isJP) {
        val multisvrn_HashValue = Decode4
    }

    c send MultiServerInit()

    // NA_12 -> 192 | 2 | 67 | 61 | 1 | 51
}