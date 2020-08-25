package net.inceptioncloud.dragonfly.mods

import net.inceptioncloud.dragonfly.apps.modmanager.controls.*
import net.inceptioncloud.dragonfly.mods.core.DragonflyMod

object KeystrokesMod : DragonflyMod("Keystrokes") {

    var enabled by option(true)
    var space by option(2.0) { it in 1.0..3.0 }

    override fun publishControls(): List<ControlElement<*>> = listOf(
        TitleControl("General"),
        BooleanControl(::enabled, "Enable mod"),
        TitleControl("Appearance", "Customize the appearance of the keystrokes on your screen"),
        NumberControl(::space, "Space", "The spacing between multiple keystrokes boxes", 1.0, 3.0, 1)
    )
}