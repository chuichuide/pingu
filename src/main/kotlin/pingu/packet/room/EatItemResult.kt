package pingu.packet.room

import pingu.netty.PKT

// server = CGameSession::OnRequestEatItem
fun EatItemResult(slotId: Int, ItemID: Int, ItemType: Int) = PKT {
    Encode1(slotId)
    Encode1(ItemID)
    Encode1(ItemType)

    // 這邊還有東西 if才會Encode
//    Encode1()
}

fun EatItemResult_JP(slotId: Int, ItemID: Int, ItemType: Int, v4: Int) = PKT {
    Encode1(slotId)
    Encode1(ItemType)
    Encode1(v4)
    Encode1(ItemID)

    Encode1()
    // 這邊還有東西 if才會Encode
//    Encode1()
}