package pingu.packet

import pingu.netty.PKT

// server = CUserPool::OnEstimateRTT
fun ResEstimateRTT(sequence: Int, time: Int) = PKT {
    Encode4(sequence)
    Encode4(time)
}