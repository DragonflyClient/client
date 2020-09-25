package net.inceptioncloud.dragonfly.mods.togglesneak

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.controls.*
import net.inceptioncloud.dragonfly.controls.color.ColorControl
import net.inceptioncloud.dragonfly.engine.internal.WidgetColor
import net.inceptioncloud.dragonfly.mods.core.DragonflyMod
import net.inceptioncloud.dragonfly.mods.keystrokes.EnumKeystrokesPosition
import net.inceptioncloud.dragonfly.mods.keystrokes.KeystrokesMod
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import tornadofx.loadFont

object ToggleSneakMod : DragonflyMod("ToggleSneak") {

    var enabledSneak by option(false)
    var enabledSprint by option(false)
    var doSneak = false
    var doSprint = false

    var enabledOverlay by option(true)
    var overlayText = ""
    var overlayTextColor by option(WidgetColor(1.0, 1.0, 1.0, 1.0))
    var overlayBackgroundColor by option(WidgetColor(1.0, 1.0, 1.0, 0.0))
    var overlaySize by option(16)
    var position by option(EnumToggleSneakPosition.BOTTOM_RIGHT)

    var posX = 0.0
    var posY = 0.0
    var width = 0.0

    override fun publishControls(): List<ControlElement<*>> = listOf(
        TitleControl("General"),
        BooleanControl(ToggleSneakMod::enabledSneak, "Enable ToggleSneak"),
        BooleanControl(ToggleSneakMod::enabledSprint, "Enable ToggleSprint"),
        TitleControl("InGame Overlay"),
        BooleanControl(ToggleSneakMod::enabledOverlay, "Enable Overlay"),
        ColorControl(ToggleSneakMod::overlayTextColor, "Text Color"),
        ColorControl(ToggleSneakMod::overlayBackgroundColor, "Background Color"),
        DropdownElement(ToggleSneakMod::position,"Position"),
        NumberControl(ToggleSneakMod::overlaySize, "Text Size", min = 5.0, max = 25.0, decimalPlaces = 1)
    )

    fun updateOverlayText() {
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

        if(overlayText != "") {

            val stringWidth = Dragonfly.fontManager.defaultFont.fontRenderer(size = overlaySize, useScale = false)
                .getStringWidth(overlayText)
            val stringHeight =
                Dragonfly.fontManager.defaultFont.fontRenderer(size = overlaySize, useScale = false).height.toDouble()
            val screenWidth = ScaledResolution(Minecraft.getMinecraft()).scaledWidth
            val screenHeight = ScaledResolution(Minecraft.getMinecraft()).scaledHeight

            when (position) {
                EnumToggleSneakPosition.TOP_LEFT -> {
                    posX = 10.0
                    posY = 10.0
                    width = stringWidth + 3.0
                }
                EnumToggleSneakPosition.TOP_RIGHT -> {
                    posX = screenWidth - (stringWidth + 3.0) - 10.0
                    posY = 10.0
                }
                EnumToggleSneakPosition.BOTTOM_LEFT -> {
                    posX = 10.0
                    posY = screenHeight - stringHeight - 10
                }
                EnumToggleSneakPosition.BOTTOM_RIGHT -> {
                    posX = screenWidth - (stringWidth + 3.0) - 10
                    posY = screenHeight - stringHeight - 10
                }
                EnumToggleSneakPosition.HOTBAR_LEFT -> {
                    posX = Minecraft.getMinecraft().ingameGUI.hotbarX - (stringWidth + 3.0) - 10
                    posY = screenHeight - stringHeight - 10
                }
                EnumToggleSneakPosition.HOTBAR_RIGHT -> {
                    posX =
                        (Minecraft.getMinecraft().ingameGUI.hotbarX + Minecraft.getMinecraft().ingameGUI.hotbarW + 10).toDouble()
                    posY = screenHeight - stringHeight - 10
                }
            }
        }
    }

}