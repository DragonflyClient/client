package net.inceptioncloud.dragonfly.versioning

/**
 * Represents a version of the Dragonfly client using the default versioning scheme.
 */
data class Version(
    val major: Int,
    val minor: Int,
    val patch: Int,
    val build: Int
) {
    /**
     * Converts the version to a list of all four version parts.
     */
    fun toVersionParts(): List<Int> = listOf(major, minor, patch, build)

    /**
     * Converts the version to a string using the default scheme.
     */
    override fun toString(): String {
        return "v$major.$minor.$patch.$build"
    }
}