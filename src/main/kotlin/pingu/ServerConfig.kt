package pingu

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.decodeFromStream
import java.io.File
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

private val confMap: Map<String, String> = loadYaml("Config")

val locale = Region.valueOf(confMap["Locale"] ?: error("Config.yaml 缺少 Locale 設定"))

val Ver by lazy {
    confMap["${locale}_VER"]?.toInt()
        ?: error("找不到 ${locale} 的版本號 (XX_VER)")
}

val ACP: Charset = when (locale) {
    Region.JP -> Charset.forName("MS932")
    Region.TW -> Charset.forName("MS950")
    Region.TH -> Charset.forName("MS874")
    Region.VN -> Charset.forName("CP1258")
    else -> StandardCharsets.UTF_8
}

val isJP = locale == Region.JP
val isTW = locale == Region.TW
val isTH = locale == Region.TH
val isVN = locale == Region.VN
val isNA = locale == Region.NA

//val debugMode = true
val debugMode = true
val showDecValue = false

inline val tickCount
    get() = System.currentTimeMillis().toInt()

enum class Region {
    JP, TW, TH, VN, NA
}

inline fun <reified T> loadYaml(fileName: String): T {
    return File(fileName + ".yaml").inputStream().use {
        Yaml.default.decodeFromStream(it)
    }
}