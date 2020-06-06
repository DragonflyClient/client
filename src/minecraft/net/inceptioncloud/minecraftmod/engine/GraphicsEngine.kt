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
import java.util.*

object GraphicsEngine {
    private val scaleStack: Stack<Double> = Stack()

    private val debugColors = arrayOf(0x27ae60, 0xf39c12, 0x2980b9)

    @JvmField
    val CHARACTERS = (0..300).map { it.toChar() }.toCharArray()

    fun renderDebugOverlay(mapped: Map<String, Widget<*>>) {
        val content = mapped.values
        val uppermostWidget = mapped.entries.lastOrNull { it.value.isHovered }

        var index = 0
        content.filter { it.isHovered && it != uppermostWidget }
            .forEach { widget ->
                val x = (widget as IPosition).x
                val y = (widget as IPosition).y
                val (width, height) = Defaults.getSizeOrDimension(widget)

                Gui.drawRect(x, y, x + width, y + height, Color(0, 0, 0, 100).rgb)
                RectangleRenderer.renderOutline(
                    x, y, x + width, y + height,
                    Color(debugColors[index % (debugColors.size)]), 0.7
                )
                index++
            }

        uppermostWidget?.let { widget ->
            val x = (uppermostWidget.value as IPosition).x
            val y = (uppermostWidget.value as IPosition).y
            val (width, height) = Defaults.getSizeOrDimension(uppermostWidget.value)

            Gui.drawRect(x, y, x + width, y + height, Color(0, 0, 0, 100).rgb)
            RectangleRenderer.renderOutline(x, y, x + width, y + height, Color(0xc0392b), 0.8)

            val titleRenderer = Dragonfly.fontDesign.retrieveOrBuild(" Medium", 12)
            val fontRenderer = Dragonfly.fontDesign.retrieveOrBuild("", 10)
            val title = "${uppermostWidget.value::class.simpleName} #${widget.key}"
            val info = uppermostWidget.value.toInfo().toMutableList()
                .apply { add("scratchpad = ${uppermostWidget.value.scratchpad != null}") }
            val infoHeight = 2 + titleRenderer.height + (fontRenderer.height + 0.5) * info.size
            val infoWidth = 2 + (titleRenderer.getStringWidth(title))
                .coerceAtLeast(info.map { fontRenderer.getStringWidth(it.replaceFirst("--state", "")) }.max()!!)

            Gui.drawRect(
                x + width + 10, y - 5,
                x + width + 10 + infoWidth, y - 5 + infoHeight,
                Color(255, 255, 255, 220).rgb
            )

            var infoTextY = y - 1 + titleRenderer.height
            titleRenderer.drawString(title, (x + width + 11).toInt(), (y - 2).toInt(), Color(0, 0, 0, 200).rgb)
            info.forEach {
                val isState = it.startsWith("--state")
                val string = it.replaceFirst("--state", "")
                fontRenderer.drawString(
                    string, (x + width + 11).toInt(),
                    infoTextY.toInt(), Color(0, 0, if (isState) 150 else 0, 200).rgb
                )
                infoTextY += fontRenderer.height + 0.5
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