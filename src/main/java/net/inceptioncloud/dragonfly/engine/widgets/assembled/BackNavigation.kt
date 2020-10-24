package net.inceptioncloud.dragonfly.engine.widgets.assembled

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.structure.*
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Image
import net.inceptioncloud.dragonfly.overlay.ScreenOverlay
import net.minecraft.client.gui.GuiScreen
import net.minecraft.util.ResourceLocation

class BackNavigation(
    initializerBlock: (BackNavigation.() -> Unit)? = null
) : AssembledWidget<BackNavigation>(initializerBlock), IPosition, IDimension, IColor {

    override var x: Float by property(0.0F)
    override var y: Float by property(0.0F)
    override var width: Float = -1.0f
    override var height: Float = 40.0f
    override var color: WidgetColor by property(DragonflyPalette.foreground)

    private var action: () -> Unit = {}

    override fun assemble(): Map<String, Widget<*>> = mapOf(
        "text" to TextField(),
        "icon" to Image()
    )

    override fun updateStructure() {
        val icon = "icon"<Image> {
            x = this@BackNavigation.x
            y = this@BackNavigation.y
            width = this@BackNavigation.height
            height = this@BackNavigation.height
            resourceLocation = ResourceLocation("dragonflyres/icons/back.png")
        }!!

        val text = "text"<TextField> {
            staticText = "Back"
            x = icon.x + icon.width + 10.0f
            y = this@BackNavigation.y
            width = 100.0f
            height = this@BackNavigation.height
            textAlignVertical = Alignment.CENTER
            textAlignHorizontal = Alignment.START
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = (height * 1.2).toInt())
            color = this@BackNavigation.color
        }!!

        this.width = text.x + text.width - icon.x
    }

    override fun handleMousePress(data: MouseData) {
        if (data.mouseX.toDouble() in x..x + width && data.mouseY.toDouble() in y..y + height) {
            action()
        }
    }

    fun action(block: () -> Unit) {
        action = block
    }

    fun gui(gui: GuiScreen) {
        action = {
            ScreenOverlay.displayGui(gui)
        }
    }
}