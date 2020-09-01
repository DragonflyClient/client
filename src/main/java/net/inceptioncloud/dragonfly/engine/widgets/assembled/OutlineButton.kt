package net.inceptioncloud.dragonfly.engine.widgets.assembled

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.GraphicsEngine
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation.Companion.morph
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.sequence.easing.EaseQuad
import net.inceptioncloud.dragonfly.engine.structure.*
import net.inceptioncloud.dragonfly.engine.toWidgetColor
import net.inceptioncloud.dragonfly.engine.widgets.primitive.*
import net.minecraft.client.Minecraft
import net.minecraft.client.audio.PositionedSoundRecord
import net.minecraft.util.ResourceLocation

/**
 * ## Simple Button Assembled Widget
 *
 * A plain button that displays a text and has three color properties to customize its look.
 * When the button is hovered, the background color's brightness is increased.
 *
 * @property color the color for the button's background
 * @property foregroundColor the color for the button's text
 * @property outlineColor the color for the button's outline
 * @property outlineStroke the width of the button's outline
 * @property text the text that is displayed by the button
 * @property isHovered whether the button is currently hovered
 * @property enableClickSound whether the default Minecraft click sound should be
 * played when the button is pressed
 * @property onClick the action that is performed when the button is clicked
 */
class OutlineButton(
    initializerBlock: (OutlineButton.() -> Unit)? = null
) : AssembledWidget<OutlineButton>(initializerBlock), IPosition, IDimension, IColor {

    override var x: Double by property(0.0)
    override var y: Double by property(0.0)
    override var width: Double by property(200.0)
    override var height: Double by property(20.0)
    override var color: WidgetColor by property(DragonflyPalette.background)
    var hoverColor: WidgetColor by property(DragonflyPalette.background.brighter(0.8))

    var text: String by property("Button")
    var outlineStroke: Double by property(2.0)
    var outlineColor: WidgetColor by property(DragonflyPalette.foreground)
    var foregroundColor: WidgetColor by property(DragonflyPalette.foreground)

    var enableClickSound: Boolean = true
    private var onClick: () -> Unit = {}

    override fun assemble(): Map<String, Widget<*>> = mapOf(
        "container" to Rectangle(),
        "text" to TextField()
    )

    override fun updateStructure() {
        "container"<Rectangle> {
            x = this@OutlineButton.x
            y = this@OutlineButton.y
            width = this@OutlineButton.width
            height = this@OutlineButton.height
            color = this@OutlineButton.color
            outlineStroke = this@OutlineButton.outlineStroke
            outlineColor = this@OutlineButton.outlineColor
        }

        val buttonFontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = height.toInt(), useScale = false)

        "text"<TextField> {
            x = this@OutlineButton.x
            y = this@OutlineButton.y
            width = this@OutlineButton.width
            height = this@OutlineButton.height
            staticText = this@OutlineButton.text
            color = this@OutlineButton.foregroundColor
            fontRenderer = buttonFontRenderer
            dropShadow = true
            shadowDistance = 2.0
            textAlignHorizontal = Alignment.CENTER
            textAlignVertical = Alignment.CENTER
        }
    }

    override fun handleHoverStateUpdate() {
        val container = getWidget<Rectangle>("container")

        if (isHovered) {
            container?.detachAnimation<MorphAnimation>()
            container?.morph(
                60,
                EaseQuad.IN_OUT,
                Rectangle::color to hoverColor
            )?.start()
        } else {
            container?.detachAnimation<MorphAnimation>()
            container?.morph(
                60,
                EaseQuad.IN_OUT,
                Rectangle::color to color
            )?.start()
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