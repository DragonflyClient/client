package net.inceptioncloud.dragonfly.options

import net.inceptioncloud.dragonfly.options.sections.*

object OptionsManager {

    fun loadOptions() {
        Options

        OptionsSectionClient.init()
        OptionsSectionZoom.init()
        OptionsSectionScoreboard.init()
        OptionsSectionPlayer.init()
        OptionsSectionChat.init()
        OptionsSectionPerformance.init()
    }
}