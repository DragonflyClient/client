package net.inceptioncloud.dragonfly.ui.renderer

import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import java.awt.Color

object RectangleRenderer {
    @JvmStatic
    fun renderInline(left: Int, top: Int, right: Int, bottom: Int, color: Color, width: Int) {
        // left:
        Gui.drawRect(left, top, left + width, bottom, color.rgb)

        // bottom:
        Gui.drawRect(left, bottom, right, bottom + width, color.rgb)

        // right:
        Gui.drawRect(right, top, right + width, bottom + width, color.rgb)

        // top:
        Gui.drawRect(left + width, top, right, top + width, color.rgb)
    }

    @JvmStatic
    fun renderOutline(left: Double, top: Double, right: Double, bottom: Double, color: Color, width: Double = 1.0) {
        // left:
        Gui.drawRect(left - width, top, left, bottom, color.rgb)

        // bottom:
        Gui.drawRect(left - width, bottom, right + width, bottom + width, color.rgb)

        // right:
        Gui.drawRect(right, top, right + width, bottom, color.rgb)

        // top:
        Gui.drawRect(left - width, top, right + width, top - width, color.rgb)
    }

    @JvmStatic
    fun drawOutline(left: Double, top: Double, right: Double, bottom: Double, color: Color) {
        GlStateManager.scale(0.5, 0.5, 0.5)

        // top:
        Gui.drawHorizontalLine((left * 2).toInt(), (right * 2).toInt(), (top * 2).toInt(), color.rgb)

        // bottom:
        Gui.drawHorizontalLine((left * 2).toInt(), (right * 2).toInt(), (bottom * 2).toInt(), color.rgb)

        // left:
        Gui.drawVerticalLine((left * 2).toInt(), (top * 2).toInt(), (bottom * 2).toInt(), color.rgb)

        // right:
        Gui.drawVerticalLine((right * 2).toInt(), (top * 2 - 1).toInt(), (bottom * 2 + 1).toInt(), color.rgb)

        GlStateManager.scale(2.0, 2.0, 2.0)
    }

    @JvmStatic
    fun renderInline(left: Double, top: Double, right: Double, bottom: Double, color: Color, width: Double) =
        renderInline(left.toInt(), top.toInt(), right.toInt(), bottom.toInt(), color, width.toInt())

    @JvmStatic
    fun renderOutline(left: Int, top: Int, right: Int, bottom: Int, color: Color, width: Int) =
        renderOutline(left.toDouble(), top.toDouble(), right.toDouble(), bottom.toDouble(), color, width.toDouble())

    @JvmStatic
    fun drawOutline(left: Int, top: Int, right: Int, bottom: Int, color: Color) =
        drawOutline(left.toDouble(), top.toDouble(), right.toDouble(), bottom.toDouble(), color)
}