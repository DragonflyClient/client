package net.inceptioncloud.dragonfly.apps.modmanager

import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.internal.SizedImage
import net.inceptioncloud.dragonfly.engine.switch
import net.inceptioncloud.dragonfly.engine.widgets.assembled.BackNavigation
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Rectangle
import net.inceptioncloud.dragonfly.ui.screens.MainMenuUI
import net.minecraft.client.gui.GuiScreen
import net.minecraft.util.ResourceLocation

class ModManagerUI(val previousScreen: GuiScreen) : GuiScreen() {

    override var backgroundImage: SizedImage? = MainMenuUI.splashImage

    var selectedEntry: ModListEntry? = null

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

        for(i in 0..2) {
            +ModListEntry {
                x = 15.0
                y = currentY
                color = DragonflyPalette.background
                text = "Hotkeys"
                icon = ResourceLocation("dragonflyres/icons/mods/hotkeys.png")
            }id "sidebar-entry-hotkeys$i"
            currentY += 61.0
        }

        if(selectedEntry == null) {
            +ModListEntry {
                x = 15.0
                y = currentY
                color = DragonflyPalette.background
                text = "Hotkeys"
                icon = ResourceLocation("dragonflyres/icons/mods/hotkeys.png")
            }id "sidebar-entry-hotkeys3"
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