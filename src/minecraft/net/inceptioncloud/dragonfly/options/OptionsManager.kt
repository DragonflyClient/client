package net.inceptioncloud.dragonfly.options

import net.inceptioncloud.dragonfly.options.sections.*

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