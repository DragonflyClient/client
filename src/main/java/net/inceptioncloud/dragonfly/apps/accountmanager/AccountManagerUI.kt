package net.inceptioncloud.dragonfly.apps.accountmanager

import kotlinx.coroutines.*
import net.inceptioncloud.dragonfly.engine.internal.SizedImage
import net.inceptioncloud.dragonfly.engine.switch
import net.inceptioncloud.dragonfly.engine.widgets.assembled.BackNavigation
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Image
import net.inceptioncloud.dragonfly.ui.loader.OneTimeUILoader
import net.inceptioncloud.dragonfly.ui.screens.MainMenuUI
import net.minecraft.client.gui.GuiScreen
import net.minecraft.util.ResourceLocation

class AccountManagerUI(val previousScreen: GuiScreen) : GuiScreen() {

    companion object : OneTimeUILoader(500)

    override var backgroundImage: SizedImage? = MainMenuUI.splashImage

    override var isNativeResolution: Boolean = true

    override fun initGui() {
        val accounts = AccountManagerApp.accounts

        +Image {
            val aspectRatio = 384.0f / 76.0f

            resourceLocation = ResourceLocation("dragonflyres/logos/account-manager.png")
            height = 70.0f
            width = height * aspectRatio
            x = this@AccountManagerUI.width / 2 - width / 2
            y = 70.0f
        } id "logo-account-manager"

        +BackNavigation {
            x = 30.0f
            y = this@AccountManagerUI.height - height - 30.0f
            gui(previousScreen)
        } id "back-navigation"

        val space = 50.0f
        val totalWidth = accounts.sumByDouble { AccountCard.getCardWidth(it.displayName).toDouble() }.toFloat() +
                AccountCard.getCardWidth("") + // minimum width for AddAccountCard
                space * accounts.size
        var currentX = width / 2 - totalWidth / 2

        accounts.forEachIndexed { index, it ->
            val accountCard = +AccountCard(it) {
                x = currentX
                y = 300.0f
                isSelected = AccountManagerApp.selectedAccount == it
            } id "account-$index"

            GlobalScope.launch(Dispatchers.IO) {
                accountCard.isExpired = !it.validate()
            }

            currentX += AccountCard.getCardWidth(it.displayName) + space
        }

        +AddAccountCard {
            x = currentX
            y = 300.0f
        } id "add-account"
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        if (keyCode == 1 && canManuallyClose) {
            previousScreen.switch()
            return
        }

        super.keyTyped(typedChar, keyCode)
    }
}