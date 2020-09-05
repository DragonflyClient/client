package net.inceptioncloud.dragonfly.apps.accountmanager

import kotlinx.coroutines.*
import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.account.link.LinkBridge
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette.accentNormal
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette.foreground
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation.Companion.morph
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.sequence.easing.EaseQuad
import net.inceptioncloud.dragonfly.engine.structure.IDimension
import net.inceptioncloud.dragonfly.engine.structure.IPosition
import net.inceptioncloud.dragonfly.engine.widgets.assembled.OutlineButton
import net.inceptioncloud.dragonfly.engine.widgets.assembled.TextField
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Image
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Rectangle
import net.inceptioncloud.dragonfly.overlay.toast.Toast
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.util.ResourceLocation

class AccountCard(
    val account: Account,
    initializerBlock: (AccountCard.() -> Unit)? = null
) : AssembledWidget<AccountCard>(initializerBlock), IPosition, IDimension {

    override var x: Double by property(0.0)
    override var y: Double by property(0.0)
    override var width: Double = -1.0
    override var height: Double by property(450.0)

    var isSelected: Boolean by property(false)
    var isExpired: Boolean by property(false)
    var accentColor: WidgetColor by property(foreground)

    init {
        this::isSelected.getTypedWidgetDelegate<Boolean>()!!.objectProperty
            .addListener { _, oldValue, newValue ->
                if (oldValue == newValue) return@addListener

                morph(
                    30, EaseQuad.IN_OUT,
                    this::accentColor to if (newValue) accentNormal else foreground
                )?.start()
            }
    }

    override fun assemble(): Map<String, Widget<*>> = mapOf(
        "container" to Rectangle(),
        "expired-icon" to Image(),
        "skull-shadow" to Rectangle(),
        "skull" to Image(),
        "name" to TextField(),
        "email" to TextField(),
        "secondary-button" to OutlineButton(), // logout button
        "primary-button" to OutlineButton()    // switch/refresh button
    )

    override fun updateStructure() {
        this.width = getCardWidth(account.displayName)

        "container"<Rectangle> {
            x = this@AccountCard.x
            y = this@AccountCard.y
            width = this@AccountCard.width
            height = this@AccountCard.height
            color = accentColor.altered { alphaDouble = 0.2 }
            outlineColor = accentColor
            outlineStroke = 2.0
        }

        "expired-icon"<Image> {
            if (isExpired) {
                isVisible = true

                width = 40.0
                height = 40.0
                x = this@AccountCard.x + this@AccountCard.width - width / 2.0
                y = this@AccountCard.y - height / 2.0
                resourceLocation = ResourceLocation("dragonflyres/icons/expired.png")
            } else isVisible = false
        }

        val skull = "skull"<Image> {
            width = 100.0
            height = 100.0
            x = this@AccountCard.x + (this@AccountCard.width - width) / 2.0
            y = this@AccountCard.y + 30.0
            resourceLocation = ResourceLocation("dragonflyres/icons/mainmenu/steve-skull.png")
        }!!

        "skull-shadow"<Rectangle> {
            width = skull.width
            height = skull.height
            x = skull.x + 2.0
            y = skull.y + 2.0
            color = WidgetColor(0, 0, 0, 50)
        }

        val name = "name"<TextField> {
            Dragonfly.fontManager.defaultFont.bindFontRenderer(size = 60, useScale = false)

            x = this@AccountCard.x
            y = skull.y + skull.height + 20.0
            width = this@AccountCard.width
            adaptHeight = true
            staticText = account.displayName
            textAlignHorizontal = Alignment.CENTER
            dropShadow = true
        }!!.also { it.adaptHeight() }

        "email"<TextField> {
            Dragonfly.fontManager.defaultFont.bindFontRenderer(size = 35, useScale = false)

            x = this@AccountCard.x
            y = name.y + name.height + 7.0
            width = this@AccountCard.width
            adaptHeight = true
            staticText = censorEmail(account.email)
            textAlignHorizontal = Alignment.CENTER
            dropShadow = true
        }!!.also { it.adaptHeight() }

        val buttonMargin = 10.0
        val mc = Minecraft.getMinecraft()

        val secondaryButton = "secondary-button"<OutlineButton> {
            width = this@AccountCard.width - buttonMargin * 2
            height = 45.0
            x = this@AccountCard.x + buttonMargin
            y = this@AccountCard.y + this@AccountCard.height - buttonMargin - height

            if (isSelected) {
                text = "Refresh"
                color = DragonflyPalette.background
            } else {
                text = "Switch"
                color = accentNormal
            }

            onClick {
                GlobalScope.launch(Dispatchers.IO) {
                    if (isSelected) {
                        if (account.refresh()) {
                            mc.session = account.toSession()
                            Toast.queue("Access token refreshed", 500)
                        } else {
                            Toast.queue("Could not refresh token!", 500)
                        }
                    } else {
                        val valid = if (isExpired) {
                            Toast.queue("Refreshing access token...", 200)
                            account.refresh()
                        } else {
                            account.validate()
                        }

                        if (valid) {
                            isExpired = false
                            mc.session = account.toSession()

                            (mc.currentScreen as? AccountManagerUI)?.stage?.content
                                ?.filterKeys { it.startsWith("account-") }
                                ?.forEach { (_, value) -> (value as? AccountCard)?.isSelected = false }

                            isSelected = true

                            LinkBridge.showModalForAccount(account)
                        } else {
                            Toast.queue("§cFailed to switch to account §r${account.displayName}§c!", 500)
                        }
                    }
                }
            }
        }!!

        "primary-button"<OutlineButton> {
            width = this@AccountCard.width - buttonMargin * 2
            height = 45.0
            x = this@AccountCard.x + buttonMargin
            y = secondaryButton.y - height - buttonMargin
            text = "Logout"

            onClick {
                GlobalScope.launch(Dispatchers.IO) {
                    account.invalidate()
                    AccountManagerApp.accounts.removeIf { it.uuid == account.uuid }
                    AccountManagerApp.storeAccounts()
                    Toast.queue("Account '${account.displayName}' has been logged out", 200)

                    mc.addScheduledTask {
                        Minecraft.getMinecraft().currentScreen.refresh()
                    }
                }
            }
        }

        if (skull.dynamicTexture == null) {
            GlobalScope.launch(Dispatchers.IO) {
                val image = account.retrieveSkull() ?: return@launch
                mc.addScheduledTask {
                    skull.dynamicTexture = DynamicTexture(image)
                }
            }
        }
    }

    override fun handleMousePress(data: MouseData) {
        structure.forEach { it.value.handleMousePress(data) }
    }

    companion object {

        /**
         * Calculates the with of the [AccountCard] based on the [name] of the account.
         */
        fun getCardWidth(name: String): Double {
            val fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 60)
            return (fontRenderer.getStringWidth(name) + 20.0).coerceAtLeast(250.0)
        }

        /**
         * Censors the email by only keeping the first three letters of the username and
         * the provider and appending four wildcards to the username (eg. abc****@gmail.com).
         */
        fun censorEmail(email: String): String {
            if ("@" !in email) return email

            val provider = email.split("@").last()
            val username = email.removeSuffix("@$provider").toCharArray()
            val firstThree = username.take(3).joinToString("")

            return "$firstThree****@$provider".toLowerCase()
        }
    }
}