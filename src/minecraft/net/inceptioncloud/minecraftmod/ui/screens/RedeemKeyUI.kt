package net.inceptioncloud.minecraftmod.ui.screens

import net.inceptioncloud.minecraftmod.design.color.BluePalette
import net.inceptioncloud.minecraftmod.engine.internal.WidgetColor
import net.inceptioncloud.minecraftmod.engine.toWidgetColor
import net.minecraft.client.gui.GuiScreen

object RedeemKeyUI : GuiScreen() {

    override var backgroundFill: WidgetColor? = BluePalette.BACKGROUND.toWidgetColor()

    override fun initGui() {
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
    }
}