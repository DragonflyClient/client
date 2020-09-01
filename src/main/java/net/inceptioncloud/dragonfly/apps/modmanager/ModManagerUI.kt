package net.inceptioncloud.dragonfly.apps.modmanager

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.apps.modmanager.controls.OptionControlElement
import net.inceptioncloud.dragonfly.apps.modmanager.controls.TitleControl
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette.accentNormal
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette.background
import net.inceptioncloud.dragonfly.engine.contains
import net.inceptioncloud.dragonfly.engine.internal.Alignment
import net.inceptioncloud.dragonfly.engine.internal.MouseData
import net.inceptioncloud.dragonfly.engine.switch
import net.inceptioncloud.dragonfly.engine.widgets.assembled.BackNavigation
import net.inceptioncloud.dragonfly.engine.widgets.assembled.TextField
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Image
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Rectangle
import net.inceptioncloud.dragonfly.mods.core.DragonflyMod
import net.inceptioncloud.dragonfly.mods.keystrokes.KeystrokesManager
import net.inceptioncloud.dragonfly.mods.keystrokes.KeystrokesMod
import net.inceptioncloud.dragonfly.ui.loader.OneTimeUILoader
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.util.ResourceLocation

class ModManagerUI(val previousScreen: GuiScreen) : GuiScreen() {

    companion object : OneTimeUILoader(500)

    /**
     * Returns all mod list entries in the sidebar
     */
    val entries: List<ModListEntry>
        get() = stage.content
            .filterKeys { it.startsWith("sidebar-entry") }
            .mapNotNull { it.value as? ModListEntry }

    var selectedMod: DragonflyMod? = null
        set(value) {
            if (field == value) return
            field = value

            updateSidebar()
            updateContent()
        }

    override var customScaleFactor: () -> Double? = {
        java.lang.Double.min(
            mc.displayWidth / 1920.0,
            mc.displayHeight / 1080.0
        )
    }

    val contentX = 500.0
    val contentWidth: Double
        get() = this@ModManagerUI.width - contentX

    override fun initGui() {
        +Rectangle {
            x = 0.0
            y = 0.0
            width = this@ModManagerUI.width.toDouble()
            height = this@ModManagerUI.height.toDouble()
            color = DragonflyPalette.foreground.brighter(0.7)
        } id "background-color"

        +Rectangle {
            x = 0.0
            y = 0.0
            width = contentX
            height = this@ModManagerUI.height.toDouble()
            color = background
        } id "sidebar-background"

        +BackNavigation {
            x = 30.0
            y = this@ModManagerUI.height - height - 30.0
            action {
                reloadKeystrokesOverlay()
                previousScreen.switch()
            }
        } id "back-navigation"

        var currentY = 17.0

        for (mod in ModManagerApp.availableMods) {
            +ModListEntry(mod) {
                x = 15.0
                y = currentY
                width = contentX - (2 * x)
            } id "sidebar-entry-${mod.cleanName}"

            currentY += 61.0
        }

        +Image {
            height = this@ModManagerUI.height / 2.0
            width = height
            x = contentX + (contentWidth / 2.0 - width / 2.0)
            y = this@ModManagerUI.height / 2.5 - height / 2.0
            resourceLocation = ResourceLocation("dragonflyres/vectors/rocket.png")
        } id "placeholder-image"

        +TextField {
            positionBelow("placeholder-image", 10.0)
            width = contentWidth / 3.0
            adaptHeight = true
            x = contentX + contentWidth / 2.0 - width / 2.0
            staticText = "Choose a mod in the sidebar to (de-)activate it and customize it's appearance, behavior and much more."
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 60, useScale = false)
            color = background
            textAlignHorizontal = Alignment.CENTER
        } id "placeholder-text"

        updateSidebar()
        updateContent()
    }

    private fun updateSidebar() {
        entries.forEach {
            it.color = if (it.mod == selectedMod) accentNormal else background
        }
    }

    private fun updateContent() {
        val placeholderImage = getWidget<Image>("placeholder-image")
        val placeholderText = getWidget<TextField>("placeholder-text")

        placeholderImage?.isVisible = selectedMod == null
        placeholderText?.isVisible = selectedMod == null

        stage.content.filterKeys { it.startsWith("control-") }
            .forEach { (key, value) ->
                stage.remove(key)
                (value as? OptionControlElement<*>)?.removeListener()
            }

        if (selectedMod != null) {
            val mod = selectedMod!!
            val controls = mod.publishControls()

            val controlsWidth = (contentWidth - 600.0).coerceIn(1000.0..1500.0)
            val controlsX = contentX + (contentWidth - controlsWidth) / 2.0
            var currentY = 40.0

            for ((index, control) in controls.withIndex()) {
                if (control is TitleControl && index != 0) currentY += 15.0

                control.x = controlsX
                control.y = currentY
                control.width = controlsWidth

                stage.add("control-element-$index" to control)

                currentY += control.height + 13.0
            }
        }
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        val data = MouseData(mouseX, mouseY, mouseButton)

        if (data in getWidget<Rectangle>("sidebar-background") &&
            data !in getWidget<BackNavigation>("back-navigation")
        ) {
            selectedMod = entries.firstOrNull { data in it }?.mod
        }
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        if (keyCode == 1 && canManuallyClose) {
            previousScreen.switch()
            reloadKeystrokesOverlay()
            return
        }

        super.keyTyped(typedChar, keyCode)
    }

    private fun reloadKeystrokesOverlay() {
        for (keystroke in KeystrokesManager.keystrokes) {
            Minecraft.getMinecraft().ingameGUI.keystrokesScale["keystrokes-${keystroke.keyDesc}"] = KeystrokesMod.scale
            Minecraft.getMinecraft().ingameGUI.keystrokesSpace["keystrokes-${keystroke.keyDesc}"] = KeystrokesMod.space
            Minecraft.getMinecraft().ingameGUI.keystrokesFontSize["keystrokes-${keystroke.keyDesc}"] = KeystrokesMod.fontSize

            Minecraft.getMinecraft().ingameGUI.initKeystrokes(true)
        }
    }
}