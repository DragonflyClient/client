package net.inceptioncloud.dragonfly.mods.keystrokes

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation.Companion.morph
import net.inceptioncloud.dragonfly.engine.internal.Alignment
import net.inceptioncloud.dragonfly.engine.sequence.easing.EaseQuad
import net.inceptioncloud.dragonfly.engine.widgets.assembled.TextField
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse

class Keystroke(val keyCode: Int, val keyDesc: String) {

    var pressed: Boolean = false
        set(value) {
            field = value

            val ingameGUI = Minecraft.getMinecraft().ingameGUI

            ingameGUI.stage["keystroke-$keyDesc"].apply {
                if(this is TextField) {
                    if(value) {
                        morph(20, EaseQuad.IN_OUT, this@Keystroke::backgroundColor to KeystrokesMod.bgActiveColor)?.start()
                        morph(20, EaseQuad.IN_OUT, this@Keystroke::textColor to KeystrokesMod.textActiveColor)?.start()
                    } else {
                        morph(20, EaseQuad.IN_OUT, this@Keystroke::backgroundColor to KeystrokesMod.bgInactiveColor)?.start()
                        morph(20, EaseQuad.IN_OUT, this@Keystroke::textColor to KeystrokesMod.textInactiveColor)?.start()
                    }
                }
            }
        }

    var backgroundColor = KeystrokesMod.bgInactiveColor
    var textColor = KeystrokesMod.textInactiveColor
    var fontSize = KeystrokesMod.fontSize
    var scale = KeystrokesMod.scale
    var space = KeystrokesMod.space
    var name = ""
    var posX = 0.0
    var posY = 0.0
    var width = 0.0
    var height = 0.0
    var keystrokesStartX = 0.0
    var keystrokesStartY = 0.0

    var textField: TextField

    init {
        when (KeystrokesMod.position) {
            EnumKeystrokesPosition.TOP_LEFT -> {
                keystrokesStartX = 10.0
                keystrokesStartY = 10.0
            }
            EnumKeystrokesPosition.TOP_RIGHT -> {
                val width = ScaledResolution(Minecraft.getMinecraft()).scaledWidth
                val scale = KeystrokesMod.scale
                val space = KeystrokesMod.space

                keystrokesStartX = width - ((scale * 3) + (space * 2)) - 10
                keystrokesStartY = 10.0
            }
            EnumKeystrokesPosition.BOTTOM_LEFT -> {
                val height = ScaledResolution(Minecraft.getMinecraft()).scaledHeight
                val scale = KeystrokesMod.scale
                val space = KeystrokesMod.space

                keystrokesStartX = 10.0
                keystrokesStartY = height - ((scale * 4) + (space * 3)) - 10
            }
            EnumKeystrokesPosition.BOTTOM_RIGHT -> {
                val width = ScaledResolution(Minecraft.getMinecraft()).scaledWidth
                val height = ScaledResolution(Minecraft.getMinecraft()).scaledHeight
                val scale = KeystrokesMod.scale
                val space = KeystrokesMod.space

                keystrokesStartX = width - ((scale * 3) + (space * 2)) - 10
                keystrokesStartY = height - ((scale * 4) + (space * 3)) - 10
            }
            EnumKeystrokesPosition.HOTBAR_LEFT -> {
                val height = ScaledResolution(Minecraft.getMinecraft()).scaledHeight
                val scale = KeystrokesMod.scale
                val space = KeystrokesMod.space

                keystrokesStartX = Minecraft.getMinecraft().ingameGUI.hotbarX - ((scale * 3) + (space * 2)) - 10
                keystrokesStartY = height - ((scale * 4) + (space * 3)) - 10
            }
            EnumKeystrokesPosition.HOTBAR_RIGHT -> {
                val height = ScaledResolution(Minecraft.getMinecraft()).scaledHeight
                val scale = KeystrokesMod.scale
                val space = KeystrokesMod.space

                keystrokesStartX = (Minecraft.getMinecraft().ingameGUI.hotbarX + Minecraft.getMinecraft().ingameGUI.hotbarW + 10).toDouble()
                keystrokesStartY = height - ((scale * 4) + (space * 3)) - 10
            }
        }

        when (keyDesc) {
            "key.forward" -> {
                width = KeystrokesMod.scale
                height = KeystrokesMod.scale
                posX = keystrokesStartX + width + KeystrokesMod.space
                posY = keystrokesStartY
                name = Keyboard.getKeyName(keyCode)
            }
            "key.left" -> {
                width = KeystrokesMod.scale
                height = KeystrokesMod.scale
                posX = keystrokesStartX
                posY = keystrokesStartY + width + KeystrokesMod.space
                name = Keyboard.getKeyName(keyCode)
            }
            "key.back" -> {
                width = KeystrokesMod.scale
                height = KeystrokesMod.scale
                posX = keystrokesStartX + width + KeystrokesMod.space
                posY = keystrokesStartY + width + KeystrokesMod.space
                name = Keyboard.getKeyName(keyCode)
            }
            "key.right" -> {
                width = KeystrokesMod.scale
                height = KeystrokesMod.scale
                posX = keystrokesStartX + (2 * width) + (2 * KeystrokesMod.space)
                posY = keystrokesStartY + width + KeystrokesMod.space
                name = Keyboard.getKeyName(keyCode)
            }
            "key.jump" -> {
                width = (3 * KeystrokesMod.scale) + (2 * KeystrokesMod.space)
                height = KeystrokesMod.scale
                posX = keystrokesStartX
                posY = keystrokesStartY + (2 * height) + (2 * KeystrokesMod.space)
                name = Keyboard.getKeyName(keyCode)
            }
            "key.attack" -> {
                width = (1.5 * KeystrokesMod.scale) + (KeystrokesMod.space / 2)
                height = KeystrokesMod.scale
                posX = keystrokesStartX
                posY = keystrokesStartY + (3 * height) + (3 * KeystrokesMod.space)
                name = Mouse.getButtonName(keyCode + 100)
                    .replace("BUTTON0", "LMB")
                    .replace("BUTTON1", "RMB")
                    .replace("BUTTON2", "MMB")
            }
            "key.use" -> {
                width = (1.5 * KeystrokesMod.scale) + (KeystrokesMod.space / 2)
                height = KeystrokesMod.scale
                posX = keystrokesStartX + 1.5 * KeystrokesMod.scale + (1.5 * KeystrokesMod.space)
                posY = keystrokesStartY + (3 * height) + (3 * KeystrokesMod.space)
                name = Mouse.getButtonName(keyCode + 100)
                    .replace("BUTTON0", "LMB")
                    .replace("BUTTON1", "RMB")
                    .replace("BUTTON2", "MMB")
            }
            else -> {
                posX = -1000.0
                posY = -1000.0
            }
        }

        textField = TextField().apply {
            x = posX
            y = posY
            this.width = this@Keystroke.width
            this.height = this@Keystroke.height
            backgroundColor = this@Keystroke.backgroundColor
            color = textColor
            textAlignHorizontal = Alignment.CENTER
            textAlignVertical = Alignment.CENTER
            staticText = name
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = KeystrokesMod.fontSize.toInt())
        }
    }

}