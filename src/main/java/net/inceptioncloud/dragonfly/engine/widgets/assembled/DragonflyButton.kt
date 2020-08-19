package net.inceptioncloud.dragonfly.engine.widgets.assembled

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.GraphicsEngine
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation.Companion.morph
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.sequence.easing.EaseQuad
import net.inceptioncloud.dragonfly.engine.structure.*
import net.inceptioncloud.dragonfly.engine.widgets.primitive.*
import net.minecraft.client.Minecraft
import net.minecraft.client.audio.PositionedSoundRecord
import net.minecraft.util.ResourceLocation

/**
 * ## Dragonfly Button Assembled Widget
 *
 * A button in the default "Dragonfly style" built as an assembled widget.
 *
 * @property color the accent color of the button that indicates that it is hovered
 * @property backgroundColor the color for the button's background
 * @property foregroundColor the color for the button's text
 * @property text the text that is displayed by the button
 * @property icon an icon that is displayed on the left side of the text
 * @property isHovered whether the button is currently hovered
 * @property enableClickSound whether the default Minecraft click sound should be
 * played when the button is pressed
 * @property onClick the action that is performed when the button is clicked
 */
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
    var iconSize: Double? by property(null)
    var useScale: Boolean by property(true)

    var isHovered: Boolean = false
    var enableClickSound: Boolean = true
    private var onClick: () -> Unit = {}

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

        val buttonFontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = height.toInt(), useScale = useScale)
        val stringWidth = buttonFontRenderer.getStringWidth(text)
        val iconMarginRight = height / 5.0
        val iconSize = this.iconSize ?: height - 6.0
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
            x = iconWidget.x + 2.0
            y = iconWidget.y + 2.0
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
            shadowDistance = 2.0
        }

        "overlay"<Rectangle> {
            x = this@DragonflyButton.x
            y = this@DragonflyButton.y
            width = this@DragonflyButton.width / 40.0
            height = this@DragonflyButton.height
            color = this@DragonflyButton.color
        }
    }

    override fun update() {
        super.update()

        val mouseX = GraphicsEngine.getMouseX()
        val mouseY = GraphicsEngine.getMouseY()
        val offset = width / 40.0
        val overlayWidget = getWidget<Rectangle>("overlay")

        if (mouseX in x..x + width && mouseY in y..y + height) {
            if (isHovered)
                return

            overlayWidget?.detachAnimation<MorphAnimation>()
            overlayWidget?.morph(
                60,
                EaseQuad.IN_OUT,
                Rectangle::width to width - offset
            )?.start()
            isHovered = true
        } else {
            if (!isHovered)
                return

            overlayWidget?.detachAnimation<MorphAnimation>()
            overlayWidget?.morph(
                60,
                EaseQuad.IN_OUT,
                Rectangle::width to offset
            )?.start()
            isHovered = false
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