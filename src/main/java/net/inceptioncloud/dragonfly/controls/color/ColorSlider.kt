package net.inceptioncloud.dragonfly.controls.color

import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.*
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation.Companion.morph
import net.inceptioncloud.dragonfly.engine.animation.post
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.sequence.easing.EaseQuad
import net.inceptioncloud.dragonfly.engine.structure.IDimension
import net.inceptioncloud.dragonfly.engine.structure.IPosition
import net.inceptioncloud.dragonfly.engine.widgets.assembled.TextField
import net.inceptioncloud.dragonfly.engine.widgets.primitive.FilledCircle
import net.inceptioncloud.dragonfly.engine.font.Typography
import net.inceptioncloud.dragonfly.engine.font.font
import net.inceptioncloud.dragonfly.ui.renderer.RenderUtils
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.GL11
import java.awt.Color

open class ColorSlider(
    initializerBlock: (ColorSlider.() -> Unit)? = null
) : AssembledWidget<ColorSlider>(initializerBlock), IPosition, IDimension {

    override var x: Float by property(0.0F)
    override var y: Float by property(0.0F)
    override var width: Float by property(0.0F)
    override var height: Float by property(0.0F)

    val innerPadding = 2.0f

    var min: Int = 0
    var max: Int = 360
    var colorInterpolator: (Float) -> Color = { Color.getHSBColor(it, 1f, 1f) }
    var colorLetter: String by property("H")

    var currentProgress: Int = 0
        set(value) {
            field = value
            currentColor = colorInterpolator((value - min) / (max - min).toFloat())
        }
    var currentColor: Color = Color.WHITE

    private val circleSize = 20.0f
    private var isDragging = false

    override fun assemble(): Map<String, Widget<*>> = mapOf(
        "slider-foreground" to FilledCircle(),
        "slider-foreground-inner" to FilledCircle(),
        "current-value" to TextField(),
        "color-letter" to TextField()
    )

    override fun updateStructure() {
        "slider-foreground"<FilledCircle> {
            size = circleSize
            x = computeCircleX()
            y = this@ColorSlider.y + (this@ColorSlider.height / 2) - (size / 2)
            color = DragonflyPalette.background
        }

        "slider-foreground-inner"<FilledCircle> {
            size = circleSize - innerPadding * 2
            x = computeCircleX() + innerPadding
            y = this@ColorSlider.y + (this@ColorSlider.height / 2) - (size / 2)
            color = DragonflyPalette.foreground
        }

        // -50
        "color-letter"<TextField> {
            width = 20.0f
            height = 40.0f
            x = this@ColorSlider.x - width - 30.0f
            y = this@ColorSlider.y + (this@ColorSlider.height / 2) - height / 2 - 2.0f
            staticText = colorLetter
            textAlignVertical = Alignment.CENTER
            textAlignHorizontal = Alignment.START
            fontRenderer = font(Typography.BASE)
            color = DragonflyPalette.foreground
        }

        // +60
        "current-value"<TextField> {
            width = 40.0f
            height = 40.0f
            x = this@ColorSlider.x + this@ColorSlider.width + 20.0f
            y = this@ColorSlider.y + (this@ColorSlider.height / 2) - height / 2 - 2.0f
            staticText = currentProgress.toString()
            textAlignVertical = Alignment.CENTER
            textAlignHorizontal = Alignment.END
            fontRenderer = font(Typography.BASE)
            color = DragonflyPalette.foreground
        }
    }

    override fun render() {
        val partWidth = 1.0f

        GlStateManager.pushMatrix()
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0F)

        RenderUtils.drawArc(
            x, (y + (height / 2)),
            90, 270,
            (height / 2), (height / 2),
            colorInterpolator(0.0f)
        )

        for (i in 0..width.toInt()) {
            val partLeft = x + (partWidth * i)
            val partRight = partLeft + partWidth
            val color = colorInterpolator(i / width)
            Gui.drawRect(partLeft, y, partRight, y + height, color.rgb)
        }

        RenderUtils.drawArc(
            (x + width), (y + (height / 2)),
            270, 450,
            (height / 2), (height / 2),
            colorInterpolator(1.0f)
        )

        GlStateManager.popMatrix()

        super.render()
    }

    private fun computeCircleX(): Float {
        val room = (max - min)
        val progress = (currentProgress.coerceIn(min..max) - min) / room.toFloat()
        return x + (progress * width) - (circleSize / 2)
    }

    override fun update() {
        super.update()

        if (isDragging) {
            val newX = GraphicsEngine.getMouseX().coerceIn(x..x + width) - circleSize / 2
            currentProgress = calculateMouseValue()

            "slider-foreground"<FilledCircle> {
                x = newX
            }
            "slider-foreground-inner"<FilledCircle> {
                x = newX + innerPadding
            }
            "current-value"<TextField> {
                staticText = currentProgress.toString()
            }
        }
    }

    override fun handleMousePress(data: MouseData) {
        val c = getWidget<FilledCircle>("slider-foreground")!!

        val mouseX = data.mouseX.toDouble()
        val mouseY = data.mouseY.toDouble()

        when {
            data in c -> isDragging = true
            mouseX in x..x + width && mouseY in y - 10.0..y + height + 10.0 -> updateCurrent()
        }

        super.handleMousePress(data)
    }

    override fun handleMouseRelease(data: MouseData) {
        if (isDragging) {
            isDragging = false
        }

        super.handleMouseRelease(data)
    }

    fun updateCurrent(new: Int = calculateMouseValue(), continueDragging: Boolean = true) {
        if (isDragging) return
        currentProgress = new

        "slider-foreground"<FilledCircle> {
            detachAnimation<MorphAnimation>()
            morph(20, EaseQuad.IN_OUT, FilledCircle::x to computeCircleX())
                ?.post { _, _ -> if (Mouse.isButtonDown(0) && continueDragging) isDragging = true }
                ?.start()
        }

        "slider-foreground-inner"<FilledCircle> {
            detachAnimation<MorphAnimation>()
            morph(20, EaseQuad.IN_OUT, FilledCircle::x to computeCircleX() + innerPadding)?.start()
        }

        "current-value"<TextField> {
            staticText = new.toString()
        }
    }

    private fun calculateMouseValue(): Int {
        val progress = (GraphicsEngine.getMouseX() - x) / width
        return (min + (progress * (max - min))).toInt().coerceIn(min..max)
    }
}