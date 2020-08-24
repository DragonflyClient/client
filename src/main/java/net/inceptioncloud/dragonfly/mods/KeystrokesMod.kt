package net.inceptioncloud.dragonfly.mods

import net.inceptioncloud.dragonfly.apps.modmanager.controls.*
import net.inceptioncloud.dragonfly.mods.core.DragonflyMod

object KeystrokesMod : DragonflyMod("Keystrokes") {

    var enabled by option { true }
    var enabled2 by option { true }
    var enabled3 by option { true }

    override fun publishControls(): List<ControlElement<*>> = listOf(
        TitleControl("General", "General settings for the keystrokes mod"),
        BooleanControl(::enabled, "Enable mod"),
        BooleanControl(::enabled2, "Enable mod #2", "Do this to enable the mod"),
        BooleanControl(::enabled3, "Enable mod #3")
    )
}