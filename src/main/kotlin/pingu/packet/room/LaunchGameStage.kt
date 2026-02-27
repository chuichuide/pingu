package pingu.packet.room

import pingu.netty.PKT
import pingu.server.Room
import pingu.server.bomber
import pingu.server.color
import pingu.tickCount

// server = CGameSession::LaunchGameStage
// client = CPrepareStage::OnLaunchGameStage | TW_5 = 45C9F0
fun LaunchGameStage(spawnPositions: List<Int>) = PKT {
    Encode4(tickCount)

    Encode2(Room.mapId)
    // 台版 87 = 賽車場05 | 89 = 賽車場07 | 156 = 海14 | 188 = 村10
    // 日版 118 = 海14

    Encode1(Room.activeSlotCount)
    Room.slots.forEachIndexed { index, slot ->
        if (slot.user != null || slot.isAI) {
            Encode1(index) // slotId
            Encode1Bool() //  隊長 好像是公會戰用的
            Encode1(spawnPositions[index]) // 角色開始的位置
            Encode1(slot.bomber) // 人物
            Encode1(slot.color) // 隊伍顏色
        }
    }
}