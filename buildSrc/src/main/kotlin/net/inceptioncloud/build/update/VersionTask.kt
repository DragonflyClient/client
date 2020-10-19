package net.inceptioncloud.build.update

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import java.io.File

/**
 * Changes the version of Dragonfly in the build script and the client source code.
 */
open class VersionTask : DefaultTask() {

    /**
     * The version of the release that can be specified via the command line argument `--new`.
     */
    @set:Option(
        option = "new",
        description = "The version of the release in the format MAJOR.MINOR.PATCH.BUILD"
    )
    var newVersion: String? = null

    /**
     * The main execution function of the version task.
     */
    @TaskAction
    fun execute() {
        requireNotNull(newVersion) { "A version for the update must be specified" }
        writeVersionToBuildScript()
        writeVersionToClientSource()
        globalVersion = newVersion
        project.version = newVersion!!
    }

    /**
     * Overrides the build script file changing the version to the new one.
     */
    private fun writeVersionToBuildScript() {
        val buildScript = File("build.gradle.kts")
        val content = buildScript.readText()
            .replace("version = \"${project.version}\"", "version = \"$newVersion\"")
        buildScript.writeText(content)
    }

    /**
     * Overrides the DragonflyVersion.kt file changing the version to the new one.
     */
    private fun writeVersionToClientSource() {
        val clientVersion = File("src/main/java/net/inceptioncloud/dragonfly/versioning/DragonflyVersion.kt")
        val content = clientVersion.readText()
            .replace(
                "localVersion = Version(${project.version.toString().replace(".", ", ")})",
                "localVersion = Version(${newVersion!!.replace(".", ", ")})"
            )
        clientVersion.writeText(content)
    }

    companion object {
        /**
         * A global variable that represents the version of the current update.
         */
        var globalVersion: String? = null
    }
}