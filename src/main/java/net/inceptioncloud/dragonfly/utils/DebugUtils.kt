package net.inceptioncloud.dragonfly.utils

import org.apache.logging.log4j.LogManager

private val recentValue = mutableMapOf<String, Any?>()

fun smartLog(key: String, value: Any?) {
    val recent = recentValue[key]
    if (recent != value) {
        LogManager.getLogger().info("$key = $value")
        recentValue[key] = value
    }
}