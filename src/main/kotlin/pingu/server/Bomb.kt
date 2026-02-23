package pingu.server

class Bomb(
    val id: Int,
    val ownerSlotId: Int,
    val pos: Int,
    val bombAttr: Int,
    val bSpecial: Boolean,
    val expireAt: Long = System.currentTimeMillis() + 2500
)