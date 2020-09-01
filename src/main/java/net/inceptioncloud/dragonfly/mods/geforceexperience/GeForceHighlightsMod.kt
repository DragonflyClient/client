package net.inceptioncloud.dragonfly.mods.geforceexperience

import net.inceptioncloud.dragonfly.apps.modmanager.controls.*
import net.inceptioncloud.dragonfly.mods.core.DragonflyMod


object GeForceHighlightsMod : DragonflyMod("GeForce Highlights") {

    var enabled by option(true)

    override fun publishControls(): List<ControlElement<*>> = listOf(
        TitleControl("General"),
        BooleanControl(GeForceHighlightsMod::enabled, "Enable mod")
    )

}