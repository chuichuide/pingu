package pingu.handler

import pingu.netty.PKTHandler
import pingu.packet.UnkResult

// 日版在發完頻道資訊後會發的 回應完這個才會進頻道選擇頁面
val UnkReq = PKTHandler { c ->
    c send UnkResult()
}