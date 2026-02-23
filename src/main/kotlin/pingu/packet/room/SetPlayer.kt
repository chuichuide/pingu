package pingu.packet.room

import pingu.isJP
import pingu.netty.PKT
import pingu.server.Encode
import pingu.server.Slot
import pingu.server.User

// server = MakeSetPlayerPacket
// clinet = TW_5 = 4B0020
val bEnterEffect =  true
fun SetPlayer(slotId: Int, slot: Slot, user: User) = PKT {
    Encode1(slotId)
    Encode4(user.id) // userId
    Encode1Bool(bEnterEffect)
    Encode1()
    if (bEnterEffect) {
        Encode1(6) // enterEffectId
    }

    slot.Encode(this)

    // CUser::AppendSimpleInfoPacket
    EncodeStr(user.name) // name
    Encode1()
    if (isJP) {
        Encode1()
        Encode2()
    }
    Encode2(user.level) // level
    if (isJP) {
        Encode4()
        EncodeStr()
    }
    Encode4()
    Encode4(user.level) // 這個不是1的話日版會沒地圖選
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