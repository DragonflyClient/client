package net.inceptioncloud.dragonfly.subscriber

import com.google.common.eventbus.Subscribe
import net.inceptioncloud.dragonfly.apps.accountmanager.AccountManagerApp
import net.inceptioncloud.dragonfly.event.client.ClientShutdownEvent
import net.inceptioncloud.dragonfly.mods.hotkeys.HotkeysMod
import net.minecraft.client.Minecraft
import org.apache.logging.log4j.LogManager
import java.io.File

object ShutdownSubscriber {
    @Subscribe
    fun clientShutdown(event: ClientShutdownEvent) {
        val temporaryDirectory = File(Minecraft.getMinecraft().mcDataDir, "temp")

        if (temporaryDirectory.exists()) {
            temporaryDirectory.deleteRecursively()
            LogManager.getLogger().info("The temporary directory has been deleted")
        }

        HotkeysMod.controller.commit()
        AccountManagerApp.storeAccounts()
    }
}