package net.inceptioncloud.dragonfly.versioning

/**
 * Represents a version status delivered by the Inception Cloud API Services.
 *
 * @param version the version string
 * @param patchNotes a link to the patch notes (nullable)
 * @param requiresInstaller whether the installer is required to download the version
 */
data class VersionStatus(
    val version: String,
    val patchNotes: String?,
    val requiresInstaller: Boolean
)