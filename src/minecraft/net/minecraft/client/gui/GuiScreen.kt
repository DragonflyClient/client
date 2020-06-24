package net.minecraft.client.gui

import com.google.common.base.Splitter
import com.google.common.collect.Lists
import com.google.common.collect.Sets
import net.inceptioncloud.minecraftmod.Dragonfly
import net.inceptioncloud.minecraftmod.design.color.CloudColor
import net.inceptioncloud.minecraftmod.engine.internal.*
import net.inceptioncloud.minecraftmod.key.ui.AttachingKeyUI
import net.inceptioncloud.minecraftmod.ui.components.button.ConfirmationButton
import net.inceptioncloud.minecraftmod.ui.renderer.RenderUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.stream.GuiTwitchUserMode
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.entity.RenderItem
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.EntityList
import net.minecraft.event.ClickEvent
import net.minecraft.event.HoverEvent
import net.minecraft.item.ItemStack
import net.minecraft.nbt.*
import net.minecraft.stats.Achievement
import net.minecraft.stats.StatList
import net.minecraft.util.*
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.LogManager
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import java.awt.Color
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection
import java.io.File
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException
import java.util.*
import java.util.function.Consumer

abstract class GuiScreen : Gui(), GuiYesNoCallback {
    /**
     * The width of the screen object.
     */
    @JvmField
    var width = 0

    /**
     * The height of the screen object.
     */
    @JvmField
    var height = 0

    @JvmField
    var allowUserInput = false

    @JvmField
    var buttonList: MutableList<GuiButton> = Lists.newArrayList()

    /**
     * Reference to the Minecraft object.
     */
    protected lateinit var mc: Minecraft

    /**
     * Holds a instance of RenderItem, used to draw the achievement icons on screen (is based on ItemStack)
     */
    @JvmField
    protected var itemRender: RenderItem? = null

    @JvmField
    protected var labelList: List<GuiLabel> = Lists.newArrayList()

    /**
     * The FontManager used by GuiScreen
     */
    @JvmField
    protected var fontRendererObj: FontRenderer? = null

    /**
     * The button that was just pressed.
     */
    private var selectedButton: GuiButton? = null
    private var eventButton = 0
    private var lastMouseEvent: Long = 0

    /**
     * Incremented when the game is in touchscreen mode and the screen is tapped, decremented if the screen isn't tapped. Does not appear to be used.
     */
    private var touchValue = 0
    private var clickedLinkURI: URI? = null

    @JvmField
    var buffer = WidgetBuffer()

    open var backgroundFill: WidgetColor? = null

    /**
     * Draws a gradient background with the default colors.
     */
    fun drawGradientBackground() {
        val startColor = CloudColor.DESIRE.rgb
        val endColor = CloudColor.ROYAL.rgb
        drawGradientBackground(startColor, endColor)
    }

