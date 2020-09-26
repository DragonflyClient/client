package net.inceptioncloud.dragonfly.apps.cosmetics

import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Rectangle
import net.minecraft.client.gui.GuiScreen

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
            color = DragonflyPalette.foreground
        } id "background"

        super.initGui()
    }
}