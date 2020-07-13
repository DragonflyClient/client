package net.minecraft.client.gui

import com.google.common.collect.Lists
import net.inceptioncloud.dragonfly.Dragonfly.fontDesign
import net.inceptioncloud.dragonfly.options.sections.OptionsSectionChat
import net.inceptioncloud.dragonfly.transition.number.DoubleTransition
import net.minecraft.client.Minecraft
import net.minecraft.network.play.client.C14PacketTabComplete
import net.minecraft.util.*
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.LogManager
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import java.awt.Color
import java.io.IOException

open class GuiChat : GuiScreen {

    private val foundPlayerNames: MutableList<String> = Lists.newArrayList()

    /**
     * Chat entry field
     */
    @JvmField
    protected var inputField: GuiTextField? = null
    private var historyBuffer = ""

    /**
     * keeps position of which chat message you will select when you press up, (does not increase for duplicated
     * messages sent immediately after each other)
     */
    private var sentHistoryCursor = -1
    private var playerNamesFound = false
    private var waitingOnAutocomplete = false
    private var autocompleteIndex = 0

    /**
     * is the text that appears when you press the chat key and the input box appears pre-filled
     */
    private var defaultInputFieldText = ""

    /**
     * Whether the close of the chat gui was manually started by the user and not forced by the client.
     */
    private var manuallyClosed = false

    /**
     * Whether the message has been sent. In this case, the message should never be restored.
     */
    private var messageSent = false

