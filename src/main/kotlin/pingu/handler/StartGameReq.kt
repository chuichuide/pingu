package pingu.handler

import pingu.netty.PKTHandler
import pingu.netty.send
import pingu.packet.DoStartGame
import pingu.packet.LaunchGameStage
import pingu.packet.StartGameResult

// server =  CGameSession::OnRequestStartGame
val StartGameReq = PKTHandler { c ->
    c.send(
        StartGameResult(0),
        LaunchGameStage(),

        DoStartGame()
    )
}