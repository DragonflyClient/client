package net.inceptioncloud.minecraftmod.engine

import net.inceptioncloud.minecraftmod.Dragonfly
import net.inceptioncloud.minecraftmod.engine.internal.Defaults
import net.inceptioncloud.minecraftmod.engine.internal.Widget
import net.inceptioncloud.minecraftmod.engine.structure.IPosition
import net.inceptioncloud.minecraftmod.ui.renderer.RectangleRenderer
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.input.Mouse
import java.awt.Color
import java.awt.Font
import java.util.*

object GraphicsEngine {
    private val scaleStack: Stack<Double> = Stack()

    private val debugColors = arrayOf(
        0x27ae60, 0xf39c12, 0x2980b9
    )

    fun renderDebugOverlay(mapped: Map<String, Widget<*>>) {
        val content = mapped.values
        val uppermostWidget = mapped.entries.lastOrNull { it.value.hovered }

        var index = 0
        content.filter { it.hovered && it != uppermostWidget }
            .forEach { widget ->
                val x = (widget as IPosition).x
                val y = (widget as IPosition).y
                val (width, height) = Defaults.getSizeOrDimension(widget)

                Gui.drawRect(x, y, x + width, y + height, Color(0, 0, 0, 100).rgb)
                RectangleRenderer.renderOutline(
                    x, y, x + width, y + height,
                    Color(debugColors[index % (debugColors.size)]), 0.5
                )
                index++
            }

        uppermostWidget?.let {
            val x = (uppermostWidget.value as IPosition).x
            val y = (uppermostWidget.value as IPosition).y
            val (width, height) = Defaults.getSizeOrDimension(uppermostWidget.value)

            Gui.drawRect(x, y, x + width, y + height, Color(0, 0, 0, 100).rgb)
            RectangleRenderer.renderOutline(x, y, x + width, y + height, Color(0xc0392b), 0.7)

            val titleRenderer = Dragonfly.fontDesign.retrieveOrBuild("JetBrains Mono Medium", Font.PLAIN, 12)
            val fontRenderer = Dragonfly.fontDesign.retrieveOrBuild("JetBrains Mono", Font.PLAIN, 9)
            val title = "${uppermostWidget.value::class.simpleName} #${it.key}"
            val info = uppermostWidget.value.toInfo().toMutableList()
                .apply { add("scratchpad = ${uppermostWidget.value.scratchpad != null}") }
            val infoHeight = 2 + titleRenderer.height + fontRenderer.height * info.size
            val infoWidth =
                2 + (titleRenderer.getStringWidth(title)).coerceAtLeast(info.map { fontRenderer.getStringWidth(it) }
                    .max()!!)

            Gui.drawRect(
                x + width + 10,
                y - 5,
                x + width + 10 + infoWidth,
                y - 5 + infoHeight,
                Color(255, 255, 255, 220).rgb
            )

            var infoTextY = y - 2 + titleRenderer.height
            titleRenderer.drawString(title, (x + width + 11).toInt(), (y - 2).toInt(), Color(0, 0, 0, 200).rgb)
            info.forEach {
                fontRenderer.drawString(it, (x + width + 11).toInt(), infoTextY.toInt(), Color(0, 0, 0, 200).rgb)
                infoTextY += fontRenderer.height
            }
        }
    }

    @JvmStatic
    fun getScaleFactor(): Int {
        return Minecraft.getMinecraft().currentScreen?.scaleFactor
            ?: ScaledResolution(Minecraft.getMinecraft()).scaleFactor
    }

    @JvmStatic
    fun pushScale(factor: Double) {
        scaleStack.push(factor)
        GlStateManager.scale(factor, factor, factor)
    }

    @JvmStatic
    fun popScale() {
        val factor = 1 / scaleStack.pop()
        GlStateManager.scale(factor, factor, factor)
    }

    fun getMouseX(): Int {
        if (Minecraft.getMinecraft().currentScreen == null) return 0
        return Mouse.getX() / getScaleFactor()
    }

    fun getMouseY(): Int {
        if (Minecraft.getMinecraft().currentScreen == null) return 0
        return Minecraft.getMinecraft().currentScreen.height - Mouse.getY() / getScaleFactor()
    }
}