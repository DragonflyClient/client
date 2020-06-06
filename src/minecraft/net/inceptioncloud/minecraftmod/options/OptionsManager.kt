package net.inceptioncloud.minecraftmod.options

import net.inceptioncloud.minecraftmod.options.sections.*

object OptionsManager {

    fun loadOptions() {
        Options

        OptionsSectionClient.init()
        OptionsSectionUI.init()
        OptionsSectionPlayer.init()
        OptionsSectionChat.init()
        OptionsSectionScoreboard.init()
        OptionsSectionZoom.init()
    }
}