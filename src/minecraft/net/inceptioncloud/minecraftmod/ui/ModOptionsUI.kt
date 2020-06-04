package net.inceptioncloud.minecraftmod.ui

import net.inceptioncloud.minecraftmod.Dragonfly
import net.inceptioncloud.minecraftmod.design.color.BluePalette
import net.inceptioncloud.minecraftmod.design.color.RGB
import net.inceptioncloud.minecraftmod.engine.font.GlyphFontRenderer
import net.inceptioncloud.minecraftmod.options.OptionKey
import net.inceptioncloud.minecraftmod.options.Options
import net.inceptioncloud.minecraftmod.options.entries.OptionEntry
import net.inceptioncloud.minecraftmod.options.entries.TitleEntry
import net.inceptioncloud.minecraftmod.options.entries.util.ExternalApplier
import net.inceptioncloud.minecraftmod.options.sections.OptionList
import net.inceptioncloud.minecraftmod.options.sections.OptionSection
import net.inceptioncloud.minecraftmod.ui.components.button.BluePaletteButton
import net.inceptioncloud.minecraftmod.ui.components.button.ImageButton
import net.inceptioncloud.minecraftmod.ui.components.list.UIList
import net.inceptioncloud.minecraftmod.ui.components.list.UIListFactory.Companion.uiListFactory
import net.inceptioncloud.minecraftmod.utils.TimeUtils
import net.minecraft.client.gui.Gui
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
class ModOptionsUI(private val previousScreen: GuiScreen) : GuiScreen()
{
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
            "inceptioncloud/ingame_background_${if (Random().nextBoolean()) 2 else 1}.png"
    )

    /**
     * UI Initialization
     */
    override fun initGui()
    {
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

        buttonList.add(BluePaletteButton(0, width / 2 - 40, height - 24,
                80, 18, "Save and Exit"))
        buttonList.add(ImageButton(1, width - 14 - 3, height - 14 - 2, 14, 14,
                ResourceLocation("inceptioncloud/icons/reload.png")))
    }

    /**
     * Default screen drawing function.
     */
    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float)
    {
        drawBackground()
        drawHeader()
        drawFooter()

        uiList.drawScreen(mouseX, mouseY, partialTicks)

        drawGradientVertical(0, 30, width, 35, Color(0, 0, 0, 80).rgb, Color(0, 0, 0, 0).rgb)
        drawGradientVertical(0, height - 23, width, height - 18, Color(0, 0, 0, 0).rgb, Color(0, 0, 0, 80).rgb)

        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    /**
     * Draws the background according to the current game state.
     */
    private fun drawBackground()
    {
        if (mc.theWorld == null)
        {
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

            Gui.drawModalRectWithCustomSizedTexture(
                    -(differenceWidth / 2), -(differenceHeight / 2), 0F, 0F,
                    originalWidth, originalHeight,
                    originalWidth.toFloat(), originalHeight.toFloat()
            )
        }

        drawRect(0, 0, width, height, RGB.of(BluePalette.BACKGROUND).alpha(0.5F).rgb())
    }

    /**
     * Draws the header with the "Mod Options" title.
     */
    private fun drawHeader()
    {
        val titleString = "Mod Options"
        val fontSize = 16
        val y = 15 - fontSize / 2 + 2
        val fontRenderer = Dragonfly.fontDesign
            .retrieveOrBuild(" Medium", fontSize * 2) as GlyphFontRenderer
        val stringWidth = fontRenderer.getStringWidth(titleString)

        drawRect(0, 0, width, 30, BluePalette.FOREGROUND.rgb)
        fontRenderer.drawStringWithCustomShadow(titleString, width / 2 - stringWidth / 2, y, BluePalette.PRIMARY.rgb,
                Color(0, 0, 0, 40).rgb, 0.9F)
    }

    /**
     * Draws the footer that contains the version info and a save-and-exit button.
     */
    private fun drawFooter()
    {
        Gui.drawRect(0, height - 18, width, height, BluePalette.FOREGROUND.rgb)
        Dragonfly.fontDesign.retrieveOrBuild("", 15)
            .drawString("v${Dragonfly.version}", 5, height - 10, Color(0, 0, 0, 50).rgb)
    }

    /**
     * Passes the mouse input on to the list.
     */
    override fun handleMouseInput()
    {
        uiList.handleMouseInput()
        super.handleMouseInput()
    }

    /**
     * Fires when a button is pressed.
     *
     * In this GUI, the only available button is the "Save & Exit Button" which shows up the previous gui screen.
     */
    override fun actionPerformed(button: GuiButton?)
    {
        if (button?.id == 0)
        {
            uiList.entries.filter { it is ExternalApplier<*> }
                .map { it as ExternalApplier<*> }
                .forEach { it.applyExternally() }

            mc.displayGuiScreen(previousScreen)
        } else if (button?.id == 1)
        {
            TimeUtils.requireDelay("ingame-reload-button", 1000L) { Dragonfly.reload() }
        }
    }

    /**
     * Notifies the [uiList] when the mouse is pressed.
     */
    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int)
    {
        uiList.mousePressed(mouseX, mouseY, mouseButton)
        super.mouseClicked(mouseX, mouseY, mouseButton)
    }

    /**
     * Notifies the [uiList] when the mouse is dragged.
     */
    override fun mouseClickMove(mouseX: Int, mouseY: Int, clickedMouseButton: Int, timeSinceLastClick: Long)
    {
        uiList.mouseDragged(mouseX, mouseY, clickedMouseButton, timeSinceLastClick)
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick)
    }

    /**
     * Notifies the [uiList] when the mouse is released.
     */
    override fun mouseReleased(mouseX: Int, mouseY: Int, state: Int)
    {
        uiList.mouseReleased(mouseX, mouseY, state)
        super.mouseReleased(mouseX, mouseY, state)
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen).
     * This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     * Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    override fun keyTyped(typedChar: Char, keyCode: Int)
    {
        uiList.keyTyped(typedChar, keyCode)
        super.keyTyped(typedChar, keyCode)
    }
}