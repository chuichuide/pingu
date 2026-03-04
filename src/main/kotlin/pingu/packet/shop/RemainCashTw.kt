package pingu.packet.shop

import pingu.netty.PKT

fun RequestRemainCashTw(success: Boolean = true, cash: Int = 0, notice: String = "") = PKT {
    Encode1Bool(success)
    if (success) {
        Encode4(cash)
    } else {
        EncodeStr(notice)
    }
}