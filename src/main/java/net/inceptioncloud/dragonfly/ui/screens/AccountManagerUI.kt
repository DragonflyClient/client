package net.inceptioncloud.dragonfly.ui.screens

import net.inceptioncloud.dragonfly.engine.internal.SizedImage
import net.minecraft.client.gui.GuiScreen
import java.lang.Double.min

class AccountManagerUI(previousScreen: GuiScreen) : GuiScreen() {

    override var backgroundImage: SizedImage? = MainMenuUI.loadSplashImage()

    override var customScaleFactor: () -> Double? = { min(mc.displayWidth / 1920.0, mc.displayHeight / 1080.0) }

    override fun initGui() {

    }
}