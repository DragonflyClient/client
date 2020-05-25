package net.inceptioncloud.minecraftmod.subscriber

import com.google.common.eventbus.Subscribe
import net.inceptioncloud.minecraftmod.Dragonfly
import net.inceptioncloud.minecraftmod.event.client.PostRenderEvent
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import java.awt.Color
import java.awt.Font

class DebugModeSubscriber
{
    @Subscribe
    fun postRender(event: PostRenderEvent)
    {
        if (!Dragonfly.isDebugMode)
            return

        renderDebugInfo("FPS: ", Minecraft.getDebugFPS().toString(), event.scaledWidth, 2)
        renderDebugInfo("TPS: ", Dragonfly.lastTPS.toString(), event.scaledWidth, 12)
        renderDebugInfo("GUI: ", Minecraft.getMinecraft().currentScreen?.javaClass?.simpleName ?: "null", event.scaledWidth, 22)
    }

    private fun renderDebugInfo(title: String, content: String, screenWidth: Int, y: Int)
    {
        val fontRenderer = Dragonfly.fontDesign.retrieveOrBuild("JetBrains Mono", Font.PLAIN, 14)
        val height = fontRenderer.height
        val framesTitleWidth = fontRenderer.getStringWidth(title)
        val framesWidth = framesTitleWidth + fontRenderer.getStringWidth(content)

        Gui.drawRect(screenWidth - 2 - framesWidth - 2, y, screenWidth - 2, y + height, Color(0, 0, 0, 150).rgb)
        fontRenderer.drawString(title, screenWidth - 3 - framesWidth, y + 2, Color.WHITE.rgb)
        fontRenderer.drawString(content, screenWidth - 3 - framesWidth + framesTitleWidth, y + 2, Color.YELLOW.rgb)
    }
}