package net.inceptioncloud.dragonfly.mods

import net.inceptioncloud.dragonfly.engine.internal.Alignment
import net.inceptioncloud.dragonfly.engine.internal.WidgetColor
import net.inceptioncloud.dragonfly.mods.core.DragonflyMod

object HotkeysMod : DragonflyMod("Hotkeys") {

    var color by option { WidgetColor(0, 0, 0, 0) }
    var size by option { 0.0 }
    var number by option { 0 }
    var text by option { "Huhu World!" }
    var enabled by option { true }
    var alignment by option { Alignment.CENTER }
}