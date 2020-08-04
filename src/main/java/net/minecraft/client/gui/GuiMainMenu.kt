package net.minecraft.client.gui

import net.inceptioncloud.dragonfly.Dragonfly.fontManager
import net.inceptioncloud.dragonfly.engine.font.FontWeight
import net.inceptioncloud.dragonfly.engine.font.renderer.IFontRenderer
import net.inceptioncloud.dragonfly.transition.number.DoubleTransition
import net.inceptioncloud.dragonfly.transition.number.SmoothDoubleTransition
import net.inceptioncloud.dragonfly.transition.supplier.ForwardBackward
import net.inceptioncloud.dragonfly.ui.components.button.TransparentButton
import net.inceptioncloud.dragonfly.ui.mainmenu.QuickAction
import net.inceptioncloud.dragonfly.ui.mainmenu.multiplayer.DirectConnectAction
import net.inceptioncloud.dragonfly.ui.mainmenu.multiplayer.LastServerAction
import net.inceptioncloud.dragonfly.ui.mainmenu.options.ModOptionsAction
import net.inceptioncloud.dragonfly.ui.mainmenu.options.ResourcePackAction
import net.inceptioncloud.dragonfly.ui.mainmenu.quit.ReloadAction
import net.inceptioncloud.dragonfly.ui.mainmenu.quit.RestartAction
import net.inceptioncloud.dragonfly.ui.mainmenu.singleplayer.CreateMapAction
import net.inceptioncloud.dragonfly.ui.mainmenu.singleplayer.LastMapAction
import net.inceptioncloud.dragonfly.ui.renderer.RenderUtils
import net.inceptioncloud.dragonfly.ui.screens.AboutUI
import net.inceptioncloud.dragonfly.versioning.DragonflyVersion
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.resources.I18n
import net.minecraft.util.EnumChatFormatting
import net.minecraft.util.ResourceLocation
import org.apache.logging.log4j.LogManager
import org.lwjgl.opengl.GLContext
import java.awt.Color
import java.io.IOException
import java.net.URI
import java.net.URL
import java.util.*
import java.util.function.Consumer
import kotlin.math.max
import kotlin.math.min

class GuiMainMenu : GuiScreen(), GuiYesNoCallback {

    /**
     * Provides all available quick actions.
     */
    private val availableActions: List<QuickAction> = listOf(
        LastMapAction(), CreateMapAction(),
        LastServerAction(), DirectConnectAction(),
        ResourcePackAction(), ModOptionsAction(),
        RestartAction(), ReloadAction()
    )

    /**
     * The Object object utilized as a thread lock when performing non thread-safe operations
     */
    private val threadLock = Any()

    /**
     * The transitions that are responsible for the different Quick Action Buttons.
     */
    private val quickActionTransitions: MutableMap<Int, DoubleTransition> = HashMap()
    private val aboutString = "About Dragonfly"

    /**
     * The transition that lets the navigation bar rise when it's hovered.
     */
    private var riseTransition: SmoothDoubleTransition? = null

    /**
     * Whether the navbar is currently hovered.
     */
    private var navbarHovered = false
    private var openGLWarning1: String

    /**
     * OpenGL graphics card warning.
     */
    private var openGLWarning2: String

    /**
     * Link to the Mojang Support about minimum requirements
     */
    private var openGLWarningLink: String? = null
    private var field92022T = 0
    private var field92021U = 0
    private var field92020V = 0
    private var field92019W = 0

    /**
     * Button Information...
     */
    private var buttonWidth = 0
    private var buttonHeight = 0
    private var buttonSpace = 0
    private var buttonY = 0
    private var quickActionLeft = 0
    private var quickActionRight = 0

