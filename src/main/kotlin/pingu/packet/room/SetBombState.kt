package pingu.packet.room

import pingu.netty.PKT
import pingu.server.Bomb

fun SetBombState(bombs: List<Bomb>, state: Int = 0) = PKT {
    Encode1(bombs.size)
    bombs.forEach { bomb ->
        Encode2(bomb.id)
        Encode1(state) // 0 = 爆炸
    }
}

fun SetBombState(bombId: Int, state: Int = 0) = PKT {
    Encode1(1)
    Encode2(bombId)
    Encode1(state) // 0 = 爆炸
}