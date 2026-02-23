package pingu.server

import pingu.netty.SendPacketBase

const val CheifMask = 2
class Slot(
    var user: User? = null,
    val meta: ByteArray = ByteArray(3), // [0]:狀態, [1]:角色, [2]:顏色
    var isAI: Boolean = false,
    var AILevel: Int = 1
)

var Slot.state: Number
    get() = meta[0]
    set(value) { meta[0] = value.toByte() }

var Slot.bomber: Number
    get() = meta[1]
    set(value) { meta[1] = value.toByte() }

var Slot.color: Number
    get() = meta[2]
    set(value) { meta[2] = value.toByte() }

fun Slot.Encode(sendPacket: SendPacketBase) {
    sendPacket.EncodeBuffer(meta)
}