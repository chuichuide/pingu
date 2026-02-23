package pingu

import pingu.netty.NettyServer

var nextUserId = 1

fun main() {
    NettyServer.start()
}