package net.inceptioncloud.dragonfly.hotkeys.types.dataclasses

class HotkeyData(
    val key: Int,
    val modifierKey: Int?,
    val time: Double,
    val delay: Double,
    var color: String
)