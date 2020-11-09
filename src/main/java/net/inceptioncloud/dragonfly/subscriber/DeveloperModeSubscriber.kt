package net.inceptioncloud.dragonfly.subscriber

import com.google.common.eventbus.Subscribe
import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.engine.font.renderer.ScaledFontRenderer
import net.inceptioncloud.dragonfly.event.client.PostRenderEvent
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import java.awt.Color
import java.lang.management.ManagementFactory
import java.text.*
import java.util.*

object DeveloperModeSubscriber {

    @Subscribe
    fun postRender(event: PostRenderEvent) {
        if (!Dragonfly.isDeveloperMode)
            return

        val frameTime = DecimalFormat("0.0").apply {
            decimalFormatSymbols = DecimalFormatSymbols.getInstance(Locale.ENGLISH)
        }.format(Minecraft.renderTime / 1000000.0)

        val asyncBuilding = Dragonfly.fontManager.defaultFont.asyncBuilding
        val cachedFontRenderer = Dragonfly.fontManager.defaultFont.cachedFontRenderer

        val stats = getGCStats()
        val ago = System.currentTimeMillis() - lastGC

        val infos = listOf(
            "FPS: " to Minecraft.getDebugFPS().toString(),
            "Render time: " to "${frameTime}ms",
            "TPS: " to Dragonfly.lastTPS.toString(),
            "GUI: " to (Minecraft.getMinecraft().currentScreen?.javaClass?.simpleName ?: "null"),
            "§nFont renderers" to null,
            "Building: " to asyncBuilding.size.toString(),
            "Cached: " to cachedFontRenderer.size.toString(),
            "Scaled: " to cachedFontRenderer.count { it.value is ScaledFontRenderer }.toString(),
            "§nGarbage collection" to null,
            "Count: " to stats.totalCount.toString(),
            "Duration: " to SimpleDateFormat(if (stats.totalTime < 10_000) "mm:ss.SSS" else "mm:ss").format(stats.totalTime) + "m",
            "Last: " to SimpleDateFormat(if (ago < 10_000) "mm:ss.SSS" else "mm:ss").format(ago) + "m ago"
        )

        var currentY = 2.0
        for (info in infos) {
            if (info.second == null) currentY += 8.0

            renderDeveloperInfo(info.first, info.second ?: "", event.scaledWidth, currentY)
            currentY += if (info.second == null) 18.0 else 16.0
        }
    }

    private fun renderDeveloperInfo(title: String, content: String, screenWidth: Double, y: Double) {
        val fontRenderer = Dragonfly.fontManager.monospaceFont.fontRenderer(size = 30)
        val height = fontRenderer.height
        val framesTitleWidth = fontRenderer.getStringWidth(title)
        val framesWidth = framesTitleWidth + fontRenderer.getStringWidth(content)

        Gui.drawRect(screenWidth - 2 - framesWidth - 2, y, screenWidth - 2, y + height, Color(0, 0, 0, 120).rgb)
        fontRenderer.drawString(title, (screenWidth - 3 - framesWidth).toInt(), (y + 2).toInt(), Color.WHITE.rgb)
        fontRenderer.drawString(content, (screenWidth - 3 - framesWidth + framesTitleWidth).toInt(), (y + 2).toInt(), Color.YELLOW.rgb)
    }

    private var statsCache: GCStats? = null
    private var statsLastLoaded: Long = 0
    private var lastGC: Long = 0

    private fun getGCStats(): GCStats {
        if (statsCache == null || (System.currentTimeMillis() - statsLastLoaded) > 500) {
            val beans = ManagementFactory.getGarbageCollectorMXBeans()
            val count = beans.sumOf { it.collectionCount }
            val time = beans.sumOf { it.collectionTime }

            if (count > statsCache?.totalCount ?: 0) {
                lastGC = System.currentTimeMillis()
            }

            statsLastLoaded = System.currentTimeMillis()
            statsCache = GCStats(count, time)
        }

        return statsCache!!
    }

    private data class GCStats(val totalCount: Long, val totalTime: Long)
}