    /**
     * The ID of the button which is selected in order to draw it's sub-modules.
     */
    private var selectedButton = 0

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the window resizes, the buttonList is cleared beforehand.
     */
    override fun initGui() {
        riseTransition = SmoothDoubleTransition.builder()
            .start(1.0)
            .end(2.0)
            .fadeIn(0)
            .stay(10)
            .fadeOut(20)
            .autoTransformator(ForwardBackward { navbarHovered })
            .build()
        mc.isConnectedToRealms = false
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        if (drawTime == -1L) drawTime = System.currentTimeMillis()
        this.drawGradientBackground()
        val finalFontRenderer = updateSize()

        if (finalFontRenderer != null && buttonList.isEmpty()) {
            addButtons()
            synchronized(threadLock) {
                val field92023S = fontRendererObj!!.getStringWidth(openGLWarning1)
                val field92024R = fontRendererObj!!.getStringWidth(openGLWarning2)
                val k = max(field92023S, field92024R)
                field92022T = (width - k) / 2
                field92021U = buttonList[0].yPosition - 24
                field92020V = field92022T + k
                field92019W = field92021U + 24
            }
        }

        navbarHovered = mouseY >= height - navbarHeight && mouseY <= height
        buttonList.stream()
            .filter { guiButton: GuiButton -> guiButton.id < 10 }
            .filter { obj: GuiButton? -> TransparentButton::class.java.isInstance(obj) }
            .map { obj: GuiButton? -> TransparentButton::class.java.cast(obj) }
            .forEach { transparentButton: TransparentButton ->
                transparentButton.isHighlighted = !riseTransition!!.isAtStart && selectedButton == transparentButton.id
                transparentButton.setFontRenderer(finalFontRenderer)
                transparentButton.setPositionY(buttonY)
                if (transparentButton.isMouseOver) selectedButton = transparentButton.id
            }

        // All quick-action buttons
        buttonList.stream()
            .filter { guiButton: GuiButton -> guiButton.id >= 10 }
            .filter { obj: GuiButton? -> TransparentButton::class.java.isInstance(obj) }
            .map { obj: GuiButton? -> TransparentButton::class.java.cast(obj) }
            .forEach { transparentButton: TransparentButton ->
                val percent = quickActionTransitions[transparentButton.id]!!.get()
                transparentButton.setPositionY((height - buttonHeight * 1.7 * percent).toInt())
                transparentButton.setFontRenderer(finalFontRenderer)
            }

        // Logo
        val initialHeight = height / 20
        val imageSize = (height / 3).coerceAtMost(300)
        val image = ResourceLocation("dragonflyres/logos/white_outline.png")
        RenderUtils.drawImage(image, width / 2 - imageSize / 2 + 2, initialHeight + 2, imageSize, imageSize, 0f, 0f, 0f, 0.4f)
        RenderUtils.drawImage(image, width / 2 - imageSize / 2, initialHeight, imageSize, imageSize)

        // Title
        val percent = imageSize / 280.0
        var fontRenderer = fontManager.defaultFont.fontRenderer(fontWeight = FontWeight.MEDIUM, size = (25 + percent * 60).toInt())
        fontRenderer.drawCenteredString("Inception Cloud Dragonfly", width / 2, initialHeight + imageSize + 10, 0xFFFFFF, true)

        // Subtitle
        var previousHeight = fontRenderer.height
        fontRenderer = fontManager.defaultFont.fontRenderer(size = (15 + percent * 40).toInt())
        fontRenderer.drawCenteredString(DragonflyVersion.string, width / 2, initialHeight + imageSize + 12 + previousHeight, 0xFFFFFF, true)

        // Update title
        val updateTitle = DragonflyVersion.update?.title
        updateTitle?.let {
            previousHeight += fontRenderer.height
            fontRenderer = fontManager.defaultFont.fontRenderer(size = (10 + percent * 30).toInt())
            fontRenderer.drawCenteredString(it, width / 2, initialHeight + imageSize + 13 + previousHeight, Color(255, 255, 255, 200).rgb, true)
        }

        // About
        fontRenderer = fontManager.defaultFont.fontRenderer()
        fontRenderer.drawString(aboutString, 5f, 5f, Color.WHITE.rgb, true)

        // What's new?
        if (DragonflyVersion.update?.patchNotes != null) {
            val s = "What's new?"
            fontRenderer.drawString(s, width - 5f - fontRenderer.getStringWidth(s), 5f, Color.WHITE.rgb, true)
        }

        // Bottom Bar
        drawRect(0, height - navbarHeight, width, height, Color(0, 0, 0, 100).rgb)

        // Buttons
        super.drawScreen(mouseX, mouseY, partialTicks)
        buttonList.remove(buttonList.firstOrNull { guiButton: GuiButton -> guiButton.id == 5 })

        // Fade-In Overlay
//        drawRect(0, 0, width, height, RGB.of(GreyToneColor.DARK_GREY)
//            .alpha((float) fadeInTransition.get())
//            .rgb());
    }

