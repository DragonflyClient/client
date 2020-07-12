package net.inceptioncloud.build.update

import com.jcraft.jsch.*
import khttp.structures.authorization.BasicAuthorization
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import java.io.File

/**
 * Publishes a new update for Dragonfly.
 *
 * This can be a major, minor, patch or build update. The task is able to upload the files directly
 * to the Inception Cloud Content Delivery Network and change the current version that is requested
 * by the Inception Cloud API services.
 */
open class PublishTask : DefaultTask() {

    /**
     * Whether this update should be published to the early access channel specified via the command
     * line argument `--eap`.
     */
    @set:Option(
        option = "eap",
        description = "Whether this update should be published to the early access channel"
    )
    var earlyAccess: Boolean = false

    /**
     * Whether this update should be published to the stable channel specified via the command line
     * argument `--stable`.
     */
    @set:Option(
        option = "stable",
        description = "Whether this update should be published to the stable channel"
    )
    var stable: Boolean = false

    /**
     * Whether this update requires the installer to be downloaded specified via the command line
     * argument `--requireInstaller`.
     */
    @set:Option(
        option = "requireInstaller",
        description = "Whether this update requires the installer to be downloaded"
    )
    var requiresInstaller: Boolean = false

    /**
     * The link to the patch notes or any other news resource.
     */
    @set:Option(
        option = "patchNotes",
        description = "The link to the patch notes or any other news resource"
    )
    var patchNotes: String? = null

    /**
     * The directory which the Inception Cloud Secrets are stored in.
     */
    private val secrets = getSecretsDirectory()

    /**
     * The main execution function of the publish task.
     */
    @TaskAction
    fun execute() {
        val outputName = "${project.name}-fat-${project.version}.jar"
        val host = readSecret("sftp_host")
        val username = readSecret("sftp_user")
        val password = readSecret("sftp_password")
        val cdn = "/var/www/cdn/dragonfly/${VersionTask.globalVersion}"

        val jsch = JSch().apply { setKnownHosts("$secrets/known_hosts") }
        val jschSession: Session = jsch.getSession(username, host)
            .apply {
                setPassword(password)
                connect()
            }

        logger.info("Uploading to Content Delivery Network...")
        val channel = jschSession.openChannel("sftp") as ChannelSftp
        channel.connect()

        try {
            channel.mkdir(cdn)
        } catch (e: SftpException) {
            logger.info("Content Delivery Network directory does already exist, recreating...")
            channel.rm("$cdn/Dragonfly-1.8.8.jar")
            channel.rm("$cdn/Dragonfly-1.8.8.json")
            channel.rmdir(cdn)
            channel.mkdir(cdn)
        }

        channel.put("/build/libs/$outputName", "$cdn/Dragonfly-1.8.8.jar")
        channel.put("/resources/Dragonfly-1.8.8.json", "$cdn/Dragonfly-1.8.8.json")
        channel.exit()
        logger.info("Upload succeeded!")

        if (!earlyAccess && !stable) {
            logger.warn("Neither early access nor stable channel was selected. No version is published!")
            return
        }

        val json = mapOf(
            "version" to VersionTask.globalVersion,
            "requiresInstaller" to requiresInstaller,
            "patchNotes" to patchNotes,
            "releaseDate" to System.currentTimeMillis()
        )

        val urls = mutableListOf<String>().apply {
            if (earlyAccess) add("eap")
            if (stable) add("stable")
        }

        for (url in urls) {
            val response = khttp.post(
                url = "https://api.inceptioncloud.net/publish/$url",
                auth = BasicAuthorization(readSecret("api_user"), readSecret("api_password")),
                json = json
            )

            if (response.statusCode != 200) {
                throw java.lang.IllegalStateException("Failed to publish update: $response")
            }
        }
    }

    /**
     * Returns the value of the secrets environment variable or throws an exception if it doesn't
     * exist.
     */
    private fun getSecretsDirectory(): String = System.getenv("INCEPTIONCLOUD_SECRETS")
        ?: throw IllegalStateException("Environment variable INCEPTIONCLOUD_SECRETS must be set")

    /**
     * Reads a secret file with the given [name] and returns the content as a string.
     */
    private fun readSecret(name: String): String = File("$secrets\\$name").readText()
}