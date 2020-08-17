package net.inceptioncloud.dragonfly.apps.accountmanager

import kotlinx.coroutines.*
import net.inceptioncloud.dragonfly.engine.internal.SizedImage
import net.inceptioncloud.dragonfly.engine.widgets.assembled.BackNavigation
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Image
import net.inceptioncloud.dragonfly.ui.screens.MainMenuUI
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.util.ResourceLocation
import java.lang.Double.min

class AccountManagerUI(val previousScreen: GuiScreen) : GuiScreen() {

    override var backgroundImage: SizedImage? = MainMenuUI.splashImage

    override var customScaleFactor: () -> Double? = { min(mc.displayWidth / 1920.0, mc.displayHeight / 1080.0) }

    val accounts = AccountManagerApp.accounts

    override fun initGui() {
        +Image {
            val aspectRatio = 384.0 / 76.0

            resourceLocation = ResourceLocation("dragonflyres/logos/account-manager.png")
            height = 70.0
            width = height * aspectRatio
            x = this@AccountManagerUI.width / 2.0 - width / 2.0
            y = 70.0
        } id "logo-account-manager"

        +BackNavigation {
            x = 30.0
            y = this@AccountManagerUI.height - height - 30.0
            gui(previousScreen)
        } id "back-navigation"

        val space = 50.0
        val totalWidth = accounts.sumByDouble { AccountCard.getCardWidth(it.displayName) } +
                AccountCard.getCardWidth("") + // minimum width for AddAccountCard
                space * accounts.size
        var currentX = width / 2.0 - totalWidth / 2.0

        accounts.forEachIndexed { index, it ->
            val accountCard = +AccountCard(it) {
                x = currentX
                y = 300.0
                isSelected = Minecraft.getMinecraft().session.playerID == it.uuid.toString()
            } id "account-$index"

            GlobalScope.launch(Dispatchers.IO) {
                accountCard.isExpired = !it.validate()
            }

            currentX += AccountCard.getCardWidth(it.displayName) + space
        }

        +AddAccountCard {
            x = currentX
            y = 300.0
        } id "add-account"
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        if (keyCode == 1 && canManuallyClose) {
            mc.displayGuiScreen(previousScreen)
            return
        }

        super.keyTyped(typedChar, keyCode)
    }
}