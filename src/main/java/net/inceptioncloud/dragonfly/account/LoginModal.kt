package net.inceptioncloud.dragonfly.account

import kotlinx.coroutines.*
import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation.Companion.morph
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.widgets.assembled.*
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Image
import net.inceptioncloud.dragonfly.options.sections.StorageOptions
import net.inceptioncloud.dragonfly.overlay.modal.Modal
import net.inceptioncloud.dragonfly.overlay.modal.ModalWidget
import net.inceptioncloud.dragonfly.overlay.toast.Toast
import net.minecraft.client.gui.GuiScreen
import net.minecraft.util.ResourceLocation
import org.lwjgl.input.Keyboard
import java.net.URL

/**
 * A modal window that is used to prompt the user for Dragonfly authentication.
 *
 * It provides a username and password field as well as an option to register for a new account
 * and skip the login process.
 */
class LoginModal(
    val isAutomaticallyOpening: Boolean
) : ModalWidget("Login", 400.0, if (isAutomaticallyOpening) 550.0 else 510.0) {

    /**
     * The padding of the container box.
     */
    val padding = 35.0

    /**
     * Whether the modal window is currently in the authentication process. If this value is true,
     * the login button cannot be pressed again.
     */
    var isAuthenticating = false

    var isEnteringTwoFactorCode = false

    override fun onShow() {
        Keyboard.enableRepeatEvents(true)
    }

    override fun updateStructure() {
        "container"<RoundedRectangle> {
            x = this@LoginModal.x
            y = this@LoginModal.y
            width = this@LoginModal.width
            height = this@LoginModal.height
            color = DragonflyPalette.background
            arc = 10.0
        }

        val image = "image"<Image> {
            width = 100.0
            height = width
            x = this@LoginModal.x + (this@LoginModal.width - width) / 2
            y = this@LoginModal.y + 25.0
            resourceLocation = ResourceLocation("dragonflyres/logos/default.png")
        }!!

        val title = "title"<TextField> {
            x = this@LoginModal.x
            y = image.y + image.height + 10.0
            width = this@LoginModal.width
            height = 60.0
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 60, useScale = false)
            staticText = "Dragonfly Login"
            textAlignHorizontal = Alignment.CENTER
            textAlignVertical = Alignment.CENTER
        }!!

        val username = "username-field"<InputTextField> {
            x = this@LoginModal.x + this@LoginModal.padding
            y = title.y + title.height + 20.0
            width = this@LoginModal.width - 2 * this@LoginModal.padding
            height = 50.0
            label = "Username"
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 50, useScale = false)
            isVisible = !isEnteringTwoFactorCode
            isEnabled = isVisible
        }!!

        val password = "password-field"<InputTextField> {
            x = this@LoginModal.x + this@LoginModal.padding
            y = username.y + username.height + 10.0
            width = this@LoginModal.width - 2 * this@LoginModal.padding
            height = 50.0
            label = "Password"
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 50, useScale = false)
            isPassword = true
            isVisible = !isEnteringTwoFactorCode
            isEnabled = isVisible
        }!!

        val login = "login-button"<OutlineButton> {
            x = this@LoginModal.x + this@LoginModal.padding
            y = password.y + password.height + 40.0
            width = this@LoginModal.width - (padding * 2)
            height = 40.0
            text = "Login"
            color = DragonflyPalette.accentNormal
            isVisible = !isEnteringTwoFactorCode
            onClick {
                if (isEnteringTwoFactorCode) return@onClick
                login()
            }
        }!!

        val register = "register-button"<OutlineButton> {
            x = this@LoginModal.x + this@LoginModal.padding
            y = login.y + login.height + 20.0
            width = this@LoginModal.width - (padding * 2)
            height = 40.0
            text = "Register"
            isVisible = !isEnteringTwoFactorCode
            onClick {
                if (isEnteringTwoFactorCode) return@onClick
                GuiScreen.openWebLink(URL("https://playdragonfly.net/register").toURI())
            }
        }!!

        "skip-text"<TextField> {
            if (isAutomaticallyOpening) {
                x = this@LoginModal.x
                y = register.y + register.height + 25.0
                width = this@LoginModal.width
                height = 40.0
                color = DragonflyPalette.background.brighter(0.5)
                fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 40, useScale = false)
                staticText = "Don't ask again"
                textAlignHorizontal = Alignment.CENTER
                textAlignVertical = Alignment.CENTER
                isVisible = !isEnteringTwoFactorCode
                clickAction = {
                    StorageOptions.SKIP_LOGIN.set(true)
                    Modal.hideModal()
                }
                hoverAction = {
                    if (isHovered) morph(50, null, TextField::color to DragonflyPalette.background.brighter(0.3))?.start()
                    else morph(50, null, TextField::color to DragonflyPalette.background.brighter(0.5))?.start()
                }
            } else {
                isVisible = false
                clickAction = {}
                hoverAction = {}
            }
        }

        "2fa-info"<TextField> {
            x = this@LoginModal.x + this@LoginModal.padding
            y = title.y + title.height + 10.0
            width = this@LoginModal.width - 2 * this@LoginModal.padding
            height = 50.0
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 40, useScale = false)
            isVisible = isEnteringTwoFactorCode
            textAlignHorizontal = Alignment.CENTER
            staticText = "Please enter the two factor auth code from your mobile device."
        }

        "2fa-field"<InputTextField> {
            x = this@LoginModal.x + this@LoginModal.padding
            y = username.y + username.height + 10.0
            width = this@LoginModal.width - 2 * this@LoginModal.padding
            height = 50.0
            label = "Two factor auth code"
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 50, useScale = false)
            isVisible = isEnteringTwoFactorCode
            maxStringLength = 6
            characterFilter = { it.isDigit() }
        }!!

        "2fa-confirm"<OutlineButton> {
            width = this@LoginModal.width - (padding * 2)
            height = 40.0
            x = this@LoginModal.x + this@LoginModal.padding
            y = this@LoginModal.y + this@LoginModal.height - this@LoginModal.padding - height
            text = "Confirm"
            color = DragonflyPalette.accentNormal
            isVisible = isEnteringTwoFactorCode
            onClick {
                if (!isEnteringTwoFactorCode) return@onClick
                execute2FA()
            }
        }!!
    }

    override fun assemble(): Map<String, Widget<*>> = mapOf(
        "container" to RoundedRectangle(),
        "image" to Image(),
        "title" to TextField(),
        "2fa-info" to TextField(),
        "username-field" to InputTextField(),
        "password-field" to InputTextField(),
        "2fa-field" to InputTextField(),
        "login-button" to OutlineButton(),
        "register-button" to OutlineButton(),
        "2fa-confirm" to OutlineButton(),
        "skip-text" to TextField()
    )

    private fun execute2FA() {
        if (isAuthenticating) return
        if (!isEnteringTwoFactorCode) return

        val username = getWidget<InputTextField>("username-field")!!
        val password = getWidget<InputTextField>("password-field")!!
        val code = getWidget<InputTextField>("2fa-field")!!

        if (username.realText.isBlank()) return Toast.queue("§cUsername must not be blank!", 300)
        if (password.realText.isBlank()) return Toast.queue("§cPassword must not be blank!", 300)
        if (code.realText.length != 6) return Toast.queue("§cAuth code must have exactly 6 digits", 300)

        isAuthenticating = true
        GlobalScope.launch(Dispatchers.IO) {
            try {
                Toast.queue("Authenticating with Dragonfly...", 200)
                val account = AuthenticationBridge.login(username.realText, password.realText, code.realText)
                Dragonfly.account = account
                Toast.queue("§aLogged in as §r${account.username}", 500)
                Modal.hideModal()
            } catch (e: Exception) {
                Toast.queue("§cAuthentication failed: §r${e.message}", 500)
                isAuthenticating = false
            }
        }
    }

    /**
     * Performs the login process.
     */
    private fun login() {
        if (isAuthenticating) return
        if (isEnteringTwoFactorCode) return

        val username = getWidget<InputTextField>("username-field")!!
        val password = getWidget<InputTextField>("password-field")!!

        if (username.realText.isBlank()) return Toast.queue("§cUsername must not be blank!", 300)
        if (password.realText.isBlank()) return Toast.queue("§cPassword must not be blank!", 300)

        isAuthenticating = true
        GlobalScope.launch(Dispatchers.IO) {
            try {
                Toast.queue("Authenticating with Dragonfly...", 200)
                val account = AuthenticationBridge.login(username.realText, password.realText)
                Dragonfly.account = account
                Toast.queue("§aLogged in as §r${account.username}", 500)
                Modal.hideModal()
            } catch (_: TwoFactorAuthException) {
                isAuthenticating = false
                isEnteringTwoFactorCode = true
                runStructureUpdate()
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

        if (char == '\r') return if (isEnteringTwoFactorCode) execute2FA() else login()
        if (char == '\t') {
            val username = getWidget<InputTextField>("username-field")!!
            val password = getWidget<InputTextField>("password-field")!!

            if (username.isFocused) {
                username.isFocused = false
                password.isFocused = true
            } else if (password.isFocused) {
                password.isFocused = false
                username.isFocused = true
            }
        }

        structure.values.forEach { it.handleKeyTyped(char, keyCode) }
    }
}