package net.inceptioncloud.dragonfly.apps.accountmanager

import kotlinx.coroutines.*
import net.inceptioncloud.dragonfly.account.link.LinkBridge
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.widgets.assembled.*
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Image
import net.inceptioncloud.dragonfly.mc
import net.inceptioncloud.dragonfly.overlay.modal.Modal
import net.inceptioncloud.dragonfly.overlay.modal.ModalWidget
import net.inceptioncloud.dragonfly.overlay.toast.Toast
import net.minecraft.util.ResourceLocation
import org.lwjgl.input.Keyboard
import net.inceptioncloud.dragonfly.engine.font.Typography
import net.inceptioncloud.dragonfly.engine.font.font

class AddAccountModal : ModalWidget("Add Account", 400.0f, 500.0f) {

    /**
     * The padding of the container box.
     */
    val padding = 35.0f

    /**
     * Whether the modal window is currently in the authentication process. If this value is true,
     * the login button cannot be pressed again.
     */
    var isAuthenticating = false

    override fun onShow() {
        Keyboard.enableRepeatEvents(true)
    }

    override fun assemble(): Map<String, Widget<*>> = mapOf(
        "container" to RoundedRectangle(),
        "image" to Image(),
        "title" to TextField(),
        "email-field" to InputTextField(),
        "password-field" to InputTextField(),
        "authenticate-button" to OutlineButton()
    )

    override fun updateStructure() {
        "container"<RoundedRectangle> {
            x = this@AddAccountModal.x
            y = this@AddAccountModal.y
            width = this@AddAccountModal.width
            height = this@AddAccountModal.height
            color = DragonflyPalette.background
            arc = 10.0f
        }

        val image = "image"<Image> {
            width = 100.0f
            height = width
            x = this@AddAccountModal.x + (this@AddAccountModal.width - width) / 2
            y = this@AddAccountModal.y + 25.0f
            resourceLocation = ResourceLocation("dragonflyres/logos/minecraft.png")
        }!!

        val title = "title"<TextField> {
            x = this@AddAccountModal.x
            y = image.y + image.height + 10.0f
            width = this@AddAccountModal.width
            height = 60.0f
            fontRenderer = font(Typography.HEADING_2)
            staticText = "Authenticate Minecraft Account"
            textAlignHorizontal = Alignment.CENTER
            textAlignVertical = Alignment.CENTER
        }!!

        val email = "email-field"<InputTextField> {
            x = this@AddAccountModal.x + this@AddAccountModal.padding
            y = title.y + title.height + 20.0f
            width = this@AddAccountModal.width - 2 * this@AddAccountModal.padding
            height = 50.0f
            label = "Email"
            fontRenderer = font(Typography.BASE)
        }!!

        "password-field"<InputTextField> {
            x = this@AddAccountModal.x + this@AddAccountModal.padding
            y = email.y + email.height + 10.0f
            width = this@AddAccountModal.width - 2 * this@AddAccountModal.padding
            height = 50.0f
            label = "Password"
            fontRenderer = font(Typography.BASE)
            isPassword = true
        }

        "authenticate-button"<OutlineButton> {
            width = this@AddAccountModal.width - (padding * 2)
            height = 40.0f
            x = this@AddAccountModal.x + this@AddAccountModal.padding
            y = this@AddAccountModal.y + this@AddAccountModal.height - padding - height
            text = "Authenticate"
            color = DragonflyPalette.accentNormal
            onClick {
                login()
            }
        }
    }

    /**
     * Performs the login process.
     */
    private fun login() {
        if (isAuthenticating) return

        val email = getWidget<InputTextField>("email-field")!!
        val password = getWidget<InputTextField>("password-field")!!

        if (email.realText.isBlank()) return Toast.queue("§cEmail must not be blank!", 300)
        if (password.realText.isBlank()) return Toast.queue("§cPassword must not be blank!", 300)

        isAuthenticating = true
        GlobalScope.launch(Dispatchers.IO) {
            try {
                Toast.queue("Authenticating with Minecraft...", 200)
                val account = AccountManagerApp.authenticate(email.realText, password.realText)

                if (account != null) {

                    if (AccountManagerApp.accounts.any { it.uuid == account.uuid }) {
                        Toast.queue("§aAccount replaced", 400)
                        AccountManagerApp.accounts.removeAll { it.uuid == account.uuid }
                    } else {
                        Toast.queue("§aAccount added", 400)
                    }

                    AccountManagerApp.accounts.add(account)
                    AccountManagerApp.storeAccounts()

                    mc.session = account.toSession()
                    mc.currentScreen.refresh()
                    Modal.hideModal()
                    LinkBridge.showModalForAccount(account)
                } else {
                    Toast.queue("§cAuthentication failed, please try again!", 300)
                    isAuthenticating = false
                }
            } catch (e: Exception) {
                Toast.queue("§cAuthentication failed: §r${e.message}", 500)
                isAuthenticating = false
            }
        }
    }

    override fun handleMousePress(data: MouseData) {
        super.handleMousePress(data)
        structure.values.forEach { it.handleMousePress(data) }
    }

    override fun handleKeyTyped(char: Char, keyCode: Int) {
        super.handleKeyTyped(char, keyCode)

        if (char == '\r') return login()
        if (char == '\t') {
            val email = getWidget<InputTextField>("email-field")!!
            val password = getWidget<InputTextField>("password-field")!!

            if (email.isFocused) {
                email.isFocused = false
                password.isFocused = true
            } else if (password.isFocused) {
                password.isFocused = false
                email.isFocused = true
            }
        }

        structure.values.forEach { it.handleKeyTyped(char, keyCode) }
    }
}