package net.inceptioncloud.dragonfly.engine.widgets.assembled

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.GraphicsEngine
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation.Companion.morph
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.sequence.easing.*
import net.inceptioncloud.dragonfly.engine.structure.*
import net.inceptioncloud.dragonfly.engine.widgets.primitive.*

class DragonflyButton(
    initializerBlock: (DragonflyButton.() -> Unit)? = null
) : AssembledWidget<DragonflyButton>(initializerBlock), IPosition, IDimension, IColor {

    override var x: Double by property(0.0)
    override var y: Double by property(0.0)
    override var width: Double by property(200.0)
    override var height: Double by property(20.0)
    override var color: WidgetColor by property(DragonflyPalette.accentNormal)

    var backgroundColor: WidgetColor by property(DragonflyPalette.background)
    var foregroundColor: WidgetColor by property(DragonflyPalette.foreground)
    var text: String by property("Button")
    var icon: ImageResource? by property(null)

    override fun assemble(): Map<String, Widget<*>> = mapOf(
        "background" to Rectangle(),
        "overlay" to Rectangle(),
        "text" to TextRenderer(),
        "icon-shadow" to Image(),
        "icon" to Image()
    )

    override fun updateStructure() {
        "background"<Rectangle> {
            x = this@DragonflyButton.x
            y = this@DragonflyButton.y
            width = this@DragonflyButton.width
            height = this@DragonflyButton.height
            color = this@DragonflyButton.backgroundColor
        }

        val buttonFontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 20)
        val stringWidth = buttonFontRenderer.getStringWidth(text)
        val iconSize = 14.0
        val iconMarginRight = 4.0
        val totalWidth = stringWidth + iconSize + iconMarginRight

        val iconWidget = "icon"<Image> {
            x = this@DragonflyButton.x + (this@DragonflyButton.width - totalWidth) / 2.0
            y = this@DragonflyButton.y + (this@DragonflyButton.height - iconSize) / 2.0
            width = iconSize
            height = iconSize
            dynamicTexture = icon?.dynamicTexture
            resourceLocation = icon?.resourceLocation
        }!!

        "icon-shadow"<Image> {
            x = iconWidget.x + 1.0
            y = iconWidget.y + 1.0
            width = iconSize
            height = iconSize
            dynamicTexture = icon?.dynamicTexture
            resourceLocation = icon?.resourceLocation
            color = WidgetColor(0, 0, 0, 100)
        }

        "text"<TextRenderer> {
            x = iconWidget.x + iconSize + iconMarginRight
            y = this@DragonflyButton.y + (this@DragonflyButton.height - buttonFontRenderer.height) / 2.0 - 1.0
            text = this@DragonflyButton.text
            color = this@DragonflyButton.foregroundColor
            fontRenderer = buttonFontRenderer
            dropShadow = true
        }

        "overlay"<Rectangle> {
            x = this@DragonflyButton.x
            y = this@DragonflyButton.y
            width = 5.0
            height = this@DragonflyButton.height
            color = this@DragonflyButton.color
        }
    }

    override fun update() {
        super.update()

        val mouseX = GraphicsEngine.getMouseX().toDouble()
        val mouseY = GraphicsEngine.getMouseY().toDouble()
        val overlayWidget = getWidget<Rectangle>("overlay")

        if (mouseX in x..x + width && mouseY in y..y + height) {
            overlayWidget?.detachAnimation<MorphAnimation>()
            overlayWidget?.morph(
                80,
                EaseQuad.IN_OUT,
                Rectangle::width to width
            )?.start()
        } else {
            overlayWidget?.detachAnimation<MorphAnimation>()
            overlayWidget?.morph(
                80,
                EaseQuad.IN_OUT,
                Rectangle::width to 5.0
            )?.start()
        }
    }
}