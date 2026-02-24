package pingu.packet.room

import pingu.netty.PKT
import pingu.server.Bomb
import pingu.server.Room.bombs

fun SetBombState(bombs: List<Bomb>, state: Int = 0) = PKT {
    Encode1(bombs.size)
    bombs.forEach { bomb ->
        Encode2(bomb.id)
        Encode1(state) // 0 = 爆炸
    }
}