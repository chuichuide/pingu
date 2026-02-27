package pingu.packet.room

import pingu.netty.PKT
import pingu.server.Bomb
import pingu.server.Room.bombs

fun SetBombState(bombs: List<Bomb>) = PKT {
    Encode1(bombs.size)
    bombs.forEach { bomb ->
        Encode2(bomb.id)
        Encode1(bomb.state) // 0 = 爆炸 1 = MovingStop?
    }
}