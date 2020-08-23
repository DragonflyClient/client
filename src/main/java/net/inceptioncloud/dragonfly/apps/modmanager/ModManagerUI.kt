package net.inceptioncloud.dragonfly.apps.modmanager

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette.accentNormal
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette.background
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation.Companion.morph
import net.inceptioncloud.dragonfly.engine.contains
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.sequence.easing.EaseQuad
import net.inceptioncloud.dragonfly.engine.switch
import net.inceptioncloud.dragonfly.engine.widgets.assembled.BackNavigation
import net.inceptioncloud.dragonfly.engine.widgets.assembled.TextField
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Image
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Rectangle
import net.minecraft.client.gui.GuiScreen
import net.minecraft.util.ResourceLocation

class ModManagerUI(val previousScreen: GuiScreen) : GuiScreen() {

    /**
     * Returns all mod list entries in the sidebar
     */
    val entries: List<ModListEntry>
        get() = stage.content
            .filterKeys { it.startsWith("sidebar-entry") }
            .mapNotNull { it.value as? ModListEntry }

    var selectedEntry: ModListEntry? = null
        set(value) {
            field = value
            entries.forEach {
                it.morph(
                    30, EaseQuad.IN_OUT,
                    ModListEntry::color to if (it == value) accentNormal else background
                )?.start()
            }
        }

    val dummyMods = listOf("Hotkeys", "KeyStrokes")

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
            width = this@ModManagerUI.width.toDouble()
            height = this@ModManagerUI.height.toDouble()
            color = DragonflyPalette.foreground
        } id "background-color"

        +Rectangle {
            x = 0.0
            y = 0.0
            width = 400.0
            height = this@ModManagerUI.height.toDouble()
            color = background
        } id "sidebar-background"

        +BackNavigation {
            x = 30.0
            y = this@ModManagerUI.height - height - 30.0
            gui(previousScreen)
        } id "back-navigation"

        var currentY = 17.0

        for (mod in dummyMods) {
            +ModListEntry {
                x = 15.0
                y = currentY
                color = background
                text = mod
                icon = ResourceLocation("dragonflyres/icons/mods/${mod.toLowerCase()}.png")
            } id "sidebar-entry-${mod.toLowerCase()}"

            currentY += 61.0
        }

        if (selectedEntry == null) {
            +Image {
                height = this@ModManagerUI.height / 2.0
                width = height
                x = 400.0 + ((this@ModManagerUI.width - 400.0) / 2.0 - width / 2.0)
                y = this@ModManagerUI.height / 2.0 - height / 2.0
                resourceLocation = ResourceLocation("dragonflyres/vectors/rocket.png")
            } id "placeholder-image"
        }

    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        val data = MouseData(mouseX, mouseY, mouseButton)
        selectedEntry = entries.firstOrNull { data in it }
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        if (keyCode == 1 && canManuallyClose) {
            previousScreen.switch()
            return
        }

        super.keyTyped(typedChar, keyCode)
    }

}