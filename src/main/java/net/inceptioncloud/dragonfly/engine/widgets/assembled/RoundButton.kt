package net.inceptioncloud.dragonfly.engine.widgets.assembled

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.structure.*
import net.minecraft.client.Minecraft
import net.minecraft.client.audio.PositionedSoundRecord
import net.minecraft.util.ResourceLocation

class RoundButton(
    initializerBlock: (RoundButton.() -> Unit)? = null
) : AssembledWidget<RoundButton>(initializerBlock), IPosition, IDimension, IColor {

    override var x: Double by property(0.0)
    override var y: Double by property(0.0)
    override var width: Double by property(200.0)
    override var height: Double by property(20.0)
    override var color: WidgetColor by property(DragonflyPalette.background)

    var arc: Double by property(3.0)

    var text: String by property("Button")
    var textSize: Int by property(30)
    var textColor: WidgetColor by property(DragonflyPalette.foreground)

    var enableClickSound: Boolean = true
    private var onClick: () -> Unit = {}

    override fun assemble(): Map<String, Widget<*>> = mapOf(
        "container" to RoundRectangle(),
        "text" to TextField()
    )

    override fun updateStructure() {
        "container"<RoundRectangle> {
            x = this@RoundButton.x
            y = this@RoundButton.y
            width = this@RoundButton.width
            height = this@RoundButton.height
            color = this@RoundButton.color
            arc = this@RoundButton.arc
        }

        val buttonFontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = textSize, useScale = false)

        "text"<TextField> {
            x = this@RoundButton.x
            y = this@RoundButton.y - 1.0
            width = this@RoundButton.width
            height = this@RoundButton.height - 1.0
            staticText = this@RoundButton.text
            color = this@RoundButton.textColor
            fontRenderer = buttonFontRenderer
            textAlignHorizontal = Alignment.CENTER
            textAlignVertical = Alignment.CENTER
        }
    }

    override fun handleMousePress(data: MouseData) {
        if (isHovered) {
            if (enableClickSound) {
                Minecraft.getMinecraft().soundHandler.playSound(PositionedSoundRecord.create(ResourceLocation("gui.button.press"), 1.0f))
            }
            onClick()
        }
    }

    /**
     * Type safe builder to set the value for the [onClick] function.
     */
    fun onClick(block: () -> Unit) {
        onClick = block
    }
}