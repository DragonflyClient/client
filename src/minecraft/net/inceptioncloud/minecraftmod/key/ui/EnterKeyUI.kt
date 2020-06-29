package net.inceptioncloud.minecraftmod.key.ui

import kotlinx.coroutines.*
import net.inceptioncloud.minecraftmod.Dragonfly
import net.inceptioncloud.minecraftmod.design.color.DragonflyPalette
import net.inceptioncloud.minecraftmod.engine.font.FontWeight
import net.inceptioncloud.minecraftmod.engine.internal.SizedImage
import net.inceptioncloud.minecraftmod.engine.internal.WidgetColor
import net.inceptioncloud.minecraftmod.engine.widgets.assembled.InputTextField
import net.inceptioncloud.minecraftmod.engine.widgets.primitive.Image
import net.inceptioncloud.minecraftmod.engine.widgets.primitive.TextRenderer
import net.inceptioncloud.minecraftmod.ui.components.button.ImageButton
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.util.ResourceLocation
import org.lwjgl.input.Keyboard

/**
 * The user interface that forces the user to enter a key to unlock the Dragonfly Client.
 */
class EnterKeyUI(val message: String? = null) : GuiScreen() {

    override var backgroundFill: WidgetColor? = WidgetColor(30, 30, 30, 255)

    override var backgroundImage: SizedImage? = SizedImage("inceptioncloud/ingame_background_2.png", 3840.0, 2160.0)

    override fun initGui() {
        Keyboard.enableRepeatEvents(true)
        val headerFontRenderer = Dragonfly.fontDesign.defaultFont.fontRenderer { size = 30; fontWeight = FontWeight.MEDIUM }

        val keyImage = +Image(
            x = width / 2 - headerFontRenderer.getStringWidth("Activate Dragonfly") / 2 - 15.0,
            y = 10.0,
            width = 20.0,
            height = 20.0,
            resourceLocation = ResourceLocation("inceptioncloud/icons/key.png")
        ) id "key-image"

        +TextRenderer(
            x = keyImage.x + 30.0,
            y = 10.0,
            fontRenderer = headerFontRenderer,
            text = "Activate Dragonfly"
        ) id "header"

        val keyInput = +InputTextField(
            x = width / 2.0 - 110.0,
            y = height / 2.0 - 10.0,
            color = DragonflyPalette.ACCENT_NORMAL,
            width = 200.0,
            height = 20.0,
            label = "32-digit key with hyphens",
            maxStringLength = 32
        ) id "key-input"

        buttonList.add(
            ImageButton(
                buttonId = 1,
                x = (keyInput.x + 206).toInt(),
                y = (keyInput.y + 3).toInt(),
                width = 14,
                height = 14,
                resourceLocation = ResourceLocation("inceptioncloud/icons/copy.png")
            )
        )
    }

    override fun actionPerformed(button: GuiButton?) {
        if (button?.id == 1) {
            GlobalScope.launch {
                getWidget<InputTextField>("key-input")?.run {
                    delay(10)
                    isFocused = true
                    delay(30)

                    repeat(inputText.length) {
                        deleteFromCursor(-1, true)
                        delay(3)
                    }

                    clipboardString?.chars()?.let {
                        for (char in it) {
                            writeText(char.toChar().toString(), true)
                            delay(5)
                        }
                    }
                }
            }
        }
    }
}