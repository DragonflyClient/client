package net.inceptioncloud.dragonfly.options

import com.google.common.eventbus.Subscribe
import net.inceptioncloud.dragonfly.apps.modmanager.ModManagerApp
import net.inceptioncloud.dragonfly.apps.settings.DragonflyOptions
import net.inceptioncloud.dragonfly.event.client.ClientShutdownEvent

/**
 * Saves the options when shutting down the client (via [ClientShutdownEvent]).
 */
object OptionSaveSubscriber {

    /**
     * [ClientShutdownEvent] Subscriber
     */
    @Subscribe
    fun clientShutdown(event: ClientShutdownEvent?) {
        DragonflyOptions.contentSave()
        ModManagerApp.availableMods.forEach { it.optionsBase.contentSave() }
    }
}