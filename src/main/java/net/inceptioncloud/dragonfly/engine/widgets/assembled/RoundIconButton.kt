package net.inceptioncloud.dragonfly.engine.widgets.assembled

import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation.Companion.morph
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.sequence.easing.EaseQuad
import net.inceptioncloud.dragonfly.engine.structure.*
import net.inceptioncloud.dragonfly.engine.widgets.primitive.FilledCircle
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Image

class RoundIconButton(
    initializerBlock: (RoundIconButton.() -> Unit)? = null
) : AssembledWidget<RoundIconButton>(initializerBlock), IPosition, IDimension {

    override var x: Double by property(0.0)
    override var y: Double by property(0.0)
    override var width: Double by property(60.0)
    override var height: Double by property(60.0)

    var backgroundColor by property(DragonflyPalette.background)
    var foregroundColor by property(DragonflyPalette.foreground)
    var highlightColor by property(DragonflyPalette.accentNormal)

    var icon: ImageResource? by property(null)

    private val outlineWidth = 2.0
    private val iconMargin = 10.0

    override fun assemble(): Map<String, Widget<*>> = mapOf(
        "outline" to FilledCircle(),
        "smooth-background" to FilledCircle(),
        "background" to FilledCircle(),
        "icon" to Image()
    )

    override fun updateStructure() {
        "outline"<FilledCircle> {
            x = this@RoundIconButton.x - outlineWidth
            y = this@RoundIconButton.y - outlineWidth
            size = this@RoundIconButton.width + outlineWidth * 2
            color = foregroundColor
            smooth = true
            isVisible = size > 0.0
        }

        "smooth-background"<FilledCircle> {
            x = this@RoundIconButton.x
            y = this@RoundIconButton.y
            size = this@RoundIconButton.width
            color = backgroundColor
            smooth = true
        }

        "background"<FilledCircle> {
            x = this@RoundIconButton.x
            y = this@RoundIconButton.y
            size = this@RoundIconButton.width
            color = backgroundColor
            smooth = false
        }

        "icon"<Image> {
            x = this@RoundIconButton.x + iconMargin
            y = this@RoundIconButton.y + iconMargin
            width = (this@RoundIconButton.width - iconMargin * 2).coerceAtLeast(0.0)
            height = (this@RoundIconButton.height - iconMargin * 2).coerceAtLeast(0.0)
            resourceLocation = icon?.resourceLocation
            dynamicTexture = icon?.dynamicTexture
        }
    }

    override fun canUpdateHoverState(): Boolean = isVisible

    override fun handleHoverStateUpdate() {
        fun FilledCircle.animate() {
            detachAnimation<MorphAnimation>()
            if (isHovered) {
                morph(
                    30, EaseQuad.IN_OUT,
                    FilledCircle::color to highlightColor
                )
            } else {
                morph(
                    30, EaseQuad.IN_OUT,
                    FilledCircle::color to backgroundColor
                )
            }?.start()
        }

        "background"<FilledCircle> { animate() }
        "smooth-background"<FilledCircle> { animate() }
        super.handleHoverStateUpdate()
    }
}