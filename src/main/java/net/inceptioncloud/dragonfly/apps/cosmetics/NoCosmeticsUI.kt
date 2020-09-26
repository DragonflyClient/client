package net.inceptioncloud.dragonfly.apps.cosmetics

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.cosmetics.logic.CosmeticsManager
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.font.FontWeight
import net.inceptioncloud.dragonfly.engine.internal.Alignment
import net.inceptioncloud.dragonfly.engine.switch
import net.inceptioncloud.dragonfly.engine.tooltip.Tooltip
import net.inceptioncloud.dragonfly.engine.tooltip.TooltipAlignment
import net.inceptioncloud.dragonfly.engine.widgets.assembled.*
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Image
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Rectangle
import net.minecraft.client.gui.GuiScreen
import net.minecraft.util.ResourceLocation
import java.net.URI

class NoCosmeticsUI(
    val previousScreen: GuiScreen
) : GuiScreen() {

    override var isNativeResolution: Boolean = true

    override fun initGui() {
        +Rectangle {
            x = 0.0
            y = 0.0
            width = this@NoCosmeticsUI.width.toDouble()
            height = this@NoCosmeticsUI.height.toDouble()
            color = DragonflyPalette.foreground.brighter(0.7)
        } id "background"

        +Image {
            val resolution = 1368 / 912.0

            height = 400.0
            width = height * resolution
            resourceLocation = ResourceLocation("dragonflyres/vectors/waiting.png")
            x = this@NoCosmeticsUI.width / 2.0 - width / 2.0
            y = 50.0
        } id "vector"

        +TextField {
            positionBelow("vector", 10.0)

            staticText = "Nothing here..."
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(fontWeight = FontWeight.MEDIUM, size = 80, useScale = false)
            textAlignHorizontal = Alignment.CENTER
            color = DragonflyPalette.background

            width = (this@NoCosmeticsUI.width / 3.0).coerceIn(400.0, 800.0)
            x = this@NoCosmeticsUI.width / 2.0 - width / 2.0

            adaptHeight = true
            adaptHeight()
        } id "title"

        +TextField {
            positionBelow("title", 20.0)

            staticText = "It looks like you don't own any cosmetic items!\n" +
                    "Head over to the ${DragonflyPalette.accentNormal.chatCode}[Dragonfly Store]" +
                    "[https://store.playdragonfly.net]§r to purchase one of our many cosmetics. Don't worry - we have enough for everybody.\n\n" +
                    "If you just bought an item from our store, press the refresh button below to synchronize your cosmetics with our database."
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 50, useScale = false)
            textAlignHorizontal = Alignment.CENTER
            color = DragonflyPalette.background

            width = (this@NoCosmeticsUI.width / 2.5).coerceIn(400.0, 900.0)
            x = this@NoCosmeticsUI.width / 2.0 - width / 2.0

            adaptHeight = true
            adaptHeight()
        } id "text"

        +OutlineButton {
            positionBelow("text", 90.0)
            width = 350.0
            height = 55.0
            text = "Get your cosmetics now!"
            x = this@NoCosmeticsUI.width / 2.0 - width / 2.0
            color = DragonflyPalette.accentNormal
            onClick {
                openWebLink(URI("https://store.playdragonfly.net"))
            }
        } id "get-button"

        +OutlineButton {
            positionBelow("get-button", 10.0)
            width = 350.0
            height = 40.0
            text = "Refresh cosmetics"
            x = this@NoCosmeticsUI.width / 2.0 - width / 2.0
            tooltip = Tooltip("Synchronize cosmetics with database", TooltipAlignment.BELOW)
            onClick {
                if (text == "Refresh cosmetics") {
                    GlobalScope.launch {
                        val fetched = CosmeticsManager.fetchDragonflyCosmetics()
                        CosmeticsManager.dragonflyAccountCosmetics = fetched

                        if (fetched?.isNotEmpty() == true) {
                            CosmeticsUI(previousScreen).switch()
                        } else {
                            this@NoCosmeticsUI.getWidget<TextField>("title")?.staticText = "Still nothing here... Sorry :c"
                        }
                    }
                }
            }
        } id "refresh-button"

        super.initGui()
    }
}