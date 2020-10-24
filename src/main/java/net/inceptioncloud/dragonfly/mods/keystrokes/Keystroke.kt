package net.inceptioncloud.dragonfly.mods.keystrokes

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation
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
                        detachAnimation<MorphAnimation>()
                        morph(
                            KeystrokesMod.animationSpeed.toInt(), EaseQuad.IN_OUT,
                            ::backgroundColor to KeystrokesMod.bgActiveColor,
                            ::color to KeystrokesMod.textActiveColor
                        )?.start()
                    } else {
                        detachAnimation<MorphAnimation>()
                        morph(
                            KeystrokesMod.animationSpeed.toInt(), EaseQuad.IN_OUT,
                            ::backgroundColor to KeystrokesMod.bgInactiveColor,
                            ::color to KeystrokesMod.textInactiveColor
                        )?.start()
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
    var posX = 0.0f
    var posY = 0.0f
    var width = 0.0f
    var height = 0.0f
    var keystrokesStartX = 0.0f
    var keystrokesStartY = 0.0f

    lateinit var textField: TextField

    init {
        update()
    }

    fun update() {
        val scale = KeystrokesMod.scale.toFloat()
        val space = KeystrokesMod.space.toFloat()
        val scaledWidth = ScaledResolution(Minecraft.getMinecraft()).scaledWidth
        val scaledHeight = ScaledResolution(Minecraft.getMinecraft()).scaledHeight

        when (KeystrokesMod.position) {
            EnumKeystrokesPosition.TOP_LEFT -> {
                keystrokesStartX = 10.0f
                keystrokesStartY = 10.0f
            }
            EnumKeystrokesPosition.TOP_RIGHT -> {
                keystrokesStartX = scaledWidth - ((scale * 3) + (space * 2)) - 10
                keystrokesStartY = 10.0f
            }
            EnumKeystrokesPosition.BOTTOM_LEFT -> {
                keystrokesStartX = 10.0f
                keystrokesStartY = scaledHeight - ((scale * 4) + (space * 3)) - 10
            }
            EnumKeystrokesPosition.BOTTOM_RIGHT -> {
                keystrokesStartX = scaledWidth - ((scale * 3) + (space * 2)) - 10
                keystrokesStartY = scaledHeight - ((scale * 4) + (space * 3)) - 10
            }
            EnumKeystrokesPosition.HOTBAR_LEFT -> {
                keystrokesStartX = Minecraft.getMinecraft().ingameGUI.hotbarX - ((scale * 3) + (space * 2)) - 10
                keystrokesStartY = scaledHeight - ((scale * 4) + (space * 3)) - 10
            }
            EnumKeystrokesPosition.HOTBAR_RIGHT -> {
                keystrokesStartX = (Minecraft.getMinecraft().ingameGUI.hotbarX + Minecraft.getMinecraft().ingameGUI.hotbarW + 10).toFloat()
                keystrokesStartY = scaledHeight - ((scale * 4) + (space * 3)) - 10
            }
        }

        when (keyDesc) {
            "key.forward" -> {
                this.width = scale
                this.height = scale
                posX = keystrokesStartX + this.width + space
                posY = keystrokesStartY
                name = Keyboard.getKeyName(keyCode)
            }
            "key.left" -> {
                this.width = scale
                this.height = scale
                posX = keystrokesStartX
                posY = keystrokesStartY + this.width + space
                name = Keyboard.getKeyName(keyCode)
            }
            "key.back" -> {
                this.width = scale
                this.height = scale
                posX = keystrokesStartX + this.width + space
                posY = keystrokesStartY + this.width + space
                name = Keyboard.getKeyName(keyCode)
            }
            "key.right" -> {
                this.width = scale
                this.height = scale
                posX = keystrokesStartX + (2 * this.width) + (2 * space)
                posY = keystrokesStartY + this.width + space
                name = Keyboard.getKeyName(keyCode)
            }
            "key.jump" -> {
                this.width = (3 * scale) + (2 * space)
                this.height = scale
                posX = keystrokesStartX
                posY = keystrokesStartY + (2 * this.height) + (2 * space)
                name = Keyboard.getKeyName(keyCode)
            }
            "key.attack" -> {
                this.width = (1.5f * scale) + (space / 2)
                this.height = scale
                posX = keystrokesStartX
                posY = keystrokesStartY + (3 * this.height) + (3 * space)
                name = Mouse.getButtonName(keyCode + 100)
                    .replace("BUTTON0", "LMB")
                    .replace("BUTTON1", "RMB")
                    .replace("BUTTON2", "MMB")
            }
            "key.use" -> {
                this.width = (1.5f * scale) + (space / 2)
                this.height = scale
                posX = keystrokesStartX + 1.5f * scale + (1.5f * space)
                posY = keystrokesStartY + (3 * this.height) + (3 * space)
                name = Mouse.getButtonName(keyCode + 100)
                    .replace("BUTTON0", "LMB")
                    .replace("BUTTON1", "RMB")
                    .replace("BUTTON2", "MMB")
            }
            else -> {
                posX = -1000.0f
                posY = -1000.0f
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
