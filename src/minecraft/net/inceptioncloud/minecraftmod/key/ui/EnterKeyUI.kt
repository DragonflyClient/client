package net.inceptioncloud.minecraftmod.key.ui

import kotlinx.coroutines.*
import net.inceptioncloud.minecraftmod.Dragonfly
import net.inceptioncloud.minecraftmod.design.color.DragonflyPalette
import net.inceptioncloud.minecraftmod.engine.font.FontWeight
import net.inceptioncloud.minecraftmod.engine.internal.*
import net.inceptioncloud.minecraftmod.engine.widgets.assembled.InputTextField
import net.inceptioncloud.minecraftmod.engine.widgets.assembled.TextField
import net.inceptioncloud.minecraftmod.engine.widgets.primitive.Image
import net.inceptioncloud.minecraftmod.engine.widgets.primitive.TextRenderer
import net.inceptioncloud.minecraftmod.key.KeyStorage
import net.inceptioncloud.minecraftmod.ui.components.button.DragonflyPaletteButton
import net.inceptioncloud.minecraftmod.ui.components.button.ImageButton
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.util.ResourceLocation
import org.lwjgl.input.Keyboard

/**
 * The user interface that forces the user to enter a key to unlock the Dragonfly Client.
 *
 * @param message an optional message that is transmitted from the [AttachingKeyUI]
 */
class EnterKeyUI(message: String? = null) : GuiScreen() {

    override var backgroundFill: WidgetColor? = WidgetColor(30, 30, 30, 255)

    override var backgroundImage: SizedImage? = SizedImage("inceptioncloud/ingame_background_2.png", 3840.0, 2160.0)

    override var canManuallyClose: Boolean = false

    var message: String? = message
        set(value) {
            getWidget<TextField>("message")?.apply {
                isVisible = value != null
                staticText = value ?: ""
                stateChanged(this)
            }
            field = value
        }

    override fun initGui() {
        Keyboard.enableRepeatEvents(true)
        val headerFR = Dragonfly.fontDesign.defaultFont.fontRenderer { size = 30; fontWeight = FontWeight.MEDIUM }

        val keyImage = +Image(
            x = width / 2 - headerFR.getStringWidth("Activate Dragonfly") / 2 - 15.0,
            y = 10.0,
            width = 20.0,
            height = 20.0,
            resourceLocation = ResourceLocation("inceptioncloud/icons/key.png")
        ) id "key-image"

        +TextRenderer(
            x = keyImage.x + 30.0,
            y = 10.0,
            fontRenderer = headerFR,
            text = "Activate Dragonfly"
        ) id "header"

        val description = +TextField(
            x = width / 2.0 - (width * 0.3).toInt().coerceAtMost(150).toDouble(),
            y = height * 0.15,
            width = (width * 0.6).toInt().coerceAtMost(300).toDouble(),
            height = 90.0,
            textAlignHorizontal = Alignment.CENTER,
            textAlignVertical = Alignment.CENTER,
            staticText = "Please enter your Dragonfly key or press the button next to the input field to paste it from your clipboard.\n\n" +
                    "Note that by redeeming your key, it is attached to your machine and cannot be used on any other device.\n\n" +
                    "If you don't already have a key, consider applying for our alpha program on our Discord server."
        ) id "description"

        +TextField(
            x = description.x,
            y = description.y + description.height + 10,
            width = description.width,
            height = 18.0,
            textAlignHorizontal = Alignment.CENTER,
            textAlignVertical = Alignment.END,
            staticText = message ?: "",
            color = DragonflyPalette.ACCENT_NORMAL
        ).apply { isVisible = message != null } id "message"

        val keyInput = +InputTextField(
            x = width / 2.0 - 110.0,
            y = description.y + description.height + 35.0,
            color = DragonflyPalette.ACCENT_NORMAL,
            width = 200.0,
            height = 20.0,
            label = "32-digit key with hyphens",
            maxStringLength = 32
        ) id "key-input"

        with(buttonList) {
            add(ImageButton(
                buttonId = 1,
                x = (keyInput.x + 206).toInt(),
                y = (keyInput.y + 3).toInt(),
                width = 14,
                height = 14,
                resourceLocation = ResourceLocation("inceptioncloud/icons/copy.png")
            ))

            add(DragonflyPaletteButton(
                buttonId = 2,
                x = width / 2 - 50,
                y = height - 30,
                widthIn = 100,
                heightIn = 20,
                buttonText = "Redeem key"
            ))
        }
    }

    override fun actionPerformed(button: GuiButton?) {
        when (button?.id) {
            1 -> pasteFromClipboard()
            2 -> redeemKey()
        }
    }

    /**
     * Pastes the current clipboard content into the `key-input` [InputTextField].
     */
    private fun pasteFromClipboard() {
        getWidget<InputTextField>("key-input")?.run {
            GlobalScope.launch {
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

    /**
     * Redeems the currently entered key from the `key-input` [InputTextField].
     */
    private fun redeemKey() {
        getWidget<InputTextField>("key-input")?.run {
            if (inputText.isNotEmpty() && KeyStorage.isRegexMatching(inputText)) {
                mc.displayGuiScreen(AttachingKeyUI(inputText))
            } else {
                this@EnterKeyUI.getWidget<TextField>("message")?.apply {
                    isVisible = true
                    staticText = "The key you entered does not match the Dragonfly key format."
                    stateChanged(this)
                }
            }
        }
    }
}