package net.inceptioncloud.dragonfly.apps.modmanager

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.apps.modmanager.controls.BooleanControl
import net.inceptioncloud.dragonfly.apps.modmanager.controls.ModManagerControl
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette.accentNormal
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette.background
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation.Companion.morph
import net.inceptioncloud.dragonfly.engine.contains
import net.inceptioncloud.dragonfly.engine.internal.Alignment
import net.inceptioncloud.dragonfly.engine.internal.MouseData
import net.inceptioncloud.dragonfly.engine.sequence.easing.EaseQuad
import net.inceptioncloud.dragonfly.engine.switch
import net.inceptioncloud.dragonfly.engine.widgets.assembled.BackNavigation
import net.inceptioncloud.dragonfly.engine.widgets.assembled.TextField
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Image
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Rectangle
import net.inceptioncloud.dragonfly.mods.KeystrokesMod
import net.inceptioncloud.dragonfly.ui.loader.OneTimeUILoader
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

    var selectedEntry: ModListEntry? = null
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

    override fun initGui() {
        selectedEntry = null
        val contentWidth = this@ModManagerUI.width - 400.0
        val contentX = 400.0

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
            gui(previousScreen)
        } id "back-navigation"

        var currentY = 17.0

        for (mod in ModManagerApp.availableMods) {
            +ModListEntry(mod) {
                x = 15.0
                y = currentY
            } id "sidebar-entry-${mod.cleanName}"

            currentY += 61.0
        }

        +Image {
            height = this@ModManagerUI.height / 2.0
            width = height
                x = 400.0 + (contentWidth / 2.0 - width / 2.0)
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
            it.morph(
                30, EaseQuad.IN_OUT,
                ModListEntry::color to if (it == selectedEntry) accentNormal else background
            )?.start()
        }
    }

    private fun updateContent() {
        val placeholderImage = getWidget<Image>("placeholder-image")
        val placeholderText = getWidget<TextField>("placeholder-text")
        val a = if (selectedEntry == null) 1.0 else 0.0

        placeholderImage?.morph(25, EaseQuad.IN_OUT, Image::color to placeholderImage.color.altered { alphaDouble = a })?.start()
        placeholderText?.morph(25, EaseQuad.IN_OUT, TextField::color to placeholderText.color.altered { alphaDouble = a })?.start()

        (stage["dummy-boolean-control"] as? ModManagerControl<*>)?.removeListener()
        stage.remove("dummy-boolean-control")

        if (selectedEntry != null) {
            +BooleanControl(
                KeystrokesMod::enabled,
                "Enable Keystrokes",
                "Wennste des auf true setzt wird alles sterben..."
            ).apply {
                x = 600.0
                y = 40.0
                width = 1200.0
            } id "dummy-boolean-control"
        }
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        val data = MouseData(mouseX, mouseY, mouseButton)

        if (data in getWidget<Rectangle>("sidebar-background") &&
            data !in getWidget<BackNavigation>("back-navigation")
        ) {
            selectedEntry = entries.firstOrNull { data in it }
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