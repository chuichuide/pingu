package pingu.netty

import io.github.classgraph.ClassGraph
import io.netty.util.internal.StringUtil.substringBefore
import pingu.Ver
import pingu.loadYaml
import pingu.locale
import pingu.netty.HandlerRouter.handlerArray
import java.util.concurrent.ConcurrentHashMap
import kotlin.jvm.java

object OpcodeManager {
    val recvOps: Map<Int, String>
    private val sendOps: Map<String, Int>
    private val sendOpCache = ConcurrentHashMap<Class<*>, Int>()

    init {
        //  載入 YAML
        val recvYaml: Map<String, Int> = loadYaml("recv_${locale}_${Ver}")
        val sendYaml: Map<String, Int> = loadYaml("send_${locale}_${Ver}")

        // 掃描
        ClassGraph()
            .enableAllInfo()
            .ignoreFieldVisibility()
            .overrideClassLoaders(Thread.currentThread().contextClassLoader)
            .acceptPackages("pingu.handler")
            .scan().use { scanResult -> // use 區塊自動關閉資源

                // --- 處理 Handler (接收) ---
                val handlerType = PKTHandler::class.java.name

                // 使用 Sequence 處理過濾與映射，邏輯更清晰
                scanResult.allClasses.asSequence()
                    .flatMap { it.fieldInfo }
                    .filter { it.typeSignatureOrTypeDescriptor.toString() == handlerType }
                    .forEach { info ->
                        val op = recvYaml[info.name] ?: return@forEach

                        if (op in handlerArray.indices) {
                            // 直接獲取靜態欄位值並轉型
                            val handler = info.loadClassAndGetField().apply { isAccessible = true }.get(null)
                            handlerArray[op] = handler as? PKTHandler
                        } else {
                            System.err.println("Opcode $op (${info.name}) Out of bounds")
                        }
                    }
            }

        // 賦值
        recvOps = recvYaml.entries.associate { (k, v) -> v to k }
        sendOps = sendYaml

        println("初始化完成: 綁定 ${handlerArray.count { it != null }} 個 Handler")
    }

    // 獲取 Opcode (含快取)
    fun getSendOp(clazz: Class<*>): Int {
        return sendOpCache.getOrPut(clazz) {
            val filteredName = clazz.name.substringAfter('$').substringBefore('$').substringBefore('_')
//            val filteredName = clazz.name.substringAfter('$').substringBefore('$')

            sendOps[filteredName] ?: error("Opcode for '$filteredName' not found in YAML")
        }
    }
}