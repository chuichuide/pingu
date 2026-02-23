package pingu.handler

import pingu.netty.PKTHandler

import pingu.packet.EnterLobbyStage

// CChannelPool::OnEnterLobbyStage
val EnterLobbyStageReq = PKTHandler { c ->
    c send EnterLobbyStage()
}