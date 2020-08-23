package net.inceptioncloud.dragonfly.apps.accountmanager

import kotlinx.coroutines.*
import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.account.link.LinkBridge
import net.inceptioncloud.dragonfly.engine.internal.ImageResource
import net.inceptioncloud.dragonfly.engine.internal.SizedImage
import net.inceptioncloud.dragonfly.engine.switch
import net.inceptioncloud.dragonfly.engine.widgets.assembled.*
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Image
import net.inceptioncloud.dragonfly.overlay.toast.Toast
import net.inceptioncloud.dragonfly.ui.screens.MainMenuUI
import net.minecraft.client.gui.GuiScreen
import net.minecraft.util.ResourceLocation
import org.lwjgl.input.Keyboard
import java.lang.Double.min

class AddAccountUI(val previousScreen: GuiScreen) : GuiScreen() {

    override var backgroundImage: SizedImage? = MainMenuUI.splashImage

    override var customScaleFactor: () -> Double? = { min(mc.displayWidth / 1920.0, mc.displayHeight / 1080.0) }

    override fun initGui() {
        Keyboard.enableRepeatEvents(true)

        +Image {
            val aspectRatio = 384.0 / 76.0

            resourceLocation = ResourceLocation("dragonflyres/logos/account-manager.png")
            height = 70.0
            width = height * aspectRatio
            x = this@AddAccountUI.width / 2.0 - width / 2.0
            y = 70.0
        } id "logo-account-manager"

        val email = +InputTextField().apply {
            width = 600.0
            height = 60.0
            x = this@AddAccountUI.width / 2.0 - width / 2.0
            y = 400.0
            label = "Email address"
            padding = 8.0
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = height.toInt(), useScale = false)
            maxStringLength = 100
        } id "email-field"

        val password = +InputTextField().apply {
            positionBelow("email-field", 25.0)
            label = "Password"
            padding = 8.0
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = height.toInt(), useScale = false)
            isPassword = true
            maxStringLength = 100
        } id "password-field"

        +DragonflyButton {
            positionBelow("password-field", 200.0)
            text = "Authenticate"
            icon = ImageResource("dragonflyres/icons/fingerprint.png")
            width = 500.0
            height = 60.0
            x = this@AddAccountUI.width / 2.0 - width / 2.0
            iconSize = 50.0
            useScale = false

            onClick {
                GlobalScope.launch(Dispatchers.IO) {
                    Toast.queue("Authenticating with Minecraft...", 200)
                    val account = AccountManagerApp.authenticate(email.realText, password.realText)

                    if (account != null) {
                        Toast.queue("§aAccount added!", 400)
                        AccountManagerApp.accounts.add(account)
                        AccountManagerApp.storeAccounts()

                        mc.session = account.toSession()
                        LinkBridge.showModalForAccount(account)

                        previousScreen.switch()
                    } else {
                        Toast.queue("§cAuthentication failed, please try again!", 300)
                        password.inputText = ""
                    }
                }
            }
        } id "authenticate-button"

        +BackNavigation {
            x = 30.0
            y = this@AddAccountUI.height - height - 30.0
            gui(previousScreen)
        } id "back-navigation"
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        if (keyCode == 1 && canManuallyClose) {
            previousScreen.switch()
            return
        }

        super.keyTyped(typedChar, keyCode)
    }
}