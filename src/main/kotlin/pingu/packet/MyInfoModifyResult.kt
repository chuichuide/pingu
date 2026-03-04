package pingu.packet

import pingu.netty.PKT

fun MyInfoModifyResult(success: Boolean = true) = PKT {
    Encode1Bool(success)
}