package pingu.handler.shop

import pingu.netty.PKTHandler
import pingu.packet.shop.FxStockInit

// server = CMainPool::OnRequestShopItemList | CItemMan::UpdateShopItemPacket
val AskFxStock = PKTHandler { c ->
    c send FxStockInit()
}