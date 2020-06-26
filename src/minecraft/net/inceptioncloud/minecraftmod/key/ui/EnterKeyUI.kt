package net.inceptioncloud.minecraftmod.key.ui

import net.inceptioncloud.minecraftmod.design.color.DragonflyPalette
import net.inceptioncloud.minecraftmod.engine.internal.WidgetColor
import net.inceptioncloud.minecraftmod.engine.widgets.primitive.Image
import net.minecraft.client.gui.GuiScreen
import net.minecraft.util.ResourceLocation

/**
 * The user interface that forces the user to enter a key to unlock the Dragonfly Client.
 */
class EnterKeyUI(val message: String? = null) : GuiScreen() {

    override var backgroundFill: WidgetColor? = DragonflyPalette.BACKGROUND

    override fun initGui() {
        +Image(
            x = 100.0,
            y = 100.0,
            width = 30.0,
            height = 30.0,
            resourceLocation = ResourceLocation("inceptioncloud/icons/key.png")
        ) id "image"
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.drawBackgroundFill()
        super.drawScreen(mouseX, mouseY, partialTicks)
    }
}