    constructor()
    constructor(defaultText: String) {
        defaultInputFieldText = defaultText
        LogManager.getLogger().info("Default field text is $defaultText")
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    override fun initGui() {
        Keyboard.enableRepeatEvents(true)
        transition.setForward()
        sentHistoryCursor = mc.ingameGUI.chatGUI.sentMessages.size

        inputField = GuiTextField(
            0, fontDesign.regular, 5,
            height - 13, GuiNewChat.calculateChatboxWidth(mc.gameSettings.chatWidth) - 10, 12
        )
        inputField!!.maxStringLength = 100
        inputField!!.enableBackgroundDrawing = false
        inputField!!.isFocused = true
        inputField!!.text = defaultInputFieldText
        inputField!!.setCanLoseFocus(false)

        LogManager.getLogger().info("Input field text is ${inputField!!.text}")
        if (messageCache != null && (defaultInputFieldText == "" || messageCache!!.startsWith(defaultInputFieldText))) {
            inputField!!.text = messageCache
            messageCache = null
            LogManager.getLogger().info("Restored message ${inputField!!.text}")
        }
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawRect(
            0,
            height - transition.castToInt(),
            6 + GuiNewChat.calculateChatboxWidth(mc.gameSettings.chatWidth),
            height,
            Color(20, 20, 20, 100).rgb
        )
        if (transition.isAtEnd) inputField!!.drawTextBox()
        val iChatComponent = mc.ingameGUI.chatGUI.getChatComponent(Mouse.getX(), Mouse.getY())
        if (iChatComponent != null && iChatComponent.chatStyle.chatHoverEvent != null) {
            handleComponentHover(iChatComponent, mouseX, mouseY)
        }
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    override fun onGuiClosed() {
        if (shouldCacheMessage()) {
            LogManager.getLogger().info("Cached chat message before closure: " + inputField!!.text)
            messageCache = inputField!!.text
        }

        Keyboard.enableRepeatEvents(false)
        mc.ingameGUI.chatGUI.resetScroll()

        super.onGuiClosed()
    }

    /**
     * Returns whether the currently entered message should be cached depending on the
     * [messageRestoreMode][OptionsSectionChat.messageRestoreMode].
     */
    private fun shouldCacheMessage() = !messageSent && inputField!!.text.let { it.isNotEmpty() && it != "/" }
            && OptionsSectionChat.messageRestoreMode.key.get().let { it != 0 && ((it == 1 && !manuallyClosed) || it == 2) }

    /**
     * Called from the main game loop to update the screen.
     */
    override fun updateScreen() {
        inputField!!.updateCursorCounter()
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    @Throws(IOException::class)
    override fun keyTyped(typedChar: Char, keyCode: Int) {
        waitingOnAutocomplete = false
        if (keyCode == 15) {
            autocompletePlayerNames()
        } else {
            playerNamesFound = false
        }
        if (keyCode == 1) {
            manuallyClosed = true
            transition.setBackward()
        } else if (keyCode != 28 && keyCode != 156) {
            when (keyCode) {
                200 -> getSentHistory(-1)
                208 -> getSentHistory(1)
                201 -> mc.ingameGUI.chatGUI.scroll(mc.ingameGUI.chatGUI.lineCount - 1)
                209 -> mc.ingameGUI.chatGUI.scroll(-mc.ingameGUI.chatGUI.lineCount + 1)
                else -> inputField!!.textboxKeyTyped(typedChar, keyCode)
            }
        } else {
            val s = inputField!!.text.trim { it <= ' ' }
            if (s.isNotEmpty()) {
                messageToSend = s
            }
            manuallyClosed = true
            messageSent = true
            transition.setBackward()
        }
    }

    /**
     * Handles mouse input.
     */
    @Throws(IOException::class)
    override fun handleMouseInput() {
        super.handleMouseInput()
        var i = Mouse.getEventDWheel()
        if (i != 0) {
            if (i > 1) {
                i = 1
            }
            if (i < -1) {
                i = -1
            }
            if (!isShiftKeyDown) {
                i *= 7
            }
            mc.ingameGUI.chatGUI.scroll(i)
        }
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    @Throws(IOException::class)
    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        if (mouseButton == 0) {
            val iChatComponent = mc.ingameGUI.chatGUI.getChatComponent(Mouse.getX(), Mouse.getY())
            if (handleComponentClick(iChatComponent)) {
                return
            }
        }
        inputField!!.mouseClicked(mouseX, mouseY, mouseButton)
        super.mouseClicked(mouseX, mouseY, mouseButton)
    }

    /**
     * Sets the text of the chat
     */
    override fun setText(newChatText: String?, shouldOverwrite: Boolean) {
        if (shouldOverwrite) {
            inputField!!.text = newChatText
        } else {
            inputField!!.writeText(newChatText)
        }
    }

    private fun autocompletePlayerNames() {
        if (playerNamesFound) {
            inputField!!.deleteFromCursor(
                inputField!!.getNthWordFromPosWS(
                    -1,
                    inputField!!.cursorPosition,
                    false
                ) - inputField!!.cursorPosition
            )
            if (autocompleteIndex >= foundPlayerNames.size) {
                autocompleteIndex = 0
            }
        } else {
            val i = inputField!!.getNthWordFromPosWS(-1, inputField!!.cursorPosition, false)
            foundPlayerNames.clear()
            autocompleteIndex = 0
            val s = inputField!!.text.substring(i).toLowerCase()
            val s1 = inputField!!.text.substring(0, inputField!!.cursorPosition)
            sendAutocompleteRequest(s1)
            if (foundPlayerNames.isEmpty()) {
                return
            }
            playerNamesFound = true
            inputField!!.deleteFromCursor(i - inputField!!.cursorPosition)
        }
        if (foundPlayerNames.size > 1) {
            val stringBuilder = StringBuilder()
            for (s2 in foundPlayerNames) {
                if (stringBuilder.isNotEmpty()) {
                    stringBuilder.append(", ")
                }
                stringBuilder.append(s2)
            }
            mc.ingameGUI.chatGUI.printChatMessageWithOptionalDeletion(ChatComponentText(stringBuilder.toString()), 1)
        }
        inputField!!.writeText(foundPlayerNames[autocompleteIndex++])
    }

    private fun sendAutocompleteRequest(p_146405_1_: String) {
        if (p_146405_1_.isNotEmpty()) {
            var blockpos: BlockPos? = null
            if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                blockpos = mc.objectMouseOver.blockPos
            }
            mc.thePlayer.sendQueue.addToSendQueue(C14PacketTabComplete(p_146405_1_, blockpos))
            waitingOnAutocomplete = true
        }
    }

    /**
     * input is relative and is applied directly to the sentHistoryCursor so -1 is the previous message, 1 is the next
     * message from the current cursor position
     */
    private fun getSentHistory(msgPos: Int) {
        var i = sentHistoryCursor + msgPos
        val j = mc.ingameGUI.chatGUI.sentMessages.size
        i = MathHelper.clamp_int(i, 0, j)
        if (i != sentHistoryCursor) {
            if (i == j) {
                sentHistoryCursor = j
                inputField!!.text = historyBuffer
            } else {
                if (sentHistoryCursor == j) {
                    historyBuffer = inputField!!.text
                }
                inputField!!.text = mc.ingameGUI.chatGUI.sentMessages[i]
                sentHistoryCursor = i
            }
        }
    }

    fun onAutocompleteResponse(input: Array<String>) {
        if (waitingOnAutocomplete) {
            playerNamesFound = false
            foundPlayerNames.clear()
            for (s in input) {
                if (s.isNotEmpty()) {
                    foundPlayerNames.add(s)
                }
            }
            val s1 = inputField!!.text
                .substring(inputField!!.getNthWordFromPosWS(-1, inputField!!.cursorPosition, false))
            val s2 = StringUtils.getCommonPrefix(*input)
            if (s2.isNotEmpty() && !s1.equals(s2, ignoreCase = true)) {
                inputField!!.deleteFromCursor(
                    inputField!!.getNthWordFromPosWS(
                        -1,
                        inputField!!.cursorPosition,
                        false
                    ) - inputField!!.cursorPosition
                )
                inputField!!.writeText(s2)
            } else if (foundPlayerNames.size > 0) {
                playerNamesFound = true
                autocompletePlayerNames()
            }
        }
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    override fun doesGuiPauseGame(): Boolean {
        return false
    }

    companion object {

        /**
         * If the gui has not been closed manually by the user, the client caches the content of the field at the time of
         * closure in this field, so it can be restored on the next open.
         */
        private var messageCache: String? = null
        private var messageToSend: String? = null
        var transition: DoubleTransition = DoubleTransition.builder().start(0.0).end(22.0).amountOfSteps(15).reachStart {
            Minecraft.getMinecraft().displayGuiScreen(null)
            if (messageToSend != null) {
                sendChatMessage(messageToSend)
                messageToSend = null
            }
        }.build()

        @JvmStatic
        val direction: Int
            get() = transition.direction
    }
}