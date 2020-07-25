package net.inceptioncloud.dragonfly.ui.screens

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.design.color.BluePalette
import net.inceptioncloud.dragonfly.design.color.RGB
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation.Companion.morph
import net.inceptioncloud.dragonfly.engine.font.*
import net.inceptioncloud.dragonfly.engine.internal.WidgetColor
import net.inceptioncloud.dragonfly.engine.sequence.easing.EaseCubic
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Image
import net.inceptioncloud.dragonfly.options.OptionKey
import net.inceptioncloud.dragonfly.options.Options
import net.inceptioncloud.dragonfly.options.entries.OptionEntry
import net.inceptioncloud.dragonfly.options.entries.TitleEntry
import net.inceptioncloud.dragonfly.options.entries.util.ExternalApplier
import net.inceptioncloud.dragonfly.options.sections.OptionList
import net.inceptioncloud.dragonfly.options.sections.OptionSection
import net.inceptioncloud.dragonfly.ui.components.button.BluePaletteButton
import net.inceptioncloud.dragonfly.ui.components.button.ImageButton
import net.inceptioncloud.dragonfly.ui.components.list.UIList
import net.inceptioncloud.dragonfly.ui.components.list.UIListEntry
import net.inceptioncloud.dragonfly.ui.components.list.UIListFactory.Companion.uiListFactory
import net.inceptioncloud.dragonfly.utils.TimeUtils
import net.inceptioncloud.dragonfly.utils.smartLog
import net.inceptioncloud.dragonfly.versioning.DragonflyVersion
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.util.ResourceLocation
import java.awt.Color
import java.util.*

/**
 * ## Mod Options UI
 *
 * In this gui screen, all options for the client can be modified.
 * It is built up by a list in which the different option entries are grouped in sections.
 * Thanks to the list, it is responsible on plenty of different screen sizes and can dynamically be updated.
 *
 * @see Options
 * @see OptionList
 * @see OptionKey
 * @see OptionEntry
 * @see OptionSection
 *
 * @property previousScreen the screen which this ui was opened from
 */
class ModOptionsUI(private val previousScreen: GuiScreen) : GuiScreen() {

    /**
     * List with all setting elements.
     */
    private lateinit var uiList: UIList

    /**
     * The location where the resource for the background image can be found.
     *
     * Each time the class is initialized, one of two background images will randomly be chosen.
     */
    private val resourceLocation = ResourceLocation(
        "dragonflyres/ingame_background_${if (Random().nextBoolean()) 2 else 1}.png"
    )

    private var focusedEntry: UIListEntry? = null

    var helpAttachedEntry: OptionEntry<*>? = null

    /**
     * UI Initialization
     */
    override fun initGui() {
        +Image(
            resourceLocation = ResourceLocation("dragonflyres/icons/info.png"),
            x = 0.0,
            y = 0.0,
            width = 14.0,
            height = 14.0,
            color = WidgetColor(255, 255, 255, 0)
        ) id "help-icon"

        uiList = uiListFactory {
            dimensions {
                widthIn = (width / 2.2).toInt().coerceAtLeast(250).coerceAtMost(350)
                heightIn = height - 48
                xIn = width / 2 - widthIn / 2
                yIn = 30

                slots {
                    widthIn = this@dimensions.widthIn
                    heightIn = 20
                }
            }

            entries {
                OptionList.all.forEach { section ->
                    +TitleEntry(section.title)
                    section.entries.forEach {
                        +it
                    }
                }
            }
        }

        buttonList.add(
            BluePaletteButton(
                0, width / 2 - 40, height - 24,
                80, 18, "Save and Exit"
            )
        )
        buttonList.add(
            ImageButton(
                1, width - 14 - 3, height - 14 - 2, 14, 14,
                ResourceLocation("dragonflyres/icons/reload.png")
            )
        )
    }

    /**
     * Default screen drawing function.
     */
    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawBackground()
        buffer.render()

        drawHeader()
        drawFooter()

        uiList.drawScreen(mouseX, mouseY, partialTicks)

        drawGradientVertical(0, 30, width, 35, Color(0, 0, 0, 80).rgb, Color(0, 0, 0, 0).rgb)
        drawGradientVertical(0, height - 23, width, height - 18, Color(0, 0, 0, 0).rgb, Color(0, 0, 0, 80).rgb)

        for (guiButton in ArrayList(buttonList)) {
            guiButton.drawButton(mc, mouseX, mouseY)
        }

        val newFocusedEntry = uiList.entries
            .firstOrNull {
                if (focusedEntry == it) {
                    mouseX in it.x - 24..it.x + uiList.listWidth
                } else {
                    mouseX in it.x..it.x + uiList.listWidth
                } && mouseY in it.y..it.y + uiList.entryHeight
            }

