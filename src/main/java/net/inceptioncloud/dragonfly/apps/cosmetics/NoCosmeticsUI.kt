package net.inceptioncloud.dragonfly.apps.cosmetics

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.inceptioncloud.dragonfly.cosmetics.logic.CosmeticsManager
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.internal.Alignment
import net.inceptioncloud.dragonfly.engine.switch
import net.inceptioncloud.dragonfly.engine.tooltip.Tooltip
import net.inceptioncloud.dragonfly.engine.tooltip.TooltipAlignment
import net.inceptioncloud.dragonfly.engine.widgets.assembled.*
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Image
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Rectangle
import net.inceptioncloud.dragonfly.engine.font.Typography
import net.inceptioncloud.dragonfly.engine.font.font
import net.minecraft.client.gui.GuiScreen
import net.minecraft.util.ResourceLocation
import java.net.URI

class NoCosmeticsUI(
    val previousScreen: GuiScreen
) : GuiScreen() {

    override var isNativeResolution: Boolean = true

    override fun initGui() {
        +Rectangle {
            x = 0.0f
            y = 0.0f
            width = this@NoCosmeticsUI.width.toFloat()
            height = this@NoCosmeticsUI.height.toFloat()
            color = DragonflyPalette.foreground.brighter(0.7)
        } id "background"

        +Image {
            val resolution = 1368 / 912.0f

            height = 400.0f
            width = height * resolution
            resourceLocation = ResourceLocation("dragonflyres/vectors/waiting.png")
            x = this@NoCosmeticsUI.width / 2 - width / 2
            y = 50.0f
        } id "vector"

        +TextField {
            positionBelow("vector", 10.0f)

            staticText = "Nothing here..."
            fontRenderer = font(Typography.HEADING_1)
            textAlignHorizontal = Alignment.CENTER
            color = DragonflyPalette.background

            width = (this@NoCosmeticsUI.width / 3.0f).coerceIn(400.0f, 800.0f)
            x = this@NoCosmeticsUI.width / 2 - width / 2

            adaptHeight = true
            adaptHeight()
        } id "title"

        +TextField {
            positionBelow("title", 20.0f)

            staticText = "It looks like you don't own any cosmetic items!\n" +
                    "Head over to the ${DragonflyPalette.accentNormal.chatCode}[Dragonfly Store]" +
                    "[https://store.playdragonfly.net]§r to purchase one of our many cosmetics. Don't worry - we have enough for everybody.\n\n" +
                    "If you just bought an item from our store, press the refresh button below to synchronize your cosmetics with our database."
            fontRenderer = font(Typography.BASE)
            textAlignHorizontal = Alignment.CENTER
            color = DragonflyPalette.background

            width = (this@NoCosmeticsUI.width / 2.5f).coerceIn(400.0f, 900.0f)
            x = this@NoCosmeticsUI.width / 2 - width / 2

            adaptHeight = true
            adaptHeight()
        } id "text"

        +OutlineButton {
            positionBelow("text", 90.0f)
            width = 350.0f
            height = 55.0f
            text = "Get your cosmetics now!"
            x = this@NoCosmeticsUI.width / 2 - width / 2
            color = DragonflyPalette.accentNormal
            onClick {
                openWebLink(URI("https://store.playdragonfly.net"))
            }
        } id "get-button"

        +OutlineButton {
            positionBelow("get-button", 10.0f)
            width = 350.0f
            height = 40.0f
            text = "Refresh cosmetics"
            x = this@NoCosmeticsUI.width / 2 - width / 2
            tooltip = Tooltip("Synchronize cosmetics with database", TooltipAlignment.BELOW)
            onClick {
                if (text == "Refresh cosmetics") {
                    GlobalScope.launch {
                        CosmeticsManager.refreshCosmeticsSync()

                        if (CosmeticsManager.dragonflyAccountCosmetics?.isNotEmpty() == true) {
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