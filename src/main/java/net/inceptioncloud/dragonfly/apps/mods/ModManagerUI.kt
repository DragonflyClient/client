package net.inceptioncloud.dragonfly.apps.mods

import net.inceptioncloud.dragonfly.engine.internal.SizedImage
import net.inceptioncloud.dragonfly.engine.switch
import net.inceptioncloud.dragonfly.engine.widgets.assembled.TextField
import net.inceptioncloud.dragonfly.ui.screens.MainMenuUI
import net.minecraft.client.gui.GuiScreen

class ModManagerUI(val previousScreen: GuiScreen) : GuiScreen() {

    override var backgroundImage: SizedImage? = MainMenuUI.splashImage

    override fun initGui() {

    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        if (keyCode == 1 && canManuallyClose) {
            previousScreen.switch()
            return
        }

        super.keyTyped(typedChar, keyCode)
    }

}