        val helpIcon = getWidget<Image>("help-icon")
        if (newFocusedEntry == null && focusedEntry != null) {
            focusedEntry = newFocusedEntry
            helpAttachedEntry = null
            helpIcon?.morph(50, EaseCubic.IN_OUT) {
                color = WidgetColor(255, 255, 255, 0)
            }?.start()
        } else if (newFocusedEntry is OptionEntry<*> && newFocusedEntry != focusedEntry) {
            focusedEntry = newFocusedEntry
            helpAttachedEntry = newFocusedEntry
            if (helpIcon?.color?.alpha != 255) {
                helpIcon?.run {
                    x = focusedEntry!!.x - 19.0
                    y = focusedEntry!!.y + 3.0
                    morph(50, EaseCubic.IN_OUT) {
                        color = WidgetColor(255, 255, 255, 255)
                    }?.start()
                }
            } else {
                helpIcon.morph(25, EaseCubic.IN_OUT) {
                    x = focusedEntry!!.x - 19.0
                    y = focusedEntry!!.y + 3.0
                }?.start()
            }
        }

        if (helpAttachedEntry != null) {
            helpIcon?.apply {
                isVisible = true
                x = helpAttachedEntry!!.x - 19.0
                y = helpAttachedEntry!!.y + 3.0
            }
        }
    }

    /**
     * Draws the background according to the current game state.
     */
    private fun drawBackground() {
        if (mc.theWorld == null) {
            mc.textureManager.bindTexture(resourceLocation)

            var originalWidth = 3840
            var originalHeight = 2160
            val factorWidth = originalWidth / width
            val factorHeight = originalHeight / height
            val factor = factorWidth.coerceAtMost(factorHeight)

            originalWidth /= factor
            originalHeight /= factor

            val differenceWidth = originalWidth - width
            val differenceHeight = originalHeight - height

            drawModalRectWithCustomSizedTexture(
                -(differenceWidth / 2), -(differenceHeight / 2), 0F, 0F,
                originalWidth, originalHeight, originalWidth.toFloat(), originalHeight.toFloat()
            )
        }

        drawRect(0, 0, width, height, RGB.of(BluePalette.BACKGROUND).alpha(0.5F).rgb())
    }

    /**
     * Draws the header with the "Mod Options" title.
     */
    private fun drawHeader() {
        val font = Dragonfly.fontDesign.defaultFont
        val fontSize = 16
        val titleString = "Mod Options"
        val y = 15 - fontSize / 2 + 2
        val fontRenderer: IFontRenderer? =
            font.fontRendererAsync { size = fontSize * 2; fontWeight = FontWeight.MEDIUM }
        val stringWidth = fontRenderer?.getStringWidth(titleString)

        drawRect(0, 0, width, 30, BluePalette.FOREGROUND.rgb)
        fontRenderer?.drawStringWithCustomShadow(
            titleString, width / 2 - stringWidth!! / 2, y,
            BluePalette.PRIMARY.rgb, Color(0, 0, 0, 40).rgb, 0.9F
        )
    }

    /**
     * Draws the footer that contains the version info and a save-and-exit button.
     */
    private fun drawFooter() {
        drawRect(0, height - 18, width, height, BluePalette.FOREGROUND.rgb)

        Dragonfly.fontDesign.defaultFont.fontRendererAsync { size = 15 }
            ?.drawString(DragonflyVersion.string, 5, height - 10, Color(0, 0, 0, 50).rgb)
    }

    /**
     * Passes the mouse input on to the list.
     */
    override fun handleMouseInput() {
        uiList.handleMouseInput()
        super.handleMouseInput()
    }

    /**
     * Fires when a button is pressed.
     *
     * In this GUI, the only available button is the "Save & Exit Button" which shows up the previous gui screen.
     */
    override fun actionPerformed(button: GuiButton?) {
        if (button?.id == 0) {
            uiList.entries.filter { it is ExternalApplier<*> }
                .map { it as ExternalApplier<*> }
                .forEach { it.applyExternally() }

            mc.displayGuiScreen(previousScreen)
        } else if (button?.id == 1) {
            TimeUtils.requireDelay("ingame-reload-button", 1000L) { Dragonfly.reload() }
        }
    }

    /**
     * Notifies the [uiList] when the mouse is pressed.
     */
    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        uiList.mousePressed(mouseX, mouseY, mouseButton)
        super.mouseClicked(mouseX, mouseY, mouseButton)
    }

    /**
     * Notifies the [uiList] when the mouse is dragged.
     */
    override fun mouseClickMove(mouseX: Int, mouseY: Int, clickedMouseButton: Int, timeSinceLastClick: Long) {
        uiList.mouseDragged(mouseX, mouseY, clickedMouseButton, timeSinceLastClick)
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick)
    }

    /**
     * Notifies the [uiList] when the mouse is released.
     */
    override fun mouseReleased(mouseX: Int, mouseY: Int, state: Int) {
        uiList.mouseReleased(mouseX, mouseY, state)
        super.mouseReleased(mouseX, mouseY, state)
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen).
     * This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     * Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    override fun keyTyped(typedChar: Char, keyCode: Int) {
        uiList.keyTyped(typedChar, keyCode)
        super.keyTyped(typedChar, keyCode)
    }
}