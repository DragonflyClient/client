package net.inceptioncloud.dragonfly.apps.modmanager.controls.color

import net.inceptioncloud.dragonfly.Dragonfly
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
import net.inceptioncloud.dragonfly.ui.renderer.RenderUtils
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.GL11
import java.awt.Color

open class ColorSlider(
    initializerBlock: (ColorSlider.() -> Unit)? = null
) : AssembledWidget<ColorSlider>(initializerBlock), IPosition, IDimension {

    override var x: Double by property(0.0)
    override var y: Double by property(0.0)
    override var width: Double by property(0.0)
    override var height: Double by property(0.0)

    val innerPadding = 2.0

    var min: Int = 0
    var max: Int = 360
    var colorInterpolator: (Double) -> Color = { Color.getHSBColor(it.toFloat(), 1f, 1f) }
    var colorLetter: String by property("H")

    var currentProgress: Int = 0
        set(value) {
            field = value
            currentColor = colorInterpolator((value - min) / (max - min).toDouble())
        }
    var currentColor: Color = Color.WHITE

    private val circleSize = 20.0
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

        "color-letter"<TextField> {
            width = 30.0
            height = 40.0
            x = this@ColorSlider.x - width - 20.0
            y = this@ColorSlider.y + (this@ColorSlider.height / 2) - height / 2 - 2.0
            staticText = colorLetter
            textAlignVertical = Alignment.CENTER
            textAlignHorizontal = Alignment.END
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 45)
            color = DragonflyPalette.foreground
        }

        "current-value"<TextField> {
            width = 50.0
            height = 40.0
            x = this@ColorSlider.x + this@ColorSlider.width + 20.0
            y = this@ColorSlider.y + (this@ColorSlider.height / 2) - height / 2 - 2.0
            staticText = currentProgress.toString()
            textAlignVertical = Alignment.CENTER
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 45)
            color = DragonflyPalette.foreground
        }
    }

    override fun render() {
        val partWidth = 1.0

        GlStateManager.pushMatrix()
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0F)

        RenderUtils.drawArc(
            x.toFloat(), (y + (height / 2)).toFloat(),
            90, 270,
            (height / 2).toFloat(), (height / 2).toFloat(),
            colorInterpolator(0.0)
        )

        for (i in 0..width.toInt()) {
            val partLeft = x + (partWidth * i)
            val partRight = partLeft + partWidth
            val color = colorInterpolator(i / width)
            Gui.drawRect(partLeft, y, partRight, y + height, color.rgb)
        }

        RenderUtils.drawArc(
            (x + width).toFloat(), (y + (height / 2)).toFloat(),
            270, 450,
            (height / 2).toFloat(), (height / 2).toFloat(),
            colorInterpolator(1.0)
        )

        GlStateManager.popMatrix()

        super.render()
    }

    private fun computeCircleX(): Double {
        val room = (max - min)
        val progress = (currentProgress.coerceIn(min..max) - min) / room.toDouble()
        return x + (progress * width) - (circleSize / 2)
    }

    override fun update() {
        super.update()

        if (isDragging) {
            val newX = GraphicsEngine.getMouseX().coerceIn(x..x + width) - circleSize / 2.0
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

    private fun updateCurrent(new: Int = calculateMouseValue()) {
        if (isDragging) return
        currentProgress = new

        "slider-foreground"<FilledCircle> {
            detachAnimation<MorphAnimation>()
            morph(20, EaseQuad.IN_OUT, FilledCircle::x to computeCircleX())
                ?.post { _, _ -> if (Mouse.isButtonDown(0)) isDragging = true }
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