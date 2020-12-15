package net.inceptioncloud.dragonfly.utils

import net.inceptioncloud.dragonfly.Dragonfly
import org.apache.logging.log4j.LogManager
import java.io.File
import java.security.MessageDigest
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import kotlin.random.Random

/**
 * A secure file is a safe way to store a string content on the user's machine.
 * It uses encryption to protect the actual value and allows any file extension to be used.
 *
 * @param name The name of the file including its file extension
 * @param secret The secret that is used for the encryption
 */
class SecureFile(val name: String, val secret: String) {

    /**
     * The Dragonfly-internal file in which the saved accounts are stored.
     */
    private val file = File(Dragonfly.secretsDirectory, name)

    /**
     * The secret key that is used for encryption of the Dragonfly token.
     */
    private val secretKey = prepareSecreteKey()

    /**
     * Writes the given [text] to the secure file after encrypting and obfuscating it.
     */
    fun write(text: String) {
        try {
            val cipher = Cipher.getInstance("AES")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)

            val content = Base64.getEncoder().encodeToString(cipher.doFinal(text.toByteArray()))
            val obfuscated = content.toCharArray().joinToString("") {
                if (Random.nextBoolean() && Random.nextBoolean() && Random.nextBoolean()) "$it\n-" else "$it"
            }
            file.writeBytes(obfuscated.toByteArray().reversedArray())
        } catch (e: Throwable) {
            LogManager.getLogger().error("Failed to store token!")
            e.printStackTrace()
        }
    }

    /**
     * Returns the decrypted content of the file or null if the file doesn't exist or if
     * the content could not be decrypted.
     */
    fun read(): String? = kotlin.runCatching {
        if (!file.exists()) return null
        val content = String(file.readBytes().reversedArray()).replace("\n-", "")

        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, secretKey)

        return String(cipher.doFinal(Base64.getDecoder().decode(content)))
    }.getOrNull()

    /**
     * Prepares the secret key that is used for the encryption.
     */
    private fun prepareSecreteKey(): SecretKeySpec {
        var key = secret.toByteArray()
        val sha = MessageDigest.getInstance("SHA-1")
        key = sha.digest(key)
        key = key.copyOf(newSize = 16)
        return SecretKeySpec(key, "AES")
    }
}