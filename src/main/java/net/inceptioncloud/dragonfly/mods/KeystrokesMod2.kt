package net.inceptioncloud.dragonfly.mods

import net.inceptioncloud.dragonfly.apps.modmanager.controls.*
import net.inceptioncloud.dragonfly.mods.core.DragonflyMod

object KeystrokesMod2 : DragonflyMod("Keystrokes2") {

    var enabled by option(true)
    var scale by option(15.0) { it in 10.0..20.0 }
    var fontSize by option(15.0) { it in 10.0..20.0 }
    var space by option(3.0) { it in 0.0..5.0 }

    override fun publishControls(): List<ControlElement<*>> = listOf(
        TitleControl("General"),
        BooleanControl(::enabled, "Enable mod"),
        TitleControl("Appearance", "Customize the appearance of the keystrokes on your screen"),
        NumberControl(::scale, "Scale", "The scale of the keystroke boxes", min = 10.0, max = 20.0, decimalPlaces = 2, liveUpdate = true),
        NumberControl(::fontSize, "FontSize", "The size of the text in the keystroke boxes", min = 10.0, max = 20.0, decimalPlaces = 2, liveUpdate = true),
        NumberControl(::space, "Space", "The space between the keystroke boxes", min = 0.0, max = 5.0, decimalPlaces = 2, liveUpdate = true)
    )
}