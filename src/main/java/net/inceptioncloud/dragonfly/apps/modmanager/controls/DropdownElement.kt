package net.inceptioncloud.dragonfly.apps.modmanager.controls

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation.Companion.morph
import net.inceptioncloud.dragonfly.engine.animation.post
import net.inceptioncloud.dragonfly.engine.contains
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.sequence.easing.EaseQuart
import net.inceptioncloud.dragonfly.engine.structure.IColor
import net.inceptioncloud.dragonfly.engine.structure.IPosition
import net.inceptioncloud.dragonfly.engine.widgets.assembled.RoundedRectangle
import net.inceptioncloud.dragonfly.engine.widgets.assembled.TextField
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Image
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Rectangle
import net.inceptioncloud.dragonfly.mc
import net.minecraft.util.ResourceLocation
import kotlin.reflect.KMutableProperty0

class DropdownElement(
    property: KMutableProperty0<out Enum<*>>,
    name: String,
    description: String?
) : OptionControlElement<Enum<*>>(property, name, description) {

    val allValues: List<Enum<*>> = optionKey.typeClass.enumConstants.toList()

    private val padding = 10.0
    private val fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 46)

    private val containerWidth by lazy {
        allValues.map { fontRenderer.getStringWidth(it.name) }.max()!!.coerceIn(100..controlWidth.toInt()) + padding * 2 + 50.0
    }
    private val containerHeight = 40.0
    private val containerX by lazy { x + width - containerWidth }
    private val containerY by lazy { y + (height - containerHeight) / 2.0 }

    private var isExpanded = false
    private var isInProgress = false

    override fun controlAssemble(): Map<String, Widget<*>> {
        val map = mutableMapOf<String, Widget<*>>(
            "container" to RoundedRectangle(),
            "selected" to TextField(),
            "icon" to Image(),
            "expanded::container" to RoundedRectangle()
        )

        for (index in allValues.indices)
            map["expanded::value-$index"] = TextField()
        for (index in 0 until allValues.size - 1)
            map["expanded::separator-$index"] = Rectangle()

        return map
    }

    override fun controlUpdateStructure() {
        val iconSize = 20.0

        "container"<RoundedRectangle> {
            x = containerX
            y = containerY
            width = containerWidth
            height = containerHeight
            color = DragonflyPalette.accentNormal
            arc = 5.0
            clickAction = {
                if (!isExpanded) expand()
            }
        }

        "selected"<TextField> {
            x = containerX + this@DropdownElement.padding
            y = containerY
            width = containerWidth - padding * 2 - 50.0
            height = containerHeight
            staticText = optionKey.get().name.toLowerCase().capitalize()
            color = DragonflyPalette.foreground
            fontRenderer = this@DropdownElement.fontRenderer
            textAlignVertical = Alignment.CENTER
        }

        "icon"<Image> {
            width = iconSize
            height = iconSize
            x = containerX + containerWidth - padding - width
            y = containerY + (containerHeight - height) / 2.0
            resourceLocation = ResourceLocation("dragonflyres/icons/expand.png")
            color = DragonflyPalette.foreground
        }

        // expanded container
        val originY = containerY + containerHeight - 5.0
        val separatorHeight = 2.0

        "expanded::container"<RoundedRectangle> {
            x = containerX
            y = originY
            width = containerWidth
            height = (containerHeight * allValues.size) + (separatorHeight * (allValues.size - 1))
            color = WidgetColor(230, 230, 230, 0)
            arc = 5.0
        }

        for ((index, value) in allValues.withIndex()) {
            "expanded::value-$index"<TextField> {
                x = containerX + this@DropdownElement.padding
                y = originY + (containerHeight * index) + (separatorHeight * index)
                height = containerHeight
                width = containerWidth - this@DropdownElement.padding
                color = DragonflyPalette.background.altered { alphaDouble = 0.0 }
                staticText = value.name.toLowerCase().capitalize()
                textAlignVertical = Alignment.CENTER
                fontRenderer = this@DropdownElement.fontRenderer
                hoverAction = {
                    if (isExpanded) {
                        color = if (isHovered) DragonflyPalette.accentNormal else DragonflyPalette.background
                    }
                }
                clickAction = {
                    optionKey.set(value)
                    collapse()
                }
            }

            if (index == 0) continue

            "expanded::separator-${index - 1}"<Rectangle> {
                width = containerWidth
                height = separatorHeight
                x = containerX
                y = originY + (containerHeight * index) + (separatorHeight * (index - 1))
                color = WidgetColor(210, 210, 210, 0)
            }
        }
    }

    override fun react(newValue: Enum<*>) {
        "selected"<TextField> {
            staticText = newValue.name.toLowerCase().capitalize()
        }
    }

    private fun expand() {
        if (isExpanded || isInProgress) return
        mc.currentScreen.focusHandler = DropdownFocusHandler()
        isExpanded = true
        isInProgress = true

        getWidgetsForExpansion()
            .forEach {
                it as IColor
                it as IPosition

                it.detachAnimation<MorphAnimation>()
                it.morph(
                    30, EaseQuart.OUT,
                    IColor::color to it.color.altered { alphaDouble = 1.0 },
                    IPosition::y to it.y + 20.0
                )?.post { _, _ -> isInProgress = false }?.start()
            }
    }

    private fun collapse() {
        if (!isExpanded || isInProgress) return
        mc.currentScreen.focusHandler = null
        isExpanded = false
        isInProgress = true

        getWidgetsForExpansion()
            .forEach {
                it as IColor
                it as IPosition

                val targetColor = if (it is TextField) DragonflyPalette.background.altered { alphaDouble = 0.0 }
                else it.color.altered { alphaDouble = 0.0 }

                it.detachAnimation<MorphAnimation>()
                it.morph(
                    30, EaseQuart.IN,
                    IColor::color to targetColor,
                    IPosition::y to it.y - 20.0
                )?.post { _, _ -> isInProgress = false }?.start()
            }
    }

    private fun getWidgetsForExpansion(): Collection<Widget<*>> =
        structure.filterKeys { it.startsWith("expanded::") }.values

    inner class DropdownFocusHandler : FocusHandler {
        override fun captureMouseFocus(data: MouseData) = data !in getWidget<RoundedRectangle>("expanded::container")
        override fun captureKeyboardFocus(key: Int) = true
        override fun handleCapturedMousePress(data: MouseData) = collapse()
    }
}