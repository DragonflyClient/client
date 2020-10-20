package net.inceptioncloud.dragonfly.mods.togglesneak

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.controls.*
import net.inceptioncloud.dragonfly.controls.color.ColorControl
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation.Companion.morph
import net.inceptioncloud.dragonfly.engine.internal.Alignment
import net.inceptioncloud.dragonfly.engine.internal.WidgetColor
import net.inceptioncloud.dragonfly.engine.sequence.easing.EaseQuad
import net.inceptioncloud.dragonfly.engine.widgets.assembled.TextField
import net.inceptioncloud.dragonfly.mods.core.DragonflyMod
import net.inceptioncloud.dragonfly.mods.keystrokes.KeystrokesMod
import net.inceptioncloud.dragonfly.mods.keystrokes.KeystrokesMod.not
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution

object ToggleSneakMod : DragonflyMod("ToggleSneak") {

    var enabledSneak by option(false)
    var enabledSprint by option(false)
    var doSneak = false
    var doSprint = false

    var enabledOverlay by option(true)
    var overlayText = ""
    var overlayTextColor by option(WidgetColor(1.0, 1.0, 1.0, 1.0))
    var overlaySize by option(16.0)
    var overlayPosition by option(EnumToggleSneakPosition.BOTTOM_RIGHT)
    var animationSpeed by option(0.25)

    var posX = 0.0
    var posY = 0.0
    var width = 0.0
    var height = 0.0

    lateinit var textField: TextField

    override fun publishControls(): List<ControlElement<*>> = listOf(
        TitleControl("General"),
        BooleanControl(!ToggleSneakMod::enabledSneak, "Enable ToggleSneak"),
        BooleanControl(!ToggleSneakMod::enabledSprint, "Enable ToggleSprint"),
        TitleControl("InGame Overlay"),
        BooleanControl(!ToggleSneakMod::enabledOverlay, "Enable Overlay"),
        ColorControl(!ToggleSneakMod::overlayTextColor, "Text Color"),
        DropdownElement(::overlayPosition, "Position"),
        NumberControl(::overlaySize, "Font Size", min = 5.0, max = 25.0, decimalPlaces = 1),
        NumberControl(!ToggleSneakMod::animationSpeed, "Animation Speed (seconds)", "Duration of the text fade-in animation", min = 0.0, max = 1.0, decimalPlaces = 2, liveUpdate = false)
    )

    fun updateOverlay() {

        val oldOverlayText = overlayText

        overlayText = if (enabledOverlay) {
            var resultText = ""

            if (enabledSneak && doSneak) {
                if (enabledSprint && doSprint) {
                    resultText = "Sneaking & Sprinting"
                } else {
                    resultText += "Sneaking"
                }
            } else if (enabledSprint && doSprint) {
                resultText = "Sprinting"
            }

            resultText
        } else {
            ""
        }

        if (overlayText != "") {

            val stringWidth =
                Dragonfly.fontManager.defaultFont.fontRenderer(size = overlaySize.toInt(), useScale = false)
                    .getStringWidth(overlayText)
            val stringHeight =
                Dragonfly.fontManager.defaultFont.fontRenderer(
                    size = overlaySize.toInt(),
                    useScale = false
                ).height.toDouble()
            val screenWidth = ScaledResolution(Minecraft.getMinecraft()).scaledWidth
            val screenHeight = ScaledResolution(Minecraft.getMinecraft()).scaledHeight

            when (overlayPosition) {
                EnumToggleSneakPosition.TOP_LEFT -> {
                    posX = 10.0
                    posY = 10.0
                    width = stringWidth + 3.0
                    height = stringHeight + 3.0
                }
                EnumToggleSneakPosition.TOP_RIGHT -> {
                    posX = screenWidth - (stringWidth + 3.0) - 10.0
                    posY = 10.0
                    width = stringWidth + 3.0
                    height = stringHeight + 3.0
                }
                EnumToggleSneakPosition.BOTTOM_LEFT -> {
                    posX = 10.0
                    posY = screenHeight - stringHeight - 10
                    width = stringWidth + 3.0
                    height = stringHeight + 3.0
                }
                EnumToggleSneakPosition.BOTTOM_RIGHT -> {
                    posX = screenWidth - (stringWidth + 3.0) - 10
                    posY = screenHeight - stringHeight - 10
                    width = stringWidth + 3.0
                    height = stringHeight + 3.0
                }
                EnumToggleSneakPosition.HOTBAR_LEFT -> {
                    posX = Minecraft.getMinecraft().ingameGUI.hotbarX - (stringWidth + 3.0) - 10
                    posY = screenHeight - stringHeight - 10
                    width = stringWidth + 3.0
                    height = stringHeight + 3.0
                }
                EnumToggleSneakPosition.HOTBAR_RIGHT -> {
                    posX =
                        (Minecraft.getMinecraft().ingameGUI.hotbarX + Minecraft.getMinecraft().ingameGUI.hotbarW + 10).toDouble()
                    posY = screenHeight - stringHeight - 10
                    width = stringWidth + 3.0
                    height = stringHeight + 3.0
                }
            }
        }

        textField = TextField().apply {
            x = posX
            y = posY
            width = this@ToggleSneakMod.width
            height = this@ToggleSneakMod.height
            staticText = overlayText
            color = WidgetColor(1.0, 1.0, 1.0, 0.0)
            backgroundColor = WidgetColor(0.0, 0.0, 0.0, 0.0)
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = overlaySize.toInt(), useScale = true)
            textAlignHorizontal = Alignment.CENTER
            textAlignVertical = Alignment.CENTER
        }

        if(oldOverlayText == "") {
            textField.apply {
                if(animationSpeed == 0.0) {
                    color = overlayTextColor
                }else {
                    morph(
                        (animationSpeed * 200).toInt(), EaseQuad.IN_OUT,
                        ::color to overlayTextColor
                    )?.start()
                }

            }
        }else {
            textField.apply {
                color = overlayTextColor
            }
        }

    }

}