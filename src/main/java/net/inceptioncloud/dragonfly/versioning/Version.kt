package net.inceptioncloud.dragonfly.versioning

import net.inceptioncloud.dragonfly.utils.Keep

/**
 * Represents a version of the Dragonfly client using the default versioning scheme.
 */
@Keep
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
        return "$major.$minor.$patch.$build"
    }

    companion object {

        /**
         * Constructs a version based on a string by splitting it using a dot as delimiter, mapping the parts
         * to integers (or nulls) and passing them to the other [of] function that takes a list of nullable
         * integers.
         */
        fun of(string: String): Version? {
            val split = string.split(".")
            return of(split.map { it.toIntOrNull() })
        }

        /**
         * Constructs a version based on a list of nullable integers. Non-null checks are performed inside the
         * function. Returns null if the size of the [parts] is not exactly 4 or if it contains elements that
         * are null.
         */
        fun of(parts: List<Int?>): Version? {
            return if (parts.size == 4 && parts.none { it == null }) {
                val nonNull = parts.requireNoNulls() // save because of the check above
                Version(nonNull[0], nonNull[1], nonNull[2], nonNull[3])
            } else null
        }
    }
}