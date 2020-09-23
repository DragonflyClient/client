package net.inceptioncloud.dragonfly.mods.togglesneak

import net.inceptioncloud.dragonfly.controls.*
import net.inceptioncloud.dragonfly.mods.core.DragonflyMod

object ToggleSneakMod : DragonflyMod("ToggleSneak") {

    var enabledSneak by option(true)
    var enabledSprint by option(true)
    var doSneak = false
    var doSprint = false

    override fun publishControls(): List<ControlElement<*>> = listOf(
        TitleControl("General"),
        BooleanControl(ToggleSneakMod::enabledSneak, "Enable ToggleSneak"),
        BooleanControl(ToggleSneakMod::enabledSprint, "Enable ToggleSprint")
    )

}