package net.inceptioncloud.dragonfly.ui.screens

import net.minecraft.client.renderer.GlStateManager
import org.jetbrains.skija.*
import org.jetbrains.skiko.SkiaRenderer
import org.lwjgl.opengl.GL11
import java.awt.Color

object TestRenderer : SkiaRenderer {
    override fun onRender(canvas: Canvas, width: Int, height: Int) {
        try {
            canvas.drawRect(
                Rect.makeXYWH(0f, 0f, width.toFloat(), height.toFloat()),
                Paint().setColor(Color.RED.rgb)
            )
            canvas.drawCircle(200f, 200f, 100f, Paint().setColor(Color.GREEN.rgb))
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        GL11.glPopMatrix()
    }

    override fun onInit() {}
    override fun onDispose() {}
    override fun onReshape(width: Int, height: Int) {}
}