    /**
     * Draws a gradient from the left top to the right bottom corner with specific colors.
     */
    protected fun drawGradientBackground(leftTop: Int, rightBottom: Int) {
        drawGradientLeftTopRightBottom(0, 0, width, height, leftTop, rightBottom)
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    open fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        for (guiButton in ArrayList(buttonList)) {
            guiButton.drawButton(mc, mouseX, mouseY)
        }
        for (guiLabel in ArrayList(labelList)) {
            guiLabel.drawLabel(mc, mouseX, mouseY)
        }
        buffer.render()
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    @Throws(IOException::class)
    protected open fun keyTyped(typedChar: Char, keyCode: Int) {

        buffer.handleKeyTyped(typedChar, keyCode)

        if (keyCode == 1) {
            mc.displayGuiScreen(null)
            if (mc.currentScreen == null) {
                mc.setIngameFocus()
            }
        } else if (keyCode == Keyboard.KEY_PERIOD && isCtrlKeyDown && isShiftKeyDown) {
            Dragonfly.isDeveloperMode = !Dragonfly.isDeveloperMode
        } else if (Dragonfly.isDeveloperMode) {
            // ICMM: Developer Mode Hotkeys
            when (keyCode) {
                Keyboard.KEY_F5 -> {
                    buttonList.clear()
                    buffer.clear()
                    onGuiClosed()
                    initGui()
                }
                Keyboard.KEY_F7 -> mc.displayGuiScreen(AttachingKeyUI("L9AJOT-XI25G0F9-QWJB3W5K-94JQD1"))
            }
        }
    }

    protected open fun renderToolTip(stack: ItemStack, x: Int, y: Int) {
        val list = stack.getTooltip(mc.thePlayer, mc.gameSettings.advancedItemTooltips)
        for (i in list.indices) {
            if (i == 0) {
                list[i] = stack.rarity.rarityColor.toString() + list[i]
            } else {
                list[i] = EnumChatFormatting.GRAY.toString() + list[i]
            }
        }
        drawHoveringText(list, x, y)
    }

    /**
     * Draws the text when mouse is over creative inventory tab. Params: current creative tab to be checked, current mouse x position, current mouse y position.
     */
    protected open fun drawCreativeTabHoveringText(tabName: String, mouseX: Int, mouseY: Int) {
        drawHoveringText(listOf(tabName), mouseX, mouseY)
    }

    /**
     * Draws a List of strings as a tooltip. Every entry is drawn on a seperate line.
     */
    protected open fun drawHoveringText(textLines: List<String>, x: Int, y: Int) {
        if (textLines.isNotEmpty()) {
            GlStateManager.disableRescaleNormal()
            RenderHelper.disableStandardItemLighting()
            GlStateManager.disableLighting()
            GlStateManager.disableDepth()
            var i = 0
            val fontRenderer = Dragonfly.fontDesign.regular
            for (s in textLines) {
                val j = fontRenderer.getStringWidth(s)
                if (j > i) {
                    i = j
                }
            }
            var left = x + 12
            var top = y - 12
            var height = 8
            if (textLines.size > 1) {
                height += 2 + (textLines.size - 1) * 10
            }
            if (left + i > width) {
                left -= 28 + i
            }
            if (top + height + 6 > this.height) {
                top = this.height - height - 6
            }
            zLevel = 300.0f
            itemRender!!.zLevel = 300.0f
            val l = -267386864
            drawGradientVertical(left - 3, top - 4, left + i + 3, top - 3, l, l)
            drawGradientVertical(left - 3, top + height + 3, left + i + 3, top + height + 4, l, l)
            drawGradientVertical(left - 3, top - 3, left + i + 3, top + height + 3, l, l)
            drawGradientVertical(left - 4, top - 3, left - 3, top + height + 3, l, l)
            drawGradientVertical(left + i + 3, top - 3, left + i + 4, top + height + 3, l, l)
            val i1 = 1347420415
            val j1 = i1 and 16711422 shr 1 or i1 and -16777216
            drawGradientVertical(left - 3, top - 3 + 1, left - 3 + 1, top + height + 3 - 1, i1, j1)
            drawGradientVertical(left + i + 2, top - 3 + 1, left + i + 3, top + height + 3 - 1, i1, j1)
            drawGradientVertical(left - 3, top - 3, left + i + 3, top - 3 + 1, i1, i1)
            drawGradientVertical(left - 3, top + height + 2, left + i + 3, top + height + 3, j1, j1)
            top += 1
            for (k1 in textLines.indices) {
                val s1 = textLines[k1]
                fontRenderer.drawStringWithShadow(s1, left.toFloat(), top.toFloat(), -1)
                if (k1 == 0) {
                    top += 1
                }
                top += 10
            }
            zLevel = 0.0f
            itemRender!!.zLevel = 0.0f
            GlStateManager.enableLighting()
            GlStateManager.enableDepth()
            RenderHelper.enableStandardItemLighting()
            GlStateManager.enableRescaleNormal()
        }
    }

    /**
     * Draws the hover event specified by the given chat component
     */
    protected fun handleComponentHover(p_175272_1_: IChatComponent?, p_175272_2_: Int, p_175272_3_: Int) {
        if (p_175272_1_ != null && p_175272_1_.chatStyle.chatHoverEvent != null) {
            val hoverevent = p_175272_1_.chatStyle.chatHoverEvent
            if (hoverevent.action == HoverEvent.Action.SHOW_ITEM) {
                var itemstack: ItemStack? = null
                try {
                    val nbtbase: NBTBase = JsonToNBT.getTagFromJson(hoverevent.value.unformattedText)
                    if (nbtbase is NBTTagCompound) {
                        itemstack = ItemStack.loadItemStackFromNBT(nbtbase)
                    }
                } catch (var11: NBTException) {
                }
                if (itemstack != null) {
                    renderToolTip(itemstack, p_175272_2_, p_175272_3_)
                } else {
                    drawCreativeTabHoveringText(
                        EnumChatFormatting.RED.toString() + "Invalid Item!",
                        p_175272_2_,
                        p_175272_3_
                    )
                }
            } else if (hoverevent.action == HoverEvent.Action.SHOW_ENTITY) {
                if (mc.gameSettings.advancedItemTooltips) {
                    try {
                        val nbtbase1: NBTBase = JsonToNBT.getTagFromJson(hoverevent.value.unformattedText)
                        if (nbtbase1 is NBTTagCompound) {
                            val list1: MutableList<String> = Lists.newArrayList()
                            val nbttagcompound = nbtbase1
                            list1.add(nbttagcompound.getString("name"))
                            if (nbttagcompound.hasKey("type", 8)) {
                                val s = nbttagcompound.getString("type")
                                list1.add("Type: " + s + " (" + EntityList.getIDFromString(s) + ")")
                            }
                            list1.add(nbttagcompound.getString("id"))
                            drawHoveringText(list1, p_175272_2_, p_175272_3_)
                        } else {
                            drawCreativeTabHoveringText(
                                EnumChatFormatting.RED.toString() + "Invalid Entity!",
                                p_175272_2_,
                                p_175272_3_
                            )
                        }
                    } catch (var10: NBTException) {
                        drawCreativeTabHoveringText(
                            EnumChatFormatting.RED.toString() + "Invalid Entity!",
                            p_175272_2_,
                            p_175272_3_
                        )
                    }
                }
            } else if (hoverevent.action == HoverEvent.Action.SHOW_TEXT) {
                drawHoveringText(NEWLINE_SPLITTER.splitToList(hoverevent.value.formattedText), p_175272_2_, p_175272_3_)
            } else if (hoverevent.action == HoverEvent.Action.SHOW_ACHIEVEMENT) {
                val statbase = StatList.getOneShotStat(hoverevent.value.unformattedText)
                if (statbase != null) {
                    val ichatcomponent = statbase.statName
                    val ichatcomponent1: IChatComponent =
                        ChatComponentTranslation("stats.tooltip.type." + if (statbase.isAchievement) "achievement" else "statistic")
                    ichatcomponent1.chatStyle.italic = java.lang.Boolean.valueOf(true)
                    val s1 = if (statbase is Achievement) statbase.description else null
                    val list: MutableList<String> =
                        Lists.newArrayList(ichatcomponent.formattedText, ichatcomponent1.formattedText)
                    if (s1 != null) {
                        list.addAll(fontRendererObj!!.listFormattedStringToWidth(s1, 150))
                    }
                    drawHoveringText(list, p_175272_2_, p_175272_3_)
                } else {
                    drawCreativeTabHoveringText(
                        EnumChatFormatting.RED.toString() + "Invalid statistic/achievement!",
                        p_175272_2_,
                        p_175272_3_
                    )
                }
            }
            GlStateManager.disableLighting()
        }
    }

    /**
     * Sets the text of the chat
     */
    protected open fun setText(newChatText: String?, shouldOverwrite: Boolean) {
    }

    /**
     * Executes the click event specified by the given chat component
     */
    protected open fun handleComponentClick(p_175276_1_: IChatComponent?): Boolean {
        if (p_175276_1_ != null) {
            val clickevent = p_175276_1_.chatStyle.chatClickEvent
            if (isShiftKeyDown) {
                if (p_175276_1_.chatStyle.insertion != null) {
                    setText(p_175276_1_.chatStyle.insertion, false)
                }
            } else if (clickevent != null) {
                if (clickevent.action == ClickEvent.Action.OPEN_URL) {
                    if (!mc.gameSettings.chatLinks) {
                        return false
                    }
                    try {
                        val uri = URI(clickevent.value)
                        val s = uri.scheme ?: throw URISyntaxException(clickevent.value, "Missing protocol")
                        if (!PROTOCOLS.contains(s.toLowerCase())) {
                            throw URISyntaxException(clickevent.value, "Unsupported protocol: " + s.toLowerCase())
                        }
                        if (mc.gameSettings.chatLinksPrompt) {
                            clickedLinkURI = uri
                            mc.displayGuiScreen(GuiConfirmOpenLink(this, clickevent.value, 31102009, false))
                        } else {
                            openWebLink(uri)
                        }
                    } catch (urisyntaxexception: URISyntaxException) {
                        LOGGER.error("Can't open url for $clickevent", urisyntaxexception)
                    }
                } else if (clickevent.action == ClickEvent.Action.OPEN_FILE) {
                    val uri1 = File(clickevent.value).toURI()
                    openWebLink(uri1)
                } else if (clickevent.action == ClickEvent.Action.SUGGEST_COMMAND) {
                    setText(clickevent.value, true)
                } else if (clickevent.action == ClickEvent.Action.RUN_COMMAND) {
                    sendChatMessage(clickevent.value, false)
                } else if (clickevent.action == ClickEvent.Action.TWITCH_USER_INFO) {
                    val chatuserinfo = mc.twitchStream.func_152926_a(clickevent.value)
                    if (chatuserinfo != null) {
                        mc.displayGuiScreen(GuiTwitchUserMode(mc.twitchStream, chatuserinfo))
                    } else {
                        LOGGER.error("Tried to handle twitch user but couldn't find them!")
                    }
                } else {
                    LOGGER.error("Don't know how to handle $clickevent")
                }
                return true
            }
        }
        return false
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    @Throws(IOException::class)
    protected open fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        if (mouseButton == 0) {
            for (guibutton in ArrayList(buttonList)) {
                if (guibutton is ConfirmationButton) continue
                if (guibutton.mousePressed(mc, mouseX, mouseY)) {
                    buttonClick(guibutton)
                }
            }
        }
    }

    fun buttonClick(guibutton: GuiButton) {
        try {
            selectedButton = guibutton
            guibutton.playPressSound(mc.soundHandler)
            actionPerformed(guibutton)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * Called when a mouse button is released.  Args : mouseX, mouseY, releaseButton
     */
    protected open fun mouseReleased(mouseX: Int, mouseY: Int, state: Int) {
        if (selectedButton != null && state == 0) {
            selectedButton!!.mouseReleased(mouseX, mouseY)
            selectedButton = null
        }
    }

    /**
     * Called when a mouse button is pressed and the mouse is moved around. Parameters are : mouseX, mouseY, lastButtonClicked & timeSinceMouseClick.
     */
    protected open fun mouseClickMove(mouseX: Int, mouseY: Int, clickedMouseButton: Int, timeSinceLastClick: Long) {
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    @Throws(IOException::class)
    protected open fun actionPerformed(button: GuiButton?) {
    }

    /**
     * Causes the screen to lay out its subcomponents again. This is the equivalent of the Java call Container.validate()
     */
    open fun setWorldAndResolution(mc: Minecraft, width: Int, height: Int) {
        this.mc = mc
        this.width = width
        this.height = height

        itemRender = mc.renderItem
        fontRendererObj = mc.fontRendererObj
        scaleFactor = ScaledResolution(mc).scaleFactor
        buttonList.clear()

        initGui()
    }

    var scaleFactor = 0

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the window resizes, the buttonList is cleared beforehand.
     */
    open fun initGui() {
    }

    /**
     * Delegates mouse and keyboard input.
     */
    @Throws(IOException::class)
    fun handleInput() {
        if (Mouse.isCreated()) {
            while (Mouse.next()) {
                handleMouseInput()
            }
        }
        if (Keyboard.isCreated()) {
            while (Keyboard.next()) {
                handleKeyboardInput()
            }
        }
    }

    /**
     * Draws a red background on the screen that indicates that the window must be bigger.
     *
     *
     * This method can be used by gui screens that need the screen to have at least a certain size.
     * When the size isn't enough, screen mustn't render it's content but instead call this method.
     * It doesn't use any font renderer as these can cause the game to crash when below a certain size.
     */
    protected fun drawSizeNotSupported() {
        drawRect(0, 0, width, height, Color(0xeb3b5a).rgb)
        val line = Math.max(5.0, Math.min(50.0, mc.displayHeight / 2.5)).toInt()
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        RenderUtils.drawLine(15.0, 15.0, 15.0, line.toDouble(), 4f)
        RenderUtils.drawLine(13.0, 15.0, line.toDouble(), 15.0, 4f)
        RenderUtils.drawLine(15.0, 15.0, line.toDouble(), line.toDouble(), 6f)
        RenderUtils.drawLine(width - 15.toDouble(), 15.0, width - 15.toDouble(), line.toDouble(), 4f)
        RenderUtils.drawLine(width - line.toDouble(), 15.0, width - 13.toDouble(), 15.0, 4f)
        RenderUtils.drawLine(width - 15.toDouble(), 15.0, width - line.toDouble(), line.toDouble(), 6f)
        RenderUtils.drawLine(15.0, height - 15.toDouble(), 15.0, height - line.toDouble(), 4f)
        RenderUtils.drawLine(13.0, height - 15.toDouble(), line.toDouble(), height - 15.toDouble(), 4f)
        RenderUtils.drawLine(15.0, height - 16.toDouble(), line.toDouble(), height - line.toDouble(), 6f)
        RenderUtils.drawLine(
            width - 15.toDouble(),
            height - 15.toDouble(),
            width - 15.toDouble(),
            height - line.toDouble(),
            4f
        )
        RenderUtils.drawLine(
            width - line.toDouble(),
            height - 15.toDouble(),
            width - 13.toDouble(),
            height - 15.toDouble(),
            4f
        )
        RenderUtils.drawLine(
            width - 15.toDouble(),
            height - 16.toDouble(),
            width - line.toDouble(),
            height - line.toDouble(),
            6f
        )
        drawRect(line + 15, line + 15, width - line - 15, height - line - 15, Color.WHITE.rgb)
        drawRect(
            line + 19, line + 19, width - line - 19, height - line - 19,
            Color(0xeb3b5a).darker().darker().rgb
        )
    }

    /**
     * Draws the background with the [backgroundFill] color.
     */
    protected fun drawBackgroundFill() {
        backgroundFill?.let { drawRect(0, 0, width, height, backgroundFill?.rgb ?: 0xFFFFFFFF.toInt()) }
    }

    /**
     * Handles mouse input.
     */
    @Throws(IOException::class)
    open fun handleMouseInput() {
        val mouseX = Mouse.getEventX() * width / mc.displayWidth
        val mouseY = height - Mouse.getEventY() * height / mc.displayHeight - 1
        val k = Mouse.getEventButton()
        if (Mouse.getEventButtonState()) {
            if (mc.gameSettings.touchscreen && touchValue++ > 0) {
                return
            }
            eventButton = k
            lastMouseEvent = Minecraft.getSystemTime()
            mouseClicked(mouseX, mouseY, eventButton)
            buffer.handleMousePress(MouseData(mouseX, mouseY, button = eventButton))
        } else if (k != -1) {
            if (mc.gameSettings.touchscreen && --touchValue > 0) {
                return
            }
            eventButton = -1
            mouseReleased(mouseX, mouseY, k)
            buffer.handleMouseRelease(MouseData(mouseX, mouseY, button = k))
        } else if (eventButton != -1 && lastMouseEvent > 0L) {
            val timeSinceLastClick = Minecraft.getSystemTime() - lastMouseEvent
            mouseClickMove(mouseX, mouseY, eventButton, timeSinceLastClick)
            buffer.handleMouseDrag(MouseData(mouseX, mouseY, button = k, draggingDuration = timeSinceLastClick))
        }
    }

    /**
     * Handles keyboard input.
     */
    @Throws(IOException::class)
    open fun handleKeyboardInput() {
        if (Keyboard.getEventKeyState()) {
            val eventCharacter = Keyboard.getEventCharacter()
            val eventKey = Keyboard.getEventKey()
            keyTyped(eventCharacter, eventKey)
        }

        mc.dispatchKeypresses()
    }

    /**
     * Called from the main game loop to update the screen.
     */
    open fun updateScreen() {
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    open fun onGuiClosed() {
        buttonList.forEach(Consumer { obj: GuiButton -> obj.destroy() })
        buttonList.clear()
    }

    /**
     * Draws either a gradient over the background screen (when it exists) or a flat gradient over background.png
     */
    open fun drawDefaultBackground() {
        drawWorldBackground(0)
    }

    open fun drawWorldBackground(tint: Int) {
        if (mc.theWorld != null) {
            drawGradientVertical(0, 0, width, height, -1072689136, -804253680)
        } else {
            drawBackground(tint)
        }
    }

    /**
     * Draws the background (i is always 0 as of 1.2.2)
     */
    fun drawBackground(tint: Int) {
        GlStateManager.disableLighting()
        GlStateManager.disableFog()
        val tessellator = Tessellator.getInstance()
        val worldrenderer = tessellator.worldRenderer
        mc.textureManager.bindTexture(optionsBackground)
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        val f = 32.0f
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR)
        worldrenderer.pos(0.0, height.toDouble(), 0.0).tex(0.0, height.toFloat() / 32.0 + tint).color(64, 64, 64, 255)
            .endVertex()
        worldrenderer.pos(width.toDouble(), height.toDouble(), 0.0)
            .tex(width.toFloat() / 32.0f.toDouble(), height.toFloat() / 32.0 + tint).color(64, 64, 64, 255).endVertex()
        worldrenderer.pos(width.toDouble(), 0.0, 0.0).tex(width.toFloat() / 32.0f.toDouble(), tint.toDouble())
            .color(64, 64, 64, 255).endVertex()
        worldrenderer.pos(0.0, 0.0, 0.0).tex(0.0, tint.toDouble()).color(64, 64, 64, 255).endVertex()
        tessellator.draw()
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    open fun doesGuiPauseGame(): Boolean {
        return true
    }

    override fun confirmClicked(result: Boolean, id: Int) {
        if (id == 31102009) {
            if (result) {
                openWebLink(clickedLinkURI)
            }
            clickedLinkURI = null
            mc.displayGuiScreen(this)
        }
    }

    private fun openWebLink(p_175282_1_: URI?) {
        try {
            val oclass = Class.forName("java.awt.Desktop")
            val `object` = oclass.getMethod("getDesktop", *arrayOfNulls(0)).invoke(null)
            oclass.getMethod("browse", *arrayOf<Class<*>>(URI::class.java)).invoke(`object`, p_175282_1_)
        } catch (throwable: Throwable) {
            LOGGER.error("Couldn't open link", throwable)
        }
    }

    /**
     * Called when the GUI is resized in order to update the world and the resolution
     */
    fun onResize(mcIn: Minecraft, width: Int, height: Int) {
        setWorldAndResolution(mcIn, width, height)
    }

    /**
     * An operator function that allows adding widgets to the buffer. After providing the widget,
     * an id for it must be specified with the infix function [WidgetIdBuilder.id].
     */
    operator fun <W : Widget<W>> W.unaryPlus(): WidgetIdBuilder<W> {
        return WidgetIdBuilder<W>(buffer, widget = this)
    }

    operator fun String.unaryMinus(): Widget<*>? {
        return buffer[this]
    }

    /**
     * Tries to get a widget and additionally cast it to the specified type. This will return
     * null if the widget was not found or cannot be cast.
     */
    @Suppress("UNCHECKED_CAST")
    fun <W : Widget<W>> getWidget(identifier: String): W? = buffer[identifier] as? W

    companion object {
        private val LOGGER = LogManager.getLogger()
        private val PROTOCOLS: Set<String> = Sets.newHashSet("http", "https")
        private val NEWLINE_SPLITTER = Splitter.on('\n')

        /**
         * Stores the given string in the system clipboard
         */
        @JvmStatic
        var clipboardString: String?
            get() {
                try {
                    val transferable = Toolkit.getDefaultToolkit().systemClipboard.getContents(null)
                    if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                        return transferable.getTransferData(DataFlavor.stringFlavor) as String
                    }
                } catch (ignored: Exception) {
                }
                return ""
            }
            set(copyText) {
                if (!StringUtils.isEmpty(copyText)) {
                    try {
                        val stringselection = StringSelection(copyText)
                        Toolkit.getDefaultToolkit().systemClipboard.setContents(stringselection, null)
                    } catch (ignored: Exception) {
                    }
                }
            }

        @JvmStatic
        @JvmOverloads
        fun sendChatMessage(msg: String?, addToChat: Boolean = true) {
            if (addToChat) {
                Minecraft.getMinecraft().ingameGUI.chatGUI.addToSentMessages(msg)
            }
            Minecraft.getMinecraft().thePlayer.sendChatMessage(msg)
        }

        /**
         * Returns true if either windows ctrl key is down or if either mac meta key is down
         */
        @JvmStatic
        val isCtrlKeyDown: Boolean
            get() = if (Minecraft.isRunningOnMac) Keyboard.isKeyDown(219) || Keyboard.isKeyDown(220) else Keyboard.isKeyDown(
                29
            ) || Keyboard.isKeyDown(157)

        /**
         * Returns true if either shift key is down
         */
        @JvmStatic
        val isShiftKeyDown: Boolean
            get() = Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54)

        /**
         * Returns true if either alt key is down
         */
        @JvmStatic
        val isAltKeyDown: Boolean
            get() = Keyboard.isKeyDown(56) || Keyboard.isKeyDown(184)

        @JvmStatic
        fun isKeyComboCtrlX(code: Int): Boolean {
            return code == 45 && isCtrlKeyDown && !isShiftKeyDown && !isAltKeyDown
        }

        @JvmStatic
        fun isKeyComboCtrlV(code: Int): Boolean {
            return code == 47 && isCtrlKeyDown && !isShiftKeyDown && !isAltKeyDown
        }

        @JvmStatic
        fun isKeyComboCtrlC(code: Int): Boolean {
            return code == 46 && isCtrlKeyDown && !isShiftKeyDown && !isAltKeyDown
        }

        @JvmStatic
        fun isKeyComboCtrlA(code: Int): Boolean {
            return code == 30 && isCtrlKeyDown && !isShiftKeyDown && !isAltKeyDown
        }
    }
}