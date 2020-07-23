package net.inceptioncloud.dragonfly.engine

import kotlinx.coroutines.*
import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.engine.font.GlyphFontRenderer
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.structure.IPosition
import net.inceptioncloud.dragonfly.ui.renderer.RectangleRenderer
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.*
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.BufferUtils
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.nio.ByteBuffer
import java.util.*

/**
 * ## Graphics Engine
 *
 * The main object of the graphics engine providing several utility functions used for rendering and accessing
 * necessary data.
 */
object GraphicsEngine {

    /**
     * A stack that contains all currently applied scale factors.
     */
    private val scaleStack: Stack<Pair<Double, Double>> = Stack()

    /**
     * The colors used by the debug overlays. The first color has the highest priority while the last
     * one has the least priority. If all colors are used, it starts all over again.
     */
    private val debugColors = arrayOf(0x27ae60, 0xf39c12, 0x2980b9)

    /**
     * All characters that can be rendered by the [GlyphFontRenderer].
     */
    @JvmField
    val CHARACTERS = ((32..126) + (161..215)).map { it.toChar() }.toCharArray()

    /**
     * Renders the debug overlay for the given widgets and their identifiers.
     */
    fun renderDebugOverlay(mapped: Map<String, Widget<*>>) {
        val content = mapped.values
        val uppermostWidget = mapped.entries.lastOrNull { it.value.isHovered }

        var index = 0
        content.filter { it.isHovered && it != uppermostWidget }
            .forEach { widget ->
                val x = (widget as IPosition).x
                val y = (widget as IPosition).y
                val (width, height) = Defaults.getSizeOrDimension(widget)

                Gui.drawRect(x, y, x + width, y + height, Color(0, 0, 0, 20).rgb)
                RectangleRenderer.renderOutline(
                    x, y, x + width, y + height,
                    Color(debugColors[index % (debugColors.size)]), 0.7
                )
                index++
            }

        uppermostWidget?.let { widget ->
            if (widget.value is AssembledWidget<*> && Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
                renderDebugOverlay((widget.value as AssembledWidget<*>).structure.mapKeys { widget.key + "/" + it.key })
                return@let
            }

            val x = (uppermostWidget.value as IPosition).x
            val y = (uppermostWidget.value as IPosition).y
            val (width, height) = Defaults.getSizeOrDimension(uppermostWidget.value)

            RectangleRenderer.renderOutline(x, y, x + width, y + height, Color(0xc0392b), 0.8)

            val titleRenderer = Dragonfly.fontDesign.retrieveOrBuild(" Medium", 12)
            val fontRenderer = Dragonfly.fontDesign.retrieveOrBuild("", 10)
            val title = "${uppermostWidget.value::class.simpleName} #${widget.key}"
            val info = uppermostWidget.value.toInfo().toMutableList()
                .apply {
                    add("scratchpad = ${uppermostWidget.value.scratchpad != null}")
                    add("animations = ${uppermostWidget.value.animationStack.size}")
                }
            val infoHeight = 2 + titleRenderer.height + (fontRenderer.height + 0.5) * info.size
            val infoWidth = 4 + (titleRenderer.getStringWidth(title))
                .coerceAtLeast(info.map { fontRenderer.getStringWidth(it.replaceFirst("--state", "> ")) }.max()!!).toDouble()
            val infoX = getMouseX().toDouble() + 5.0 //x + width + 10
            val infoY = getMouseY().toDouble() + 5.0 //y - 5

            val avgColor = readAveragePixelColor(infoX.toInt(), infoY.toInt(), infoWidth.toInt(), infoHeight.toInt())
            val backgroundColor = avgColor.selectHighestContrast(WidgetColor(0, 0, 0, 170), WidgetColor(255, 255, 255, 170))
            val contrastColor = if (backgroundColor.red == 0) WidgetColor(255, 255, 255) else WidgetColor(0, 0, 0)

            Gui.drawRect(
                infoX, infoY,
                infoX + infoWidth, infoY + infoHeight,
                backgroundColor.rgb
            )

            var infoTextY = infoY + 4 + titleRenderer.height
            val infoTextX = infoX + 1

            titleRenderer.drawString(title, infoTextX.toInt(), (infoY + 3).toInt(), contrastColor.rgb)
            info.forEach {
                val string = it.replaceFirst("--state", "> ")
                fontRenderer.drawString(string, infoTextX.toInt(), infoTextY.toInt(), contrastColor.rgb)
                infoTextY += fontRenderer.height + 0.5
            }

            GlStateManager.color(1F, 1F, 1F, 1F)
        }
    }

