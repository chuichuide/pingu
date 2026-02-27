package pingu.packet.shop

import jdk.internal.vm.vector.VectorSupport.test
import pingu.netty.PKT
import pingu.netty.toBA

fun FxStockInit() = PKT {
    val test = ("18 03 00 00 00 00 00 00").toBA()
    Encode4(test.size)
    EncodeBuffer(test)
}