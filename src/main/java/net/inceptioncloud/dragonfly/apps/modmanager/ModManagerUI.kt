package net.inceptioncloud.dragonfly.apps.modmanager

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation.Companion.morph
import net.inceptioncloud.dragonfly.engine.internal.Alignment
import net.inceptioncloud.dragonfly.engine.internal.WidgetColor
import net.inceptioncloud.dragonfly.engine.sequence.easing.EaseQuad
import net.inceptioncloud.dragonfly.engine.structure.IColor
import net.inceptioncloud.dragonfly.engine.switch
import net.inceptioncloud.dragonfly.engine.widgets.assembled.BackNavigation
import net.inceptioncloud.dragonfly.engine.widgets.assembled.TextField
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Image
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Rectangle
import net.minecraft.client.gui.GuiScreen
import net.minecraft.util.ResourceLocation

class ModManagerUI(val previousScreen: GuiScreen) : GuiScreen() {

    var selectedEntry: ModListEntry? = null

    var show = true

    override var customScaleFactor: () -> Double? = {
        java.lang.Double.min(
            mc.displayWidth / 1920.0,
            mc.displayHeight / 1080.0
        )
    }

    override fun initGui() {

        +Rectangle {
            x = 0.0
            y = 0.0
            width = 1920.0
            height = 1080.0
            color = DragonflyPalette.foreground
        } id "background-color"

        +Rectangle {
            x = 0.0
            y = 0.0
            width = 383.0
            height = this@ModManagerUI.height.toDouble()
            color = DragonflyPalette.background
        } id "sidebar-background"

        +BackNavigation {
            x = 30.0
            y = this@ModManagerUI.height - height - 30.0
            gui(previousScreen)
        } id "back-navigation"

        var currentY = 17.0
        var currentText = "Hotkeys"
        var currentColor = DragonflyPalette.background

        if (selectedEntry != null) {
            currentColor = if (selectedEntry!!.text == currentText) {
                DragonflyPalette.accentNormal
            } else {
                DragonflyPalette.background
            }
        }

        +ModListEntry {
            x = 15.0
            y = currentY
            color = currentColor
            text = currentText
            icon = ResourceLocation("dragonflyres/icons/mods/hotkeys.png")
        } id "sidebar-entry-${currentText.toLowerCase()}"

        currentY += 61.0
        currentText = "KeyStrokes"

        if (selectedEntry != null) {
            currentColor = if (selectedEntry!!.text == currentText) {
                DragonflyPalette.accentNormal
            } else {
                DragonflyPalette.background
            }
        }

        +ModListEntry {
            x = 15.0
            y = currentY
            color = currentColor
            text = currentText
            icon = ResourceLocation("dragonflyres/icons/mods/hotkeys.png")
        } id "sidebar-entry-${currentText.toLowerCase()}"

        if (selectedEntry == null) {
            +Image {
                x = 965.0
                y = 195.0
                width = 250.0
                height = 250.0
                resourceLocation = ResourceLocation("dragonflyres/icons/taskbar/apps/mod-manager.png")
            } id "placeholder-image"
            +TextField {
                x = 808.0
                y = 448.0
                width = 800.0
                height = 119.0
                staticText = "Mod Manager"
                textAlignVertical = Alignment.CENTER
                textAlignHorizontal = Alignment.START
                fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 100 * 2, useScale = false)
                color = DragonflyPalette.background
            } id "placeholder-title"
            +TextField {
                x = 727.0
                y = 530.0
                width = 800.0
                height = 119.0
                staticText = "The place where everything begins."
                textAlignVertical = Alignment.CENTER
                textAlignHorizontal = Alignment.START
                fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 50 * 2, useScale = false)
                color = DragonflyPalette.background
            } id "placeholder-slogan"
            +TextField {
                x = 700.0
                y = 670.0
                width = 800.0
                height = 119.0
                staticText = "In the Mod Manager you are able to control all your mods, \n" +
                        "and to customize the client so that everything is exactly\n" +
                        "how you want it to be."
                textAlignVertical = Alignment.CENTER
                textAlignHorizontal = Alignment.CENTER
                fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 25 * 2, useScale = false)
                color = DragonflyPalette.background
            } id "placeholder-text"
            +Rectangle {
                x = 700.0
                y = 195.0
                width = 800.0
                height = 700.0
                color = WidgetColor(255,255,255,0)
            } id "placeholder-plate"
        }

    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        if (keyCode == 1 && canManuallyClose) {
            previousScreen.switch()
            return
        }

        super.keyTyped(typedChar, keyCode)
    }

}