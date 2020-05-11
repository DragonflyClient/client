package net.inceptioncloud.minecraftmod.engine

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution

object GraphicsEngine
{
    fun getScaledResolution(): Int
    {
        return Minecraft.getMinecraft().currentScreen?.scaleFactor
               ?: ScaledResolution(Minecraft.getMinecraft()).scaleFactor
    }
}