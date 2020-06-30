package net.inceptioncloud.minecraftmod.key

import com.google.gson.Gson
import khttp.responses.Response
import org.apache.logging.log4j.LogManager

/**
 * Controls the attaching and validating of the keys via http requests to the
 * Inception Cloud API.
 */
object KeyController {

    /**
     * Sends a request asking to attach the given [key] to the current machine.
     */
    fun attachKey(key: String): Result = try {
        val response = khttp.post(
            url = "https://api.inceptioncloud.net/keys/attach",
            json = mapOf(
                "key" to key,
                "machineIdentifier" to MachineIdentifier.generateIdentifier()
            ),
            timeout = 10.0
        )

        response.toResult()
    } catch (e: Exception) {
        Result(false, e.message!!)
    }

    /**
     * Sends a request asking to validate the currently [stored key][KeyStorage.getStoredKey]
     */
    fun validateStoredKey(): Result {
        try {
            val key = KeyStorage.getStoredKey() ?: return Result(false, "No key stored on local machine!")
            val response = khttp.post(
                url = "https://api.inceptioncloud.net/keys/validate",
                json = mapOf(
                    "key" to key,
                    "machineIdentifier" to MachineIdentifier.generateIdentifier()
                ),
                timeout = 10.0
            )

            return response.toResult()
        } catch (e: Exception) {
            return Result(false, e.message!!)
        }
    }

    /**
     * Converts a [Response] to a [Result].
     */
    private fun Response.toResult(): Result = if (statusCode == 200) {
        Gson().fromJson(text, Result::class.java)
    } else {
        LogManager.getLogger().error("Could not convert response to result!\n" +
                "Response: $this\n" +
                "URL: $url\n" +
                "Text: $text")
        Result(false, "Request not successful!")
    }

    /**
     * A simple class holding information about the response-
     */
    data class Result(val success: Boolean, val message: String)
}
