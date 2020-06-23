package net.inceptioncloud.minecraftmod.key.ui

import kotlinx.coroutines.*
import net.inceptioncloud.minecraftmod.design.color.DragonflyPalette
import net.inceptioncloud.minecraftmod.engine.animation.alter.MorphAnimation
import net.inceptioncloud.minecraftmod.engine.animation.alter.MorphAnimation.Companion.morph
import net.inceptioncloud.minecraftmod.engine.animation.alter.MorphAnimation.Companion.morphBetween
import net.inceptioncloud.minecraftmod.engine.animation.post
import net.inceptioncloud.minecraftmod.engine.internal.*
import net.inceptioncloud.minecraftmod.engine.sequence.easing.EaseCubic
import net.inceptioncloud.minecraftmod.engine.sequence.easing.EaseQuad
import net.inceptioncloud.minecraftmod.engine.widgets.assembled.TextField
import net.inceptioncloud.minecraftmod.engine.widgets.primitive.FilledCircle
import net.inceptioncloud.minecraftmod.engine.widgets.primitive.Rectangle
import net.inceptioncloud.minecraftmod.key.KeyController
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.*
import kotlin.concurrent.thread
import kotlin.math.pow
import kotlin.math.sqrt

@Suppress("UNCHECKED_CAST")
class AttachingKeyUI(val key: String) : GuiScreen() {

    override var backgroundFill: WidgetColor? = DragonflyPalette.BACKGROUND

    private var result: KeyController.Result? = null

    private var guiMainMenu: GuiMainMenu? = null

    private var shouldBuildMainMenu = false

    init {
        thread(start = true) {
            Thread.sleep(5000)

            result = KeyController.attachKey(key)
            println(result)

            getWidget<FilledCircle>("loading-circle-1")?.run {
                detachAnimation<MorphAnimation>()
                morph(duration = 50, easing = EaseQuad.IN_OUT) {
                    x = width / 2 - size / 2
                }.post { _, widget ->
                    (widget as Widget<FilledCircle>).morph(duration = 130, easing = EaseCubic.IN_OUT) {
                        color = if (result!!.success) WidgetColor(0x34c464) else WidgetColor(0xff6663)
                        size = sqrt(width.toDouble().pow(2.0) + height.toDouble().pow(2.0))
                        x = width / 2 - size / 2
                        y = height / 2 - size / 2
                    }.post { _, _ ->
                        val overlay = Rectangle(
                            x = 0.0,
                            y = 0.0,
                            width = width.toDouble(),
                            height = height.toDouble(),
                            color = if (result!!.success) WidgetColor(0x34c464) else WidgetColor(0xff6663)
                        )
                        val overlayBorder = Rectangle(
                            x = -20.0,
                            y = 0.0,
                            width = width.toDouble(),
                            height = height.toDouble(),
                            color = DragonflyPalette.BACKGROUND
                        )

                        +overlayBorder id "rectangle-overlay-border"
                        +overlay id "rectangle-overlay"
                        shouldBuildMainMenu = true
                        buffer.content.filter { !it.key.startsWith("rectangle-overlay") }.forEach { it.value.isVisible = false }

                        GlobalScope.launch {
                            delay(2500)
                            overlay.morph(duration = 150, easing = EaseCubic.IN_OUT) {
                                x = this@AttachingKeyUI.width.toDouble() + 5.0
                            }.start()
                            overlayBorder.morph(duration = 180, easing = EaseCubic.IN_OUT) {
                                x = this@AttachingKeyUI.width.toDouble()
                            }.post { _, _ ->
                                Minecraft.getMinecraft().currentScreen = guiMainMenu
                            }.start()
                        }
                    }.start()
                }.start()
            }

            getWidget<FilledCircle>("loading-circle-2")?.run {
                detachAnimation<MorphAnimation>()
                morph(duration = 50, easing = EaseQuad.IN_OUT) {
                    x = width / 2 - size / 2
                }.post { _, widget -> widget.isVisible = false }.start()
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
        backgroundFill
            ?.let { drawRect(0, 0, width, height, backgroundFill?.rgb ?: 0xFFFFFFFF.toInt()) }

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
        } else if (result?.success == true) {
            if (shouldBuildMainMenu && guiMainMenu == null) {
                guiMainMenu = GuiMainMenu().apply {
                    val scaledResolution = ScaledResolution(Minecraft.getMinecraft())
                    val scaledWidth = scaledResolution.scaledWidth
                    val scaledHeight = scaledResolution.scaledHeight
                    setWorldAndResolution(Minecraft.getMinecraft(), scaledWidth, scaledHeight)
                }
            }

            guiMainMenu?.drawScreen(mouseX, mouseY, partialTicks)
        }

        super.drawScreen(mouseX, mouseY, partialTicks)
    }
}