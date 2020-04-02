package net.inceptioncloud.minecraftmod.ui.renderer

import net.inceptioncloud.minecraftmod.design.color.ColorTransformator
import net.inceptioncloud.minecraftmod.design.color.GreyToneColor
import net.inceptioncloud.minecraftmod.design.font.IFontRenderer
import net.minecraft.client.gui.Gui
import java.awt.event.KeyEvent

fun render(fontRenderer: IFontRenderer, keycode: Int, description: String, x: Int, y: Int)
{
    val keyString = KeyEvent.getKeyText(keycode)
    val height = fontRenderer.height + 4
    val length = fontRenderer.getStringWidth(keyString).coerceAtLeast(height) + 4
    val fontY = y + height / 4 + 2;

    val foreground = GreyToneColor.WHITE.rgb
    val background = ColorTransformator.of(GreyToneColor.DARK_GREY).changeAlpha(0.3F).toRGB()

    Gui.drawRect(x + 1, y + 1, x + length + 1, y + height + 1, background)

    Gui.drawRect(x, y, x + length + 2, y + 1, foreground)
    Gui.drawRect(x, y + height + 2, x + length + 2, y + height + 1, foreground)
    Gui.drawRect(x, y, x + 1, y + height + 2, foreground)
    Gui.drawRect(x + length + 1, y, x + length + 2, y + height + 2, foreground)

    fontRenderer.drawCenteredString(keyString, x + (length + 2) / 2 - 1, fontY, GreyToneColor.LIGHT_WHITE.rgb, false)
    fontRenderer.drawString(description, x + length + 6F, fontY.toFloat(), GreyToneColor.LIGHT_WHITE.rgb, false)
}