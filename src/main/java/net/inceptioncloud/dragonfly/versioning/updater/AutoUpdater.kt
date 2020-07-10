package net.inceptioncloud.dragonfly.versioning.updater

import net.inceptioncloud.dragonfly.versioning.DragonflyVersion
import net.inceptioncloud.dragonfly.versioning.DragonflyVersion.versionStatus
import org.apache.logging.log4j.LogManager

/**
 * A bridge to the Dragonfly auto updater.
 */
object AutoUpdater {

    /**
     * Checks whether an update for Dragonfly is available is available.
     */
    fun isUpdateAvailable(): Boolean {
        val remote = DragonflyVersion.remoteVersion
        val local = DragonflyVersion.localVersion

        if (remote == null) {
            LogManager.getLogger().warn("Could not check for update since the remote version is unknown!")
            return false
        }

        return when (DragonflyVersion.compareVersionsPlain(local, remote)) {
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

    /**
     * Launches the updater from the Dragonfly local storage.
     */
    fun update() {
        val updater = System.getenv("appdata") + "\\Dragonfly\\Dragonfly-Updater.jar"
        val programArguments = mutableListOf("--version=${DragonflyVersion.remoteVersion.toString()}")

        if (versionStatus?.requiresInstaller == true) {
            programArguments.add("--requireInstaller")
        }

        LogManager.getLogger().info("Launching auto-updater and terminating Dragonfly...")
        Runtime.getRuntime().exec("java -jar $updater ${programArguments.joinToString(" ")}")
    }
}