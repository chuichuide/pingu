package pingu.server

import io.netty.channel.EventLoop
import io.netty.util.concurrent.ScheduledFuture
import pingu.netty.ClientSocket
import pingu.netty.NettyServer.workerGroup
import pingu.netty.PKT
import pingu.packet.room.*
import java.util.concurrent.TimeUnit
import kotlin.random.Random

// 之後改用class
object Room {
    val executor: EventLoop = workerGroup.next()
    private var tickFuture: ScheduledFuture<*>? = null
    val slots = Array(8) { Slot() }
    var mapId = 89

    //    val nextBombId = AtomicInteger(0)
    var nextBombId = 0
    val bombs = mutableListOf<Bomb>()
    var lastAirplaneTime = 0L

    fun addBomb(slotId: Int, pos: Int, bombAttr: Int, bSpecial: Boolean, unk: Int) {
        executor.execute {
            val bombId = nextBombId++
            bombs += Bomb(bombId, slotId, pos, bombAttr, bSpecial)
            BC(
                BombIgnite(slotId, pos, bombAttr, bombId, unk)
            )
        }
    }

    fun startGane() {
        // 每 500ms 一次
        tickFuture = executor.scheduleAtFixedRate(::onTick, 0, 500, TimeUnit.MILLISECONDS)

        val spawnPositions = (0..7).shuffled()
        val random = Random.nextInt(9999)
        BC(
            LaunchGameStage(spawnPositions),
            AirplaneItemProb(),

            DoStartGame(random)
        )
    }

    private fun onTick() {
        val now = System.currentTimeMillis()

        // 找出所有該爆的炸彈
        val exploded = bombs.filter { it.expireAt <= now }
        if (exploded.isEmpty()) return

        // 移除並觸發爆炸
        bombs.removeAll(exploded)
        exploded.forEach {
            BC(
                SetBombState(it.id)
            )
        }
    }

    fun BC(vararg packets: PKT) {
        executor.execute {
            slots.asSequence()
                .mapNotNull { it.user?.c } // 取得所有slot中有玩家的 ClientSocket
                .distinctBy { it.ch.id() } // 過濾掉 2P 的重複連線 對2P只會發一次
                .forEach { c ->
                    packets.forEach {
                        c.send(it)
                    }
                }
        }
    }

    infix fun BC(packet: PKT) {
        executor.execute {
            slots.asSequence()
                .mapNotNull { it.user?.c } // 取得所有slot中有玩家的 ClientSocket
                .distinctBy { it.ch.id() } // 過濾掉 2P 的重複連線 對2P只會發一次
                .forEach { it.send(packet) }
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
        nextBombId = 0
        bombs.clear()
        lastAirplaneTime = 0L
    }
}