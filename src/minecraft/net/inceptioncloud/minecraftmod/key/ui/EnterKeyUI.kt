package net.inceptioncloud.minecraftmod.key.ui

import net.inceptioncloud.minecraftmod.engine.internal.SizedImage
import net.inceptioncloud.minecraftmod.engine.internal.WidgetColor
import net.inceptioncloud.minecraftmod.engine.widgets.primitive.Image
import net.minecraft.client.gui.GuiScreen
import net.minecraft.util.ResourceLocation

/**
 * The user interface that forces the user to enter a key to unlock the Dragonfly Client.
 */
class EnterKeyUI(val message: String? = null) : GuiScreen() {

    override var backgroundFill: WidgetColor? = WidgetColor(100, 100, 100, 255)

    override var backgroundImage: SizedImage? = SizedImage("inceptioncloud/ingame_background_2.png", 3840.0, 2160.0)

    override fun initGui() {
        +Image(
            x = 100.0,
            y = 100.0,
            width = 30.0,
            height = 30.0,
            resourceLocation = ResourceLocation("inceptioncloud/icons/key.png")
        ) id "image"
    }

}