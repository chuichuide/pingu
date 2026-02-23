package pingu.netty

import io.github.classgraph.ClassGraph
import pingu.Ver
import pingu.loadYaml
import pingu.locale
import java.util.IdentityHashMap

object OpcodeManager {
    val recvOps: Map<Int, String>
    private val sendOps: Map<String, Int>
    val sendOpCache = IdentityHashMap<Class<*>, Int>(1000)
//    private val sendOpCache = ConcurrentHashMap<Class<*>, Int>()

    init {
        //  載入 YAML
        val recvYaml: Map<String, Int> = loadYaml("${locale}_${Ver}_recv")
        val sendYaml: Map<String, Int> = loadYaml("${locale}_${Ver}_send")

        val pktInterface = PKT::class.java.name
        val handlerType = PKTHandler::class.java.name

        // 掃描
        ClassGraph()
            .enableAllInfo()
            .ignoreFieldVisibility()
//            .acceptPackages("pingu.handler")
            .acceptPackages("pingu.handler", "pingu.packet")
            .scan().use { scanResult -> // use 區塊自動關閉資源

                scanResult.allClasses.forEach { classInfo ->
                    // 處理 Handler (接收) ---
                    classInfo.fieldInfo
                        .filter { it.typeSignatureOrTypeDescriptor.toString() == handlerType }
                        .forEach { fieldInfo ->
                            recvYaml[fieldInfo.name]?.let { op ->
                                if (op in handlerArray.indices) {
                                    val field = fieldInfo.loadClassAndGetField().apply { isAccessible = true }
                                    handlerArray[op] = field.get(null) as? PKTHandler
                                }
                            }
                        }

                    // 處理 Packet (發送) ---將所有 Packet Class 對應到 Opcode
                    if (classInfo.implementsInterface(pktInterface)) {
                        val clazz = classInfo.loadClass()
                        val filteredName = clazz.name.substringAfter('$').substringBefore('$').substringBefore('_')
                        sendYaml[filteredName]?.let { op ->
                            sendOpCache[clazz] = op
                        }
                    }
                }
            }

        // 賦值
        recvOps = recvYaml.entries.associate { (k, v) -> v to k }
        sendOps = sendYaml

//        println("初始化完成: 綁定 ${handlerArray.count { it != null }} 個 Handler")
        println("初始化完成: 綁定 ${handlerArray.count { it != null }} 個 Handler, 註冊 ${sendOpCache.size} 個發送包")
    }

    // 獲取 Opcode (含快取)
    fun getSendOp(clazz: Class<*>): Int = sendOpCache[clazz] ?: -1
/*    fun getSendOp(clazz: Class<*>): Int {
        return sendOpCache.getOrPut(clazz) {
            val filteredName = clazz.name.substringAfter('$').substringBefore('$').substringBefore('_')
            sendOps[filteredName] ?: error("Opcode for '$filteredName' not found in YAML")
        }
    }*/
}