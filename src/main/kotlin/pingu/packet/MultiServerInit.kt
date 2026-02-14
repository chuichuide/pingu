package pingu.packet

import pingu.netty.PKT

// server = CMultiServer::Init
fun MultiServerInit() = PKT {
    Encode1Bool()
    // TODO
    Encode1Bool()
    // TODO
}