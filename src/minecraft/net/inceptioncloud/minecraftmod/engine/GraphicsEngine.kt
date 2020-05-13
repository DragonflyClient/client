package net.inceptioncloud.minecraftmod.engine

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import java.util.*

object GraphicsEngine
{
    private val scaleStack: Stack<Double> = Stack()

    @JvmStatic
    fun getScaledResolution(): Int
    {
        return Minecraft.getMinecraft().currentScreen?.scaleFactor
               ?: ScaledResolution(Minecraft.getMinecraft()).scaleFactor
    }

    @JvmStatic
    fun pushScale(factor: Double)
    {
        scaleStack.push(factor)
        GlStateManager.scale(factor, factor, factor)
    }

    @JvmStatic
    fun popScale()
    {
        val factor = 1 / scaleStack.pop()
        GlStateManager.scale(factor, factor, factor)
    }
}