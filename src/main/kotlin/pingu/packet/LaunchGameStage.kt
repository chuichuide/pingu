package pingu.packet

import pingu.netty.PKT
import pingu.tickCount

// server = CGameSession::LaunchGameStage
// client TW_5 = 45C9F0
fun LaunchGameStage() = PKT {
    Encode4(tickCount)

    Encode2(204) // mapId

    val usersize = 2
    Encode1(usersize)
    repeat(usersize) { i ->
        Encode1(i) // slotId
        Encode1Bool() //  隊長
        Encode1(i) // 角色開始的位置
        Encode1(6) // 人物
        Encode1(i) // 隊伍顏色
    }
}