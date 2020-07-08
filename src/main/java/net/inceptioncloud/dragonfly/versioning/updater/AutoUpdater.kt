package net.inceptioncloud.dragonfly.versioning.updater

import net.inceptioncloud.dragonfly.versioning.DragonflyVersion
import org.apache.logging.log4j.LogManager

object AutoUpdater {

    fun isUpdateAvailable(): Boolean {
        val remote = DragonflyVersion.remoteVersion
        val local = DragonflyVersion.localVersion

        if (remote == null) {
            LogManager.getLogger().warn("Could not check for update since the remote version is unknown!")
            return false
        }

        return when (DragonflyVersion.compareVersions(local, remote)) {
            1 -> {
                LogManager.getLogger().info("The local version ($local) is ahead of the remote version ($remote)!")
                false
            }
            0 -> {
                LogManager.getLogger().info("The current version and the remote version are identical ($local)!")
                false
            }
            else -> {
                LogManager.getLogger().info("The local version ($local) is behind the remote version ($remote)!")
                true
            }
        }
    }

    fun update() {
        LogManager.getLogger().info("Launching auto-updater and terminating Dragonfly...")

        val process = Runtime.getRuntime().exec("java -jar dragonfly/Dragonfly-Updater.jar")
        val input = process.inputStream

        input.reader().use {
            val choice = readLine()
            println("Result of auto updater: $choice")
        }
    }
}