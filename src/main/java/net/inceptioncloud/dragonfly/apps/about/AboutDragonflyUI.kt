package net.inceptioncloud.dragonfly.apps.about

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette.accentNormal
import net.inceptioncloud.dragonfly.engine.font.FontWeight
import net.inceptioncloud.dragonfly.engine.internal.WidgetColor
import net.inceptioncloud.dragonfly.engine.widgets.assembled.*
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Image
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Rectangle
import net.minecraft.client.gui.GuiScreen
import net.minecraft.util.ResourceLocation

/**
 * A modern UI that displays information about Dragonfly.
 *
 * The screen contains a window that is split into 4 sections, the title, information about the network,
 * developer (me :D) and credits to other sources like icons and fonts.
 *
 * @property parentScreen the screen from which the UI was opened
 */
class AboutDragonflyUI(val parentScreen: GuiScreen) : GuiScreen() {

    override var customScaleFactor: () -> Double? = { java.lang.Double.min(mc.displayWidth / 1920.0, mc.displayHeight / 1080.0) }

    override fun initGui() {
        +Rectangle {
            x = 0.0
            y = 0.0
            width = this@AboutDragonflyUI.width.toDouble()
            height = this@AboutDragonflyUI.height.toDouble()
            color = DragonflyPalette.foreground.brighter(0.7)
        } id "background"

        +Image {
            resourceLocation = ResourceLocation("dragonflyres/logos/about-dragonfly.png")
            height = 90.0
            width = 468.0
            x = this@AboutDragonflyUI.width / 2.0 - width / 2.0
            y = 60.0
        } id "logo-about-dragonfly"

        +TextField {
            positionBelow("logo-about-dragonfly", 60.0)
            width = (this@AboutDragonflyUI.width * (3 / 4.0)).coerceAtMost(1280.0)
            x = this@AboutDragonflyUI.width / 2 - width / 2
            adaptHeight = true
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 72, useScale = false, fontWeight = FontWeight.MEDIUM)
            color = DragonflyPalette.background
            staticText = "Inception Cloud"
        } id "inception-cloud-header"

        +TextField {
            positionBelow("inception-cloud-header", 5.0)
            adaptHeight = true
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 56, useScale = false)
            color = DragonflyPalette.background
            staticText = "Dragonfly is a product by Inception Cloud that is maintained by the Dragonfly team. " +
                    "Find out more at [${accentNormal.chatCode}https://inceptioncloud.net§r][https://inceptioncloud.net]."
        } id "inception-cloud-text"

        +SocialMediaPreview(Network.TWITTER, "§7@§rInceptionCloud", "https://twitter.com/InceptionCloud") {
            positionBelow("inception-cloud-text", 20.0)
            height = 56.0
            width = 300.0
        } id "twitter-preview"

        +SocialMediaPreview(Network.INSTAGRAM, "§7@§rInceptionCloud", "https://instagram.com/InceptionCloud") {
            positionBelow("inception-cloud-text", 20.0)
            height = 56.0
            width = 300.0
            x += 320.0
        } id "instagram-preview"

        +SocialMediaPreview(Network.GITHUB, "§7@§rInceptionCloud", "https://github.com/InceptionCloud") {
            positionBelow("inception-cloud-text", 20.0)
            height = 56.0
            width = 300.0
            x += 320.0 * 2
        } id "github-preview"

        +SocialMediaPreview(Network.DISCORD, "§7icnet.dev/§rdiscord", "https://icnet.dev/discord") {
            positionBelow("inception-cloud-text", 20.0)
            height = 56.0
            width = 300.0
            x += 320.0 * 3
        } id "discord-preview"

        +TextField {
            positionBelow("inception-cloud-text", 140.0)
            adaptHeight = true
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 72, useScale = false, fontWeight = FontWeight.MEDIUM)
            color = DragonflyPalette.background
            staticText = "Dragonfly Products"
        } id "dragonfly-products-header"

        +TextField {
            positionBelow("dragonfly-products-header", 5.0)
            adaptHeight = true
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 56, useScale = false)
            color = DragonflyPalette.background
            staticText = "Dragonfly comes along with other related products. All these products can be found " +
                    "on our Dragonfly website at [${accentNormal.chatCode}https://playdragonfly.net§r][https://playdragonfly.net]."
        } id "dragonfly-products-text"

        +TextField {
            positionBelow("dragonfly-products-text", 40.0)
            adaptHeight = true
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 72, useScale = false, fontWeight = FontWeight.MEDIUM)
            color = DragonflyPalette.background
            staticText = "Credits (Third Parties)"
        } id "credits-header"

        +TextField {
            positionBelow("credits-header", 5.0)
            adaptHeight = true
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 56, useScale = false)
            color = DragonflyPalette.background
            staticText = "${accentNormal.chatCode}· §rDragonfly uses several open source libraries whose licences can be " +
                    "found ${accentNormal.chatCode}here§r.\n" +
                    "${accentNormal.chatCode}· §rThe fonts used by Dragonfly are provided by [${accentNormal.chatCode}Google Fonts§r]" +
                    "[https://fonts.google.com]. The main font is called \"Rubik\".\n" +
                    "${accentNormal.chatCode}· §rAll icons that can be seen in Dragonfly are provided by [${accentNormal.chatCode}Icons8§r]" +
                    "[https://icons8.com]. We mainly use the “Fluent” icon set.\n" +
                    "${accentNormal.chatCode}· §rNvidia Highlights for Minecraft by [${accentNormal.chatCode}MCGeForce§r]" +
                    "[https://github.com/MCGeForce/MCGeForce]. MCGeForce Copyright (c) 2012 Adam Heinrich <adam@adamh.cz> "
        } id "credits-text"
    }
}
