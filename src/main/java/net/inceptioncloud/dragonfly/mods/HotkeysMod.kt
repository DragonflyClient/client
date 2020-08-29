package net.inceptioncloud.dragonfly.mods

import net.inceptioncloud.dragonfly.apps.modmanager.controls.BooleanControl
import net.inceptioncloud.dragonfly.apps.modmanager.controls.ButtonControl
import net.inceptioncloud.dragonfly.apps.modmanager.controls.ControlElement
import net.inceptioncloud.dragonfly.apps.modmanager.controls.TitleControl
import net.inceptioncloud.dragonfly.engine.internal.Alignment
import net.inceptioncloud.dragonfly.engine.internal.WidgetColor
import net.inceptioncloud.dragonfly.mods.core.DragonflyMod

object HotkeysMod : DragonflyMod("Hotkeys") {

    var enabled by option(true)

    override fun publishControls(): List<ControlElement<*>> = listOf(
        TitleControl("General"),
        BooleanControl(::enabled, "Enable mod"),
        TitleControl("Hotkeys", "List of all hotkeys"),
        ButtonControl("STRG + G","Hier könnte ihre Werbung stehen - +49 1574 2525642")
    )

}