    /**
     * Changes the font and button size when the window size is updated (by rescaling or toggling fullscreen).
     */
    private fun updateSize(): IFontRenderer? {
        val percent = min(height / 540.0, 1.0)
        val buttonFontSize = (18 + percent * 15).toInt()
        val fontRenderer = fontManager.defaultFont.fontRendererAsync(size = buttonFontSize)
        if (fontRenderer != null) {
            buttonWidth = (80 + percent * 30).toInt()
            buttonHeight = fontRenderer.height
            buttonSpace = 10
            buttonY = height - navbarHeight + buttonHeight / 2
            quickActionLeft = (width / 2 - buttonSpace * 1.5 - buttonWidth * 2 + 10).toInt()
            quickActionRight = (width / 2 + buttonSpace * 1.5 + buttonWidth * 2 - 10).toInt()
        }
        return fontRenderer
    }

    /**
     * Adds Singleplayer and Multiplayer buttons on Main Menu for players who have bought the game.<br></br>
     *
     * **The following button IDs are used:**<br></br>
     * `0` Singleplayer<br></br>
     * `1` Multiplayer<br></br>
     * `2` Options<br></br>
     * `3` Quit Game
     */
    fun addButtons() {
        buttonList.add(
            TransparentButton(
                0,
                (width / 2 - buttonSpace * 1.5 - buttonWidth * 2).toInt(),
                buttonY,
                buttonWidth,
                buttonHeight,
                I18n.format("menu.singleplayer")
            )
        )
        buttonList.add(
            TransparentButton(
                1,
                width / 2 - buttonSpace / 2 - buttonWidth,
                buttonY,
                buttonWidth,
                buttonHeight,
                I18n.format("menu.multiplayer")
            )
        )
        buttonList.add(
            TransparentButton(
                2,
                width / 2 + buttonSpace / 2,
                buttonY,
                buttonWidth,
                buttonHeight,
                I18n.format("menu.options")
            )
        )
        buttonList.add(
            TransparentButton(
                3,
                (width / 2 + buttonSpace * 1.5 + buttonWidth).toInt(),
                buttonY,
                buttonWidth,
                buttonHeight,
                I18n.format("menu.quit")
            )
        )
        val fontRenderer = updateSize()
        var left = true
        for (quickAction in availableActions) {
            val stringWidth = fontRenderer?.getStringWidth(quickAction.display) ?: 0
            val xPosition = if (left) quickActionLeft + 50 else quickActionRight - stringWidth - 50
            val buttonId = quickAction.ownButtonId
            buttonList.add(TransparentButton(buttonId, xPosition, height, stringWidth, 20, quickAction.display).setOpacity(0.5f))
            quickActionTransitions[buttonId] = DoubleTransition.builder()
                .start(0.0)
                .end(1.0)
                .amountOfSteps(20)
                .autoTransformator(ForwardBackward { riseTransition!!.isAtEnd && isQuickActionSelected(buttonId) })
                .build()
            left = !left
        }
    }

