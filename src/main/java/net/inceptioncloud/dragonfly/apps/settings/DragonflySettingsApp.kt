package net.inceptioncloud.dragonfly.apps.settings

import net.inceptioncloud.dragonfly.options.sections.*
import net.inceptioncloud.dragonfly.ui.taskbar.TaskbarApp
import net.minecraft.client.Minecraft

object DragonflySettingsApp : TaskbarApp("Dragonfly Settings") {

    init {
        DragonflyOptions

        OptionsSectionClient.init()
        OptionsSectionZoom.init()
        OptionsSectionScoreboard.init()
        OptionsSectionPlayer.init()
        OptionsSectionOverlay.init()
        OptionsSectionChat.init()
        OptionsSectionPerformance.init()
    }

    override fun open() = gui(DragonflySettingsUI(Minecraft.getMinecraft().currentScreen))
}