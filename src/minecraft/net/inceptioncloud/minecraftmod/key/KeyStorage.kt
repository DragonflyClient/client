package net.inceptioncloud.minecraftmod.key

import java.io.File

/**
 * Manages the storing and reading of the key used for the client.
 */
object KeyStorage {

    /**
     * The file in which the key is stored
     * */
    private val keyFile = File("inceptioncloud/alpha.key")

    /**
     * Returns whether a valid key is stored in the [keyFile] by checking if the result
     * of [getStoredKey] is not null.
     */
    fun isKeySaved(): Boolean = getStoredKey() != null

    /**
     * Returns the key stored in the [keyFile] as a string. If the file doesn't exist or if
     * the key is corrupted (= doesn't match the regex), this function will return null.
     */
    fun getStoredKey(): String? = keyFile.takeIf { it.exists() }
        ?.readText()?.takeIf { it.matches(Regex("[0-9A-Z]{6}-[0-9A-Z]{8}-[0-9A-Z]{8}-[0-9A-Z]{6}")) }

    /**
     * Stores the [key] in the [keyFile] by simply setting the content of the file to the
     * key as plain text.
     */
    fun storeKey(key: String) = keyFile.writeText(key)
}