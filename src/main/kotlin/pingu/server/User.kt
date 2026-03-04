package pingu.server

import pingu.netty.ClientSocket

class User(
    val c: ClientSocket,
    val id: Int,
    val name: String,
    var nickName: String = "",
    var address: String = "",
    var birthday: String = "",
    var profile: String = "",
    var gender: Int = 0,
    var reveal: Boolean = true,
    val level: Int = 5,
    val lucci: Int = 1234567,
    val exp: Int = 0
)