    /**
     * Gets the current scale factor defined in [ScaledResolution.scaleFactor]. If the [Minecraft.currentScreen]
     * doesn't have a [GuiScreen.scaleFactor] set, a new scaled resolution is instantiated and the scale factor
     * is read from it.
     */
    @JvmStatic
    fun getScaleFactor(): Int {
        return Minecraft.getMinecraft().currentScreen?.scaleFactor
            ?: ScaledResolution(Minecraft.getMinecraft()).scaleFactor
    }

    /**
     * Pushes a scale to the [scaleStack] and applies it using [GlStateManager.scale].
     */
    @JvmStatic
    fun pushScale(factor: Pair<Double, Double>) {
        scaleStack.push(factor)
        GlStateManager.scale(factor.first, factor.second, 1.0)
    }

    /**
     * Pops the uppermost scale and reverts it using [GlStateManager.scale].
     */
    @JvmStatic
    fun popScale() {
        val factor = scaleStack.pop()
        GlStateManager.scale(1 / factor.first, 1 / factor.second, 1.0)
    }

    /**
     * Gets the current mouse x position or 0, if the [Minecraft.currentScreen] is null.
     */
    @JvmStatic
    fun getMouseX(): Int {
        if (Minecraft.getMinecraft().currentScreen == null) return 0
        return Mouse.getX() / getScaleFactor()
    }

    /**
     * Gets the current mouse y position or 0, if the [Minecraft.currentScreen] is null.
     */
    @JvmStatic
    fun getMouseY(): Int {
        if (Minecraft.getMinecraft().currentScreen == null) return 0
        return Minecraft.getMinecraft().currentScreen.height - Mouse.getY() / getScaleFactor()
    }

    /**
     * Reads the color of a specific pixel from the current frame buffer at the [xIn],[yIn] position and
     * returns it as a [WidgetColor].
     */
    @JvmStatic
    fun readPixelColor(xIn: Int, yIn: Int): WidgetColor {
        val sf = getScaleFactor()
        val x = xIn * sf
        val y = yIn * sf
        val buffer = (BufferUtils.createByteBuffer(2196).position(0).limit(64) as ByteBuffer).asFloatBuffer()
        GL11.glReadPixels(x * sf, y * sf, 1, 1, GL11.GL_RGBA, GL11.GL_FLOAT, buffer)

        return WidgetColor(buffer.get(0), buffer.get(1), buffer.get(2), buffer.get(3))
    }

    /**
     * Reads the average pixel color of the specified area. Note that if the area is large, this will
     * have a HUGE impact on the performance, so think about what you're doing!
     */
    @JvmStatic
    fun readAveragePixelColor(xIn: Int, yIn: Int, widthIn: Int, heightIn: Int): WidgetColor {
        val sf = getScaleFactor()
        val x = xIn * sf
        val y = yIn * sf
        val width = widthIn * sf
        val height = heightIn * sf
        val buffer = (BufferUtils.createByteBuffer(width * height * 4 * 4).position(0).limit(width * height * 4 * 4) as ByteBuffer).asFloatBuffer()

        GL11.glReadPixels(x, y, width, height, GL11.GL_RGBA, GL11.GL_FLOAT, buffer)

        val red = (0 until 4 * width * height step 4).map { buffer.get(it) }.average()
        val green = (1 until 4 * width * height step 4).map { buffer.get(it) }.average()
        val blue = (2 until 4 * width * height step 4).map { buffer.get(it) }.average()
        val alpha = (3 until 4 * width * height step 4).map { buffer.get(it) }.average()

        return WidgetColor(red, green, blue, alpha)
    }

    /**
     * Runs the specified [block] after a delay of [millis] in a new coroutine launched in
     * the [GlobalScope].
     */
    @JvmStatic
    fun runAfter(millis: Long, block: () -> Unit) {
        GlobalScope.launch {
            delay(millis)
            block()
        }
    }
}

/**
 * A simple extension function that converts a [java.awt.Color] to a [WidgetColor].
 */
fun Color.toWidgetColor(): WidgetColor = WidgetColor(this)
