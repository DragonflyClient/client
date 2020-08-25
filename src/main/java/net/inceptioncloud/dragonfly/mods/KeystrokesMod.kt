package net.inceptioncloud.dragonfly.mods

import net.inceptioncloud.dragonfly.apps.modmanager.controls.*
import net.inceptioncloud.dragonfly.mods.core.DragonflyMod

object KeystrokesMod : DragonflyMod("Keystrokes") {

    var enabled by option(true)
    var space by option(2.0) { it in 0.0..1.0 }
    var percent by option(50) { it in 0..100 }

    override fun publishControls(): List<ControlElement<*>> = listOf(
        TitleControl("General"),
        BooleanControl(::enabled, "Enable mod"),
        TitleControl("Appearance", "Customize the appearance of the keystrokes on your screen"),
        NumberControl(::space, "Space", "The space between the keystroke boxes", min = 0.0, max = 1.0, decimalPlaces = 2, liveUpdate = true),
        NumberControl(::percent, "Size", "The size in percent", min = 0.0, max = 100.0, decimalPlaces = 0, formatter = { "$it%" })
    )
}