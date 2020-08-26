package net.inceptioncloud.dragonfly.mods

import net.inceptioncloud.dragonfly.apps.modmanager.controls.*
import net.inceptioncloud.dragonfly.engine.internal.Alignment
import net.inceptioncloud.dragonfly.mods.core.DragonflyMod

object KeystrokesMod : DragonflyMod("Keystrokes") {

    var enabled by option(true)
    var space by option(2.0) { it in 0.0..1.0 }
    var alignment by option(Alignment.START)

    override fun publishControls(): List<ControlElement<*>> = listOf(
        TitleControl("General"),
        BooleanControl(::enabled, "Enable mod"),
        TitleControl("Appearance", "Customize the appearance of the keystrokes on your screen"),
        NumberControl(::space, "Space", "The space between the keystroke boxes", min = 0.0, max = 1.0, decimalPlaces = 2, liveUpdate = true),
        DropdownElement(::alignment, "Alignment", "The alignment of the keystroke boxes on the screen")
    )
}