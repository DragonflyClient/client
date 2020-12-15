package net.inceptioncloud.dragonfly.diagnostic

import com.google.gson.JsonObject
import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.mc
import net.inceptioncloud.dragonfly.options.sections.StorageOptions
import net.minecraft.crash.CrashReport
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.apache.logging.log4j.LogManager

object CrashDiagnostics {

    /**
     * Submits the given [report] to the Dragonfly servers if this is allowed by the user.
     */
    @JvmStatic
    fun submit(report: CrashReport) {
        if (StorageOptions.SEND_DIAGNOSTICS.get() != 1) return
        if (Dragonfly.isDeveloperMode) return

        try {
            LogManager.getLogger().info("Submitting crash report...")

            val json = JsonObject()
            json.addProperty("cause", report.causeStackTraceOrString)
            json.addProperty("comment", report.wittyComment)
            json.addProperty("user", mc.session.username)
            json.addProperty("dragonflyUser", Dragonfly.account?.username)
            json.addProperty("full", report.completeReport)

            val body = json.toString()
                .toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("https://api.playdragonfly.net/v1/diagnostics/submit_crash_report")
                .post(body)
                .build()
            val response = Dragonfly.httpClient.newCall(request).execute()
                .use { response -> response.body!!.string() }
                .let { Dragonfly.gson.fromJson(it, JsonObject::class.java).asJsonObject }

            // validate response
            if (!response.get("success").asBoolean) throw IllegalStateException(response.get("error").asString)

            LogManager.getLogger().info("Crash report submitted to Dragonfly")
        } catch (e: Throwable) {
            LogManager.getLogger().error("Could not submit crash report to Dragonfly!")
        }
    }
}