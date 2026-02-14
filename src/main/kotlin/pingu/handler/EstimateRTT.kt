package pingu.handler

import pingu.netty.Decode4
import pingu.netty.PKTHandler
import pingu.netty.send
import pingu.packet.ResEstimateRTT
import pingu.tickCount

//  server = CUserPool::OnEstimateRTT
val EstimateRTT = PKTHandler { c ->
    val sequence = Decode4

    c send ResEstimateRTT(sequence, tickCount)
}