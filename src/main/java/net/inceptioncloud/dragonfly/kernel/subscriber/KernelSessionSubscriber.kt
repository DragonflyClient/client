package net.inceptioncloud.dragonfly.kernel.subscriber

import com.google.common.eventbus.Subscribe
import net.dragonfly.kernel.packets.client.UpdateMinecraftAccountPacket
import net.inceptioncloud.dragonfly.event.client.SessionChangeEvent
import net.inceptioncloud.dragonfly.kernel.KernelClient

/**
 * Listens to the [SessionChangeEvent] and sends an [UpdateMinecraftAccountPacket] to the server
 * to show that the user switched Minecraft accounts.
 */
object KernelSessionSubscriber {

    @Subscribe
    fun onSessionChange(event: SessionChangeEvent) {
        KernelClient.client.sendTCP(UpdateMinecraftAccountPacket(event.session.profile?.id?.toString()))
    }
}