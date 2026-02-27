package pingu.server

import io.netty.channel.EventLoop
import io.netty.util.concurrent.ScheduledFuture
import pingu.isJP
import pingu.netty.ClientSocket
import pingu.netty.NettyServer.workerGroup
import pingu.netty.PKT
import pingu.packet.room.*
import java.util.concurrent.TimeUnit
import kotlin.random.Random

// 之後改用class
object Room {
    private val executor: EventLoop = workerGroup.next()
    private var tickFuture: ScheduledFuture<*>? = null
    private var nextBombId = 0
    val slots = Array(8) { Slot() }
    var mapId = 89
    val bombs = mutableListOf<Bomb>()
    var lastAirplaneTime = 0L

    val activeSlotCount: Int
        get() = slots.count { it.user != null || it.isAI }

    val uniqueClients: Sequence<ClientSocket>
        get() = slots.asSequence()
            .mapNotNull { it.user?.c } // 取得所有slot中有玩家的 ClientSocket
            .distinctBy { it.ch.id() } // 過濾掉 2P 的重複連線 對2P只會發一次

    fun startGame() {
        // 每 200ms 一次
        tickFuture = executor.scheduleAtFixedRate(::onTick, 0, 200, TimeUnit.MILLISECONDS)

        // temp fix jp crash
        if (isJP)
            mapId = 118

        val spawnPositions = (0..7).shuffled()
        val random = Random.nextInt(9999)
        BC(
            LaunchGameStage(spawnPositions),
            AirplaneItemProb(),

            DoStartGame(random)
        )
    }

    // CGameSession::Update
    private fun onTick() {
        val now = System.currentTimeMillis()
        val stateChanged = mutableListOf<Bomb>()

        bombs.removeIf { bomb ->
            if (bomb.updateState(now)) {
                stateChanged.add(bomb)
            }
            bomb.shouldRemove()
        }

        if (stateChanged.isNotEmpty()) {
            BC(SetBombState(stateChanged))
        }
    }

    fun addBomb(slotId: Int, pos: Int, bombAttr: Int, isSpecial: Boolean, unk: Int) {
        executor.execute {
            val bombId = nextBombId++
            bombs += Bomb(bombId, slotId, pos, bombAttr, isSpecial)
            BC(
                BombIgnite(slotId, pos, bombAttr, bombId, unk)
            )
        }
    }

    fun getBomb(bombId: Int): Bomb? {
        return bombs.find { it.id == bombId }
    }

    fun BC(vararg packets: PKT) {
        uniqueClients.forEach { client ->
            packets.forEach { client.ctx.write(it) }
            client.ctx.flush()
        }
    }

    infix fun BC(packet: PKT) {
        uniqueClients.forEach { client ->
            client.ctx.writeAndFlush(packet)
        }
    }

    fun encodeSlots(c: ClientSocket) {
        slots.forEachIndexed { index, slot ->
            val user = slot.user
            if (user != null) {
                c send SetPlayer(index, slot, user)
            } else {
                c send SetSlotState(index, slot)
            }
        }
    }

    fun endGame() {
    }

    fun reset() {
        tickFuture?.cancel(false)
        bombs.clear()
        nextBombId = 0
        lastAirplaneTime = 0L
    }
}