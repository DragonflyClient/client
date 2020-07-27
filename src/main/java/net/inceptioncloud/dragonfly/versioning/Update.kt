package net.inceptioncloud.dragonfly.versioning

/**
 * Represents an update of the Dragonfly client published to the backend.
 */
data class Update(
    /**
     * The version in string-format
     */
    val version: String,

    /**
     * Whether the update requires the installer to be downloaded
     */
    val requiresInstaller: Boolean? = false,

    /**
     * A link to the patch notes or another news page related to the update
     */
    val patchNotes: String?,

    /**
     * The date of the release in milliseconds
     */
    val releaseDate: Long,

    /**
     * The title of the release
     */
    val title: String?
)
