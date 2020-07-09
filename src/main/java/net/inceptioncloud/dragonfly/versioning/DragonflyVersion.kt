package net.inceptioncloud.dragonfly.versioning

import com.google.gson.JsonParser
import org.apache.logging.log4j.LogManager
import java.io.File
import java.lang.IllegalStateException
import java.net.URL

/**
 * Stores the local and remote version of Dragonfly and compares them considering the selected
 * [UpdateChannel].
 */
object DragonflyVersion {

    /**
     * The version of this client.
     */
    val localVersion = Version(1, 0, 0, 0)

    /**
     * The version that is specified as the latest one, fetched lazily from the Inception Cloud
     * content delivery network for Dragonfly.
     */
    val remoteVersion: Version? by lazy { getRemoteVersionString()?.let { Version.of(it) } }

    /**
     * The string representation of the [localVersion].
     */
    @JvmStatic
    val string = "v$localVersion"

    /**
     * The location of the Dragonfly local storage in the appdata directory.
     */
    private val localStorage = System.getenv("appdata") + "\\Dragonfly"

    /**
     * Compares the [first] version to the [second] version based on the [channel][getChannel].
     *
     * While the [UpdateChannel.STABLE] only compares the major, minor and build parts of the
     * version, the [UpdateChannel.EARLY_ACCESS_PROGRAM] also uses the patch part for comparison.
     */
    fun compareVersions(first: Version, second: Version): Int? {
        return when (getChannel() ?: throw IllegalStateException(
            "No update channel set in installation_properties.json!"
        )) {
            UpdateChannel.STABLE -> compareVersionsStable(first, second)
            UpdateChannel.EARLY_ACCESS_PROGRAM -> compareVersionsEap(first, second)
        }
    }

    /**
     * Compares the local to the remote version dropping the patch part of the version.
     */
    private fun compareVersionsStable(first: Version, second: Version): Int = compareVersionParts(
        first.toVersionParts().toMutableList().apply { removeAt(2) },
        second.toVersionParts().toMutableList().apply { removeAt(2) }
    )

    /**
     * Compares the local to the remote version.
     */
    private fun compareVersionsEap(first: Version, second: Version): Int = compareVersionParts(
        first.toVersionParts(),
        second.toVersionParts()
    )

    /**
     * Compares the given parts of the local and remote version and returns
     *
     * - 1 if the local version is newer
     * - 0 if the versions are identical
     * - -1 if the remote version is newer
     *
     * based on the [channel][getChannel].
     */
    private fun compareVersionParts(local: List<Int>, remote: List<Int>): Int {
        for (index in local.indices) {
            val l = local[index]
            val r = remote[index]

            if (l < r) {
                return -1
            } else if (l > r) {
                return 1
            }
        }

        return 0
    }

    /**
     * Returns the [UpdateChannel] determined in the `dragonfly/installation_properties.json` or
     * null, if the file doesn't exist or if the identifier is invalid.
     */
    fun getChannel(): UpdateChannel? {
        val properties = File("${localStorage}\\installation_properties.json")
        println(properties)
        return properties.takeIf { it.exists() }?.reader()?.use {
            val json = JsonParser().parse(it)?.asJsonObject
            json?.get("channel")?.asString?.let { channel -> UpdateChannel.getByIdentifier(channel) }
        }
    }

    /**
     * Fetches the remote version from the Inception Cloud content delivery network and returns
     * the string value.
     */
    private fun getRemoteVersionString(): String? {
        return try {
            URL("https://cdn.icnet.dev/dragonfly/version").readText()
        } catch (e: Exception) {
            LogManager.getLogger().warn("Could not fetch remote version!")
            e.printStackTrace()
            null
        }
    }
}