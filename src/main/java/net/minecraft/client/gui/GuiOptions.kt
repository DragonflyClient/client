package net.minecraft.client.gui

import net.inceptioncloud.dragonfly.Dragonfly
import net.minecraft.client.settings.GameSettings
import net.minecraft.world.EnumDifficulty
import net.minecraft.client.audio.SoundHandler
import net.minecraft.client.audio.SoundCategory
import net.minecraft.client.audio.PositionedSoundRecord
import net.minecraft.util.IChatComponent
import net.minecraft.util.ChatComponentText
import net.minecraft.util.ChatComponentTranslation
import net.minecraft.client.gui.stream.GuiStreamOptions
import net.minecraft.client.gui.stream.GuiStreamUnavailable
import net.minecraft.client.resources.I18n

class GuiOptions     // TODO [24.01.2020]: Add Language Button to options
// this.buttonList.add(new GuiButtonLanguage(5, this.width / 2 - 124, j + 72 + 12));
// -> this.mc.displayGuiScreen(new GuiLanguage(this, this.mc.gameSettings, this.mc.getLanguageManager()));
    (
    private val parentScreen: GuiScreen,
    /** Reference to the GameSettings object.  */
    private val gameSettings: GameSettings
) : GuiScreen(), GuiYesNoCallback {
    private var field_175357_i: GuiButton? = null
    private var field_175356_r: GuiLockIconButton? = null
    protected var field_146442_a = "Options"

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    override fun initGui() {
        var i = 0
        field_146442_a = I18n.format("options.title")
        for (`gamesettings$options` in field_146440_f) {
            if (`gamesettings$options`.enumFloat) {
                buttonList.add(
                    GuiOptionSlider(
                        `gamesettings$options`.returnEnumOrdinal(),
                        width / 2 - 155 + i % 2 * 160,
                        height / 6 - 12 + 24 * (i shr 1),
                        `gamesettings$options`
                    )
                )
            } else {
                val guioptionbutton = GuiOptionButton(
                    `gamesettings$options`.returnEnumOrdinal(),
                    width / 2 - 155 + i % 2 * 160,
                    height / 6 - 12 + 24 * (i shr 1),
                    `gamesettings$options`,
                    gameSettings.getKeyBinding(`gamesettings$options`)
                )
                buttonList.add(guioptionbutton)
            }
            ++i
        }
        if (mc.theWorld != null) {
            val enumdifficulty = mc.theWorld.difficulty
            field_175357_i = GuiButton(
                108,
                width / 2 - 155 + i % 2 * 160,
                height / 6 - 12 + 24 * (i shr 1),
                150,
                20,
                func_175355_a(enumdifficulty)
            )
            buttonList.add(field_175357_i!!)
            if (mc.isSingleplayer && !mc.theWorld.worldInfo.isHardcoreModeEnabled) {
                field_175357_i!!.setWidth(field_175357_i!!.buttonWidth - 20)
                field_175356_r = GuiLockIconButton(
                    109,
                    field_175357_i!!.xPosition + field_175357_i!!.buttonWidth,
                    field_175357_i!!.yPosition
                )
                buttonList.add(field_175356_r!!)
                field_175356_r!!.func_175229_b(mc.theWorld.worldInfo.isDifficultyLocked)
                field_175356_r!!.enabled = !field_175356_r!!.func_175230_c()
                field_175357_i!!.enabled = !field_175356_r!!.func_175230_c()
            } else {
                field_175357_i!!.enabled = false
            }
        }
        buttonList.add(
            GuiButton(
                110,
                width / 2 - 155,
                height / 6 + 48 - 6,
                150,
                20,
                I18n.format("options.skinCustomisation")
            )
        )
        buttonList.add(object :
            GuiButton(8675309, width / 2 + 5, height / 6 + 48 - 6, 150, 20, "Super Secret Settings...") {
            override fun playPressSound(soundHandlerIn: SoundHandler) {
                val soundeventaccessorcomposite = soundHandlerIn.getRandomSoundFromCategories(
                    SoundCategory.ANIMALS,
                    SoundCategory.BLOCKS,
                    SoundCategory.MOBS,
                    SoundCategory.PLAYERS,
                    SoundCategory.WEATHER
                )
                if (soundeventaccessorcomposite != null) {
                    soundHandlerIn.playSound(
                        PositionedSoundRecord.create(
                            soundeventaccessorcomposite.soundEventLocation,
                            0.5f
                        )
                    )
                }
            }
        })
        buttonList.add(GuiButton(106, width / 2 - 155, height / 6 + 72 - 6, 150, 20, I18n.format("options.sounds")))
        buttonList.add(GuiButton(107, width / 2 + 5, height / 6 + 72 - 6, 150, 20, I18n.format("options.stream")))
        buttonList.add(GuiButton(101, width / 2 - 155, height / 6 + 96 - 6, 150, 20, I18n.format("options.video")))
        buttonList.add(GuiButton(100, width / 2 + 5, height / 6 + 96 - 6, 150, 20, I18n.format("options.controls")))
        buttonList.add(GuiButton(102, width / 2 - 155, height / 6 + 120 - 6, 150, 20, I18n.format("options.language")))
        buttonList.add(GuiButton(103, width / 2 + 5, height / 6 + 120 - 6, 150, 20, I18n.format("options.chat.title")))
        buttonList.add(
            GuiButton(
                105,
                width / 2 - 155,
                height / 6 + 144 - 6,
                150,
                20,
                I18n.format("options.resourcepack")
            )
        )
        buttonList.add(
            GuiButton(
                104,
                width / 2 + 5,
                height / 6 + 144 - 6,
                150,
                20,
                I18n.format("options.snooper.view")
            )
        )
        buttonList.add(GuiButton(200, width / 2 - 100, height / 6 + 168, I18n.format("gui.done")))
    }

    fun func_175355_a(p_175355_1_: EnumDifficulty): String {
        val ichatcomponent: IChatComponent = ChatComponentText("")
        ichatcomponent.appendSibling(ChatComponentTranslation("options.difficulty"))
        ichatcomponent.appendText(": ")
        ichatcomponent.appendSibling(ChatComponentTranslation(p_175355_1_.difficultyResourceKey))
        return ichatcomponent.formattedText
    }

    override fun confirmClicked(result: Boolean, id: Int) {
        mc.displayGuiScreen(this)
        if (id == 109 && result && mc.theWorld != null) {
            mc.theWorld.worldInfo.isDifficultyLocked = true
            field_175356_r!!.func_175229_b(true)
            field_175356_r!!.enabled = false
            field_175357_i!!.enabled = false
        }
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    override fun actionPerformed(button: GuiButton?) {
        if (button!!.enabled) {
            if (button.id < 100 && button is GuiOptionButton) {
                val `gamesettings$options` = button.returnEnumOptions()
                gameSettings.setOptionValue(`gamesettings$options`, 1)
                button.displayString = gameSettings.getKeyBinding(GameSettings.Options.getEnumOptions(button.id))
            }
            if (button.id == 108) {
                mc.theWorld.worldInfo.difficulty =
                    EnumDifficulty.getDifficultyEnum(mc.theWorld.difficulty.difficultyId + 1)
                field_175357_i!!.displayString = func_175355_a(mc.theWorld.difficulty)
            }
            if (button.id == 109) {
                mc.displayGuiScreen(
                    GuiYesNo(
                        this, ChatComponentTranslation("difficulty.lock.title").formattedText, ChatComponentTranslation(
                            "difficulty.lock.question", ChatComponentTranslation(
                                mc.theWorld.worldInfo.difficulty.difficultyResourceKey
                            )
                        ).formattedText, 109
                    )
                )
            }
            if (button.id == 110) {
                mc.gameSettings.saveOptions()
                mc.displayGuiScreen(GuiCustomizeSkin(this))
            }
            if (button.id == 8675309) {
                mc.entityRenderer.activateNextShader()
            }
            if (button.id == 101) {
                mc.gameSettings.saveOptions()
                mc.displayGuiScreen(GuiVideoSettings(this, gameSettings))
            }
            if (button.id == 100) {
                mc.gameSettings.saveOptions()
                mc.displayGuiScreen(GuiControls(this, gameSettings))
            }
            if (button.id == 102) {
                mc.gameSettings.saveOptions()
                mc.displayGuiScreen(GuiLanguage(this, gameSettings, mc.languageManager))
            }
            if (button.id == 103) {
                mc.gameSettings.saveOptions()
                mc.displayGuiScreen(ScreenChatOptions(this, gameSettings))
            }
            if (button.id == 104) {
                mc.gameSettings.saveOptions()
                mc.displayGuiScreen(GuiSnooper(this, gameSettings))
            }
            if (button.id == 200) {
                mc.gameSettings.saveOptions()
                mc.displayGuiScreen(parentScreen)
            }
            if (button.id == 105) {
                mc.gameSettings.saveOptions()
                mc.displayGuiScreen(GuiScreenResourcePacks(this))
            }
            if (button.id == 106) {
                mc.gameSettings.saveOptions()
                mc.displayGuiScreen(GuiScreenOptionsSounds(this, gameSettings))
            }
            if (button.id == 107) {
                mc.gameSettings.saveOptions()
                val istream = mc.twitchStream
                if (istream.func_152936_l() && istream.func_152928_D()) {
                    mc.displayGuiScreen(GuiStreamOptions(this, gameSettings))
                } else {
                    GuiStreamUnavailable.func_152321_a(this)
                }
            }
        }
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawDefaultBackground()
        drawCenteredString(fontRendererObj, field_146442_a, width / 2, 15, 16777215)
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    private fun print(text: String) {
        println(text)
    }

    companion object {
        private val field_146440_f = arrayOf(GameSettings.Options.FOV)
    }
}