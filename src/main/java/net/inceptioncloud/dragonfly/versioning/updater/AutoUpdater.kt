package net.inceptioncloud.dragonfly.versioning.updater

import net.inceptioncloud.dragonfly.versioning.DragonflyVersion
import net.inceptioncloud.dragonfly.versioning.DragonflyVersion.update
import org.apache.logging.log4j.LogManager
import java.io.File

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
        val updater = getFolder() + "Dragonfly${File.separator}Dragonfly-Updater.jar"
        val programArguments = mutableListOf("--version=${DragonflyVersion.remoteVersion.toString()}")

        if (update?.requiresInstaller == true) {
            programArguments.add("--requireInstaller")
        }

        val command = "java -jar $updater ${programArguments.joinToString(" ")}"
        LogManager.getLogger().info("Launching auto-updater with command $command")

        val process = Runtime.getRuntime().exec(command)

        process.inputStream.reader().useLines {
            it.forEach { line -> LogManager.getLogger().info("Updater: $line") }
        }

        LogManager.getLogger().info("Updater finished with exit code " + process.waitFor())
    }

    private fun getFolder(): String {
        when {
            System.getProperty("os.name").toLowerCase().contains("windows") -> {
                return System.getenv("appdata") + File.separator
            }
            System.getProperty("os.name").toLowerCase().contains("linux") -> {
                return System.getProperty("user.home") + File.separator
            }
            System.getProperty("os.name").toLowerCase().contains("os") -> {
                return "/Users/" + System.getProperty("user.name") + "/Library/Application Support/"
            }
        }
        return "ERROR"
    }

}