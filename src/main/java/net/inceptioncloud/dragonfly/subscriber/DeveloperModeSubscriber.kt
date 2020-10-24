package net.inceptioncloud.dragonfly.subscriber

import com.google.common.eventbus.Subscribe
import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.engine.font.renderer.ScaledFontRenderer
import net.inceptioncloud.dragonfly.event.client.PostRenderEvent
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import java.awt.Color

object DeveloperModeSubscriber {

    @Subscribe
    fun postRender(event: PostRenderEvent) {
        if (!Dragonfly.isDeveloperMode)
            return

        renderDeveloperInfo(
            "FPS: ",
            Minecraft.getDebugFPS().toString(),
            event.scaledWidth, 2.0
        )
        renderDeveloperInfo(
            "TPS: ",
            Dragonfly.lastTPS.toString(),
            event.scaledWidth, 22.0
        )
        renderDeveloperInfo(
            "GUI: ",
            Minecraft.getMinecraft().currentScreen?.javaClass?.simpleName ?: "null",
            event.scaledWidth, 42.0
        )

        val asyncBuilding = Dragonfly.fontManager.defaultFont.asyncBuilding
        val cachedFontRenderer = Dragonfly.fontManager.defaultFont.cachedFontRenderer

        renderDeveloperInfo(
            "Building: ",
            asyncBuilding.size.toString(),
            event.scaledWidth, 62.0
        )
        renderDeveloperInfo(
            "Cached: ",
            cachedFontRenderer.size.toString(),
            event.scaledWidth, 82.0
        )
        renderDeveloperInfo(
            "Scaled: ",
            cachedFontRenderer.count { it.value is ScaledFontRenderer }.toString(),
            event.scaledWidth, 102.0
        )
    }

    private fun renderDeveloperInfo(title: String, content: String, screenWidth: Double, y: Double) {
        val fontRenderer = Dragonfly.fontManager.monospaceFont.fontRenderer(size = 38)
        val height = fontRenderer.height
        val framesTitleWidth = fontRenderer.getStringWidth(title)
        val framesWidth = framesTitleWidth + fontRenderer.getStringWidth(content)

        Gui.drawRect(screenWidth - 2 - framesWidth - 2, y, screenWidth - 2, y + height, Color(0, 0, 0, 150).rgb)
        fontRenderer.drawString(title, (screenWidth - 3 - framesWidth).toInt(), (y + 2).toInt(), Color.WHITE.rgb)
        fontRenderer.drawString(content, (screenWidth - 3 - framesWidth + framesTitleWidth).toInt(), (y + 2).toInt(), Color.YELLOW.rgb)
    }
}
