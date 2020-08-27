package net.inceptioncloud.dragonfly.engine.widgets.assembled

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation.Companion.morph
import net.inceptioncloud.dragonfly.engine.animation.post
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.sequence.easing.EaseQuad
import net.inceptioncloud.dragonfly.engine.structure.*
import net.minecraft.client.Minecraft
import net.minecraft.client.audio.PositionedSoundRecord
import net.minecraft.util.ResourceLocation

class RoundToggleButton(
    initializerBlock: (RoundToggleButton.() -> Unit)? = null
) : AssembledWidget<RoundToggleButton>(initializerBlock), IPosition, IDimension, IColor {

    override var x: Double by property(0.0)
    override var y: Double by property(0.0)
    override var width: Double by property(200.0)
    override var height: Double by property(20.0)
    override var color: WidgetColor by property(DragonflyPalette.background)

    var arc: Double by property(3.0)

    var text: String by property("Button")
    var textSize: Int by property(30)
    var textColor: WidgetColor by property(DragonflyPalette.foreground)

    var toggleColor: WidgetColor by property(DragonflyPalette.accentNormal)
    var toggleTextColor: WidgetColor by property(DragonflyPalette.foreground)

    var isToggled: Boolean = false
    var enableClickSound: Boolean = true
    private var onClick: () -> Unit = {}

    override fun assemble(): Map<String, Widget<*>> = mapOf(
        "container" to RoundedRectangle(),
        "text" to TextField()
    )

    override fun updateStructure() {
        "container"<RoundedRectangle> {
            x = this@RoundToggleButton.x
            y = this@RoundToggleButton.y
            width = this@RoundToggleButton.width
            height = this@RoundToggleButton.height
            color = statefulBackgroundColor()
            arc = this@RoundToggleButton.arc
        }

        val buttonFontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = textSize, useScale = false)

        "text"<TextField> {
            x = this@RoundToggleButton.x
            y = this@RoundToggleButton.y - 1.0
            width = this@RoundToggleButton.width
            height = this@RoundToggleButton.height - 1.0
            staticText = this@RoundToggleButton.text
            color = statefulTextColor()
            fontRenderer = buttonFontRenderer
            textAlignHorizontal = Alignment.CENTER
            textAlignVertical = Alignment.CENTER
        }
    }

    private fun statefulBackgroundColor() = if (isToggled) toggleColor else this@RoundToggleButton.color
    private fun statefulTextColor() = if (isToggled) toggleTextColor else this@RoundToggleButton.textColor

    override fun handleMousePress(data: MouseData) {
        if (isHovered) {
            if (enableClickSound) {
                Minecraft.getMinecraft().soundHandler.playSound(PositionedSoundRecord.create(ResourceLocation("gui.button.press"), 1.0f))
            }

            toggle()
            onClick()
        }
    }

    /**
     * Changes whether the button is currently toggled.
     */
    fun toggle() {
        isToggled = !isToggled
        getWidget<RoundedRectangle>("container")?.morph(
            30, EaseQuad.IN_OUT,
            RoundedRectangle::color to statefulBackgroundColor()
        )?.start()
        getWidget<TextField>("text")?.morph(
            30, EaseQuad.IN_OUT,
            TextField::color to statefulTextColor()
        )?.start()
    }

    /**
     * Type safe builder to set the value for the [onClick] function.
     */
    fun onClick(block: () -> Unit) {
        onClick = block
    }
}