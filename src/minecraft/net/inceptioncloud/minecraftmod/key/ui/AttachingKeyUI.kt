package net.inceptioncloud.minecraftmod.key.ui

import net.inceptioncloud.minecraftmod.design.color.DragonflyPalette
import net.inceptioncloud.minecraftmod.engine.animation.Animation
import net.inceptioncloud.minecraftmod.engine.animation.alter.MorphAnimation
import net.inceptioncloud.minecraftmod.engine.animation.alter.MorphAnimation.Companion.morph
import net.inceptioncloud.minecraftmod.engine.animation.alter.MorphAnimation.Companion.morphBetween
import net.inceptioncloud.minecraftmod.engine.internal.*
import net.inceptioncloud.minecraftmod.engine.sequence.easing.EaseCubic
import net.inceptioncloud.minecraftmod.engine.sequence.easing.EaseQuad
import net.inceptioncloud.minecraftmod.engine.widgets.assembled.TextField
import net.inceptioncloud.minecraftmod.engine.widgets.primitive.FilledCircle
import net.inceptioncloud.minecraftmod.key.KeyController
import net.minecraft.client.gui.GuiScreen
import kotlin.concurrent.thread
import kotlin.math.pow
import kotlin.math.sqrt

class AttachingKeyUI(val key: String) : GuiScreen() {

    override var backgroundFill: WidgetColor? = DragonflyPalette.BACKGROUND

    private var result: KeyController.Result? = null

    init {
        thread(start = true) {
            Thread.sleep(4000)

            result = KeyController.attachKey(key)
            println(result)

            getWidget<FilledCircle>("loading-circle-1")?.run {
                detachAnimation<MorphAnimation>()
                morph(duration = 50, easing = EaseQuad.IN_OUT) {
                    x = width / 2 - size / 2
                }.run {
                    postActions.add { _: Animation, widget: Widget<*> ->
                        @Suppress("UNCHECKED_CAST")
                        (widget as Widget<FilledCircle>).morph(duration = 100, easing = EaseCubic.IN_OUT) {
                            color = if (result!!.success) WidgetColor(0x34c464) else WidgetColor(0xff6663)
                            size = sqrt(width.toDouble().pow(2.0) + height.toDouble().pow(2.0))
                            x = width / 2 - size / 2
                            y = height / 2 - size / 2
                        }.start()
                    }
                    start()
                }
            }

            getWidget<FilledCircle>("loading-circle-2")?.run {
                detachAnimation<MorphAnimation>()
                morph(duration = 50, easing = EaseQuad.IN_OUT) {
                    x = width / 2 - size / 2
                }.run {
                    postActions.add { _: Animation, widget: Widget<*> -> widget.isVisible = false }
                    start()
                }
            }
        }
    }

    override fun initGui() {
        +TextField(
            x = width / 2 - 100.0,
            y = height / 2 - 20.0,
            width = 200.0,
            height = 20.0,
            staticText = "Validating Key...",
            color = DragonflyPalette.FOREGROUND,
            textAlignVertical = Alignment.START,
            textAlignHorizontal = Alignment.CENTER
        ) id "header"

        +FilledCircle(
            x = width / 2 - 15.0,
            y = height / 2.0,
            size = 7.0,
            color = DragonflyPalette.ACCENT_NORMAL
        ).apply {
            if (result != null) {
                size = sqrt(width.toDouble().pow(2.0) + height.toDouble().pow(2.0))
                x = width / 2 - size / 2
                y = height / 2 - size / 2
                color = if (result?.success == true) WidgetColor(0x34c464) else WidgetColor(0xff6663)
            }
        } id "loading-circle-1"

        +FilledCircle(
            x = width / 2 + 8.0,
            y = height / 2.0,
            size = 7.0,
            color = DragonflyPalette.ACCENT_NORMAL
        ).apply { isVisible = result == null } id "loading-circle-2"
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        if (result == null) {
            getWidget<FilledCircle>("loading-circle-1")?.morphBetween(
                duration = 150,
                easing = EaseQuad.IN_OUT,
                first = { x = width / 2 - 15.0 },
                second = { x = width / 2 + 15.0 - size }
            )
            getWidget<FilledCircle>("loading-circle-2")?.morphBetween(
                duration = 150,
                easing = EaseQuad.IN_OUT,
                first = { x = width / 2 + 15.0 - size },
                second = { x = width / 2 - 15.0 }
            )
        }

        super.drawScreen(mouseX, mouseY, partialTicks)
    }
}