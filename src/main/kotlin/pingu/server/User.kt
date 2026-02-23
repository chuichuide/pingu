package pingu.server

import pingu.netty.ClientSocket

class User(
    val c: ClientSocket,
    val id: Int,
    val name: String,
    val level: Int = 5,
    val lucci: Int = 1234567,
    val exp: Int = 0
)