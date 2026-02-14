package pingu.packet

import pingu.netty.PKT

// server = MakeSetPlayerPacket
// clinet = TW_5 = 4B0020
val enterEffect =  true
fun SetPlayer(slotId: Int) = PKT {
    Encode1(slotId)
    Encode4() // userId
    Encode1Bool(enterEffect)
    Encode1()
    if (enterEffect) {
        Encode1(5)
    }
    EncodeBuffer("02 06 00") // state | 人物 | 隊伍顏色

    // CUser::AppendSimpleInfoPacket
    EncodeStr("chui") // name
    Encode1()
    Encode2(5) // level
    Encode4()
    Encode4()
    Encode2() // guildMark

    // CBaseSession::MakeSlotAccessoryPacket

    // SlotDecos 佈景裝飾等
    val SlotDecosSize = 0
    Encode1(SlotDecosSize)
    if (SlotDecosSize > 0) {
        Encode1()
        repeat(SlotDecosSize) {
            Encode1() // 1 | 4 | 6
            Encode2() // 70 | 55 | 20
        }
    }

    // CharDecos 角色裝備
    val CharDecosSize = 0
    Encode1(CharDecosSize)
    if (CharDecosSize > 0) {
        Encode1()
        repeat(SlotDecosSize) {
            Encode4()
            Encode4()
            Encode4()
        }
    }

    // CUser::GetInventoryLoaded(pUser, 14)
    Encode4()
}