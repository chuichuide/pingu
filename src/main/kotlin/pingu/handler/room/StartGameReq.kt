package pingu.handler.room

import pingu.netty.PKTHandler
import pingu.packet.room.StartGameResult
import pingu.server.Room

// server = CGameSession::OnRequestStartGame
val StartGameReq = PKTHandler { c ->
    val res = 0
    c send StartGameResult(res)

    Room.startGame()
}