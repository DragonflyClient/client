package net.inceptioncloud.dragonfly.subscriber

import com.google.common.eventbus.Subscribe
import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.engine.font.renderer.ScaledFontRenderer
import net.inceptioncloud.dragonfly.event.client.PostRenderEvent
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import org.github.jamm.MemoryMeter
import java.awt.Color
import java.awt.Font


class DeveloperModeSubscriber {
    @Subscribe
    fun postRender(event: PostRenderEvent) {
        if (!Dragonfly.isDeveloperMode)
            return

        renderDebugInfo(
            "FPS: ",
            Minecraft.getDebugFPS().toString(),
            event.scaledWidth, 2
        )
        renderDebugInfo(
            "TPS: ",
            Dragonfly.lastTPS.toString(),
            event.scaledWidth, 10
        )
        renderDebugInfo(
            "GUI: ",
            Minecraft.getMinecraft().currentScreen?.javaClass?.simpleName ?: "null",
            event.scaledWidth, 18
        )

        val asyncBuilding = Dragonfly.fontDesign.defaultFont.asyncBuilding
        val cachedFontRenderer = Dragonfly.fontDesign.defaultFont.cachedFontRenderer

        renderDebugInfo(
            "Async Building: ",
            asyncBuilding.size.toString(),
            event.scaledWidth, 26
        )
        renderDebugInfo(
            "Cached: ",
            cachedFontRenderer.size.toString(),
            event.scaledWidth, 34
        )
        renderDebugInfo(
            "Scaled: ",
            cachedFontRenderer.count { it.value is ScaledFontRenderer }.toString(),
            event.scaledWidth, 42
        )
    }

    private fun renderDebugInfo(title: String, content: String, screenWidth: Int, y: Int) {
        val fontRenderer = Dragonfly.fontDesign.retrieveOrBuild("JetBrains Mono", Font.PLAIN, 14)
        val height = fontRenderer.height
        val framesTitleWidth = fontRenderer.getStringWidth(title)
        val framesWidth = framesTitleWidth + fontRenderer.getStringWidth(content)

        Gui.drawRect(screenWidth - 2 - framesWidth - 2, y, screenWidth - 2, y + height, Color(0, 0, 0, 150).rgb)
        fontRenderer.drawString(title, screenWidth - 3 - framesWidth, y + 2, Color.WHITE.rgb)
        fontRenderer.drawString(content, screenWidth - 3 - framesWidth + framesTitleWidth, y + 2, Color.YELLOW.rgb)
    }
}