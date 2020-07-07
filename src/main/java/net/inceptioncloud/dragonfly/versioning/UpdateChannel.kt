package net.inceptioncloud.dragonfly.versioning

/**
 * Represents the two available update channels 'stable' and 'eap'.
 */
enum class UpdateChannel(val identifier: String) {

    /**
     * The stable channel, which delivers only major and minor updates.
     */
    STABLE("stable"),

    /**
     * The early access channel, which delivers major, minor and patch updates.
     */
    EARLY_ACCESS_PROGRAM("eap");

    companion object {
        /**
         * Finds the update channel by its [identifier], which should be either 'stable' or 'eap'. Returns
         * null of no enum constant could be found.
         */
        fun getByIdentifier(identifier: String) = values().firstOrNull { it.identifier == identifier }
    }
}