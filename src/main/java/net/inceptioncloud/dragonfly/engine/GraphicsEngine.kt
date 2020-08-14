package net.inceptioncloud.dragonfly.engine

import kotlinx.coroutines.*
import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.engine.font.renderer.GlyphFontRenderer
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
    private val scaleStack: Stack<Double> = Stack()

    /**
     * All characters that can be rendered by the [GlyphFontRenderer].
     */
    @JvmField
    val CHARACTERS = ((32..126) + (161..252)).map { it.toChar() }.toCharArray()

    /**
     * Gets the current scale factor defined in [ScaledResolution.scaleFactor]. If the [Minecraft.currentScreen]
     * doesn't have a [GuiScreen.scaleFactor] set, a new scaled resolution is instantiated and the scale factor
     * is read from it.
     */
    @JvmStatic
    fun getScaleFactor(): Double {
        return (Minecraft.getMinecraft().currentScreen?.scaleFactor
            ?: ScaledResolution(Minecraft.getMinecraft()).scaleFactor.toDouble())
    }

    /**
     * Pushes a scale to the [scaleStack] and applies it using [GlStateManager.scale].
     */
    @JvmStatic
    fun pushScale(factor: Double) {
        scaleStack.push(factor)
        GlStateManager.scale(factor, factor, 1.0)
    }

    /**
     * Pops the uppermost scale and reverts it using [GlStateManager.scale].
     */
    @JvmStatic
    fun popScale() {
        val factor = scaleStack.pop()
        GlStateManager.scale(1 / factor, 1 / factor, 1.0)
    }

    /**
     * Gets the current mouse x position or 0, if the [Minecraft.currentScreen] is null.
     */
    @JvmStatic
    fun getMouseX(): Double {
        if (Minecraft.getMinecraft().currentScreen == null) return 0.0
        return Mouse.getX() / getScaleFactor()
    }

    /**
     * Gets the current mouse y position or 0, if the [Minecraft.currentScreen] is null.
     */
    @JvmStatic
    fun getMouseY(): Double {
        if (Minecraft.getMinecraft().currentScreen == null) return 0.0
        return Minecraft.getMinecraft().currentScreen.height - Mouse.getY() / getScaleFactor()
    }

    /**
     * Reads the color of a specific pixel from the current frame stage at the [xIn],[yIn] position and
     * returns it as a [WidgetColor].
     */
    @JvmStatic
    fun readPixelColor(xIn: Int, yIn: Int): WidgetColor {
        val sf = getScaleFactor()
        val x = (xIn * sf * sf).toInt()
        val y = (yIn * sf * sf).toInt()
        val buffer = (BufferUtils.createByteBuffer(2196).position(0).limit(64) as ByteBuffer).asFloatBuffer()
        GL11.glReadPixels(x, y, 1, 1, GL11.GL_RGBA, GL11.GL_FLOAT, buffer)

        return WidgetColor(buffer.get(0), buffer.get(1), buffer.get(2), buffer.get(3))
    }

    /**
     * Reads the average pixel color of the specified area. Note that if the area is large, this will
     * have a HUGE impact on the performance, so think about what you're doing!
     */
    @JvmStatic
    fun readAveragePixelColor(xIn: Int, yIn: Int, widthIn: Int, heightIn: Int): WidgetColor {
        val sf = getScaleFactor()
        val x = (xIn * sf).toInt()
        val y = (yIn * sf).toInt()
        val width = (widthIn * sf).toInt()
        val height = (heightIn * sf).toInt()
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
