package pingu.handler.room

import pingu.netty.PKTHandler

import pingu.packet.room.MovableBoxMove
import pingu.server.Room

val MovableBoxMoveReq = PKTHandler { c ->
    Room BC MovableBoxMove()
}