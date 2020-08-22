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
class LoginModal : ModalWidget("Login", 400.0, 550.0) {

    /**
     * The padding of the container box.
     */
    val padding = 35.0

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
        "username-field" to InputTextField(),
        "password-field" to InputTextField(),
        "login-button" to OutlineButton(),
        "register-button" to OutlineButton(),
        "skip-text" to TextField()
    )

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
        }!!

        val password = "password-field"<InputTextField> {
            x = this@LoginModal.x + this@LoginModal.padding
            y = username.y + username.height + 10.0
            width = this@LoginModal.width - 2 * this@LoginModal.padding
            height = 50.0
            label = "Password"
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 50, useScale = false)
            isPassword = true
        }!!

        val login = "login-button"<OutlineButton> {
            x = this@LoginModal.x + this@LoginModal.padding
            y = password.y + password.height + 40.0
            width = this@LoginModal.width - (padding * 2)
            height = 40.0
            text = "Login"
            color = DragonflyPalette.accentNormal
            onClick {
                login()
            }
        }!!

        val register = "register-button"<OutlineButton> {
            x = this@LoginModal.x + this@LoginModal.padding
            y = login.y + login.height + 20.0
            width = this@LoginModal.width - (padding * 2)
            height = 40.0
            text = "Register"
            onClick {
                GuiScreen.openWebLink(URL("https://playdragonfly.net/register").toURI())
            }
        }!!

        "skip-text"<TextField> {
            x = this@LoginModal.x
            y = register.y + register.height + 25.0
            width = this@LoginModal.width
            height = 40.0
            color = DragonflyPalette.background.brighter(0.5)
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 40, useScale = false)
            staticText = "Skip for now"
            textAlignHorizontal = Alignment.CENTER
            textAlignVertical = Alignment.CENTER
            clickAction = {
                StorageOptions.SKIP_LOGIN.set(true)
                Modal.hideModal()
            }
            hoverAction = {
                if (isHovered) morph(50, null, TextField::color to DragonflyPalette.background.brighter(0.3))?.start()
                else morph(50, null, TextField::color to DragonflyPalette.background.brighter(0.5))?.start()
            }
        }
    }

    /**
     * Performs the login process.
     */
    private fun login() {
        if (isAuthenticating) return

        val username = getWidget<InputTextField>("username-field")!!
        val password = getWidget<InputTextField>("password-field")!!

        if (username.realText.isBlank()) return Toast.queue("§cUsername must not be blank!", 300)
        if (password.realText.isBlank()) return Toast.queue("§cPassword must not be blank!", 300)

        isAuthenticating = true
        GlobalScope.launch(Dispatchers.IO) {
            try {
                Toast.queue("Authenticating with Dragonfly...", 200)
                val account = DragonflyAccountBridge.login(username.realText, password.realText)
                Dragonfly.account = account
                Toast.queue("§aLogged in as §r${account.username}", 500)
                Modal.hideModal()
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

fun showLoginModal() {
    var otherFinished = false
    fun `continue`() {
        if (otherFinished) {
            Modal.showModal(LoginModal())
        }
    }
    Dragonfly.fontManager.defaultFont.fontRendererAsync(size = 60, useScale = false) {
        `continue`()
        otherFinished = true
    }
    Dragonfly.fontManager.defaultFont.fontRendererAsync(size = 50, useScale = false) {
        `continue`()
        otherFinished = true
    }
}