    /**
     * Checks whether the Quick Action represented by the Button is currently selected.
     */
    private fun isQuickActionSelected(quickActionButtonId: Int): Boolean {
        val actions: List<QuickAction> = availableActions
            .filter { action: QuickAction? -> action!!.headButtonId == selectedButton }
            .toList()
        return (quickActionButtonId == actions[0].ownButtonId || quickActionButtonId == actions[1].ownButtonId)
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    override fun doesGuiPauseGame(): Boolean {
        return false
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    @Throws(IOException::class)
    override fun actionPerformed(button: GuiButton?) {
        when (button!!.id) {
            0 -> mc.displayGuiScreen(GuiSelectWorld(this))
            1 -> mc.displayGuiScreen(GuiMultiplayer(this))
            2 -> mc.displayGuiScreen(GuiOptions(this, mc.gameSettings))
            3 -> mc.shutdown()
        }
        if (button.id >= 10) getActionByButtonId(button.id)!!.handleClick.run()
    }

    /**
     * Tries to find the quick action that belongs to the given button id.
     */
    private fun getActionByButtonId(buttonId: Int): QuickAction? {
        return availableActions.stream()
            .filter { quickAction: QuickAction? -> quickAction!!.ownButtonId == buttonId }
            .findFirst()
            .orElse(null)
    }

    override fun confirmClicked(result: Boolean, id: Int) {
        if (result && id == 12) {
            val isaveformat = mc.saveLoader
            isaveformat.flushCache()
            isaveformat.deleteWorldDirectory("Demo_World")
            mc.displayGuiScreen(this)
        } else if (id == 13) {
            if (result) {
                try {
                    val oclass = Class.forName("java.awt.Desktop")
                    val `object` = oclass.getMethod("getDesktop", *arrayOfNulls(0)).invoke(null)
                    oclass.getMethod("browse", URI::class.java).invoke(`object`, URI(openGLWarningLink!!))
                } catch (throwable: Throwable) {
                    logger.error("Couldn't open link", throwable)
                }
            }
            mc.displayGuiScreen(this)
        }
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    @Throws(IOException::class)
    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        super.mouseClicked(mouseX, mouseY, mouseButton)
        synchronized(threadLock) {
            if (openGLWarning1.isNotEmpty() && mouseX >= field92022T && mouseX <= field92020V && mouseY >= field92021U && mouseY <= field92019W) {
                val guiConfirmOpenLink = GuiConfirmOpenLink(this, openGLWarningLink, 13, true)
                guiConfirmOpenLink.disableSecurityWarning()
                mc.displayGuiScreen(guiConfirmOpenLink)
            }
        }

        val fontRenderer = fontManager.defaultFont.fontRendererAsync()
        if (mouseY in 5..5 + (fontRenderer?.height ?: 0)) {
            if (mouseX in 5..5 + (fontRenderer?.getStringWidth(aboutString) ?: 0)) {
                mc.displayGuiScreen(AboutUI(this))
            } else {
                val s = "What's new?"
                val sWidth = fontRenderer?.getStringWidth(s) ?: 0
                if (mouseX in width - sWidth - 5..width - 5) {
                    openWebLink(URL(DragonflyVersion.update?.patchNotes ?: "https://google.net").toURI())
                }
            }
        }
    }

    override fun onGuiClosed() {
        riseTransition!!.destroy()
        quickActionTransitions.values.forEach(Consumer { obj: DoubleTransition -> obj.destroy() })
        super.onGuiClosed()
    }

    /**
     * @return The height of the navigation bar.
     */
    private val navbarHeight: Int
        get() = (buttonHeight * 2 * riseTransition!!.get()).toInt()

    companion object {
        val informationText =
            "Please click " + EnumChatFormatting.UNDERLINE + "here" + EnumChatFormatting.RESET + " for more information."
        private val logger = LogManager.getLogger()

        /**
         * The time when the GUI was first drawn.
         *
         * @see .addButtons
         */
        private var drawTime: Long = -1
    }

    /**
     * Default Constructor
     */
    init {
        openGLWarning2 = informationText
        openGLWarning1 = ""
        if (!GLContext.getCapabilities().OpenGL20 && !OpenGlHelper.areShadersSupported()) {
            openGLWarning1 = I18n.format("title.oldgl1")
            openGLWarning2 = I18n.format("title.oldgl2")
            openGLWarningLink = "https://help.mojang.com/customer/portal/articles/325948?ref=game"
        }
    }
}
