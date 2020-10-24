package net.inceptioncloud.dragonfly.controls

import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.font.*
import net.inceptioncloud.dragonfly.engine.internal.Widget
import net.inceptioncloud.dragonfly.engine.internal.WidgetColor
import net.inceptioncloud.dragonfly.engine.widgets.assembled.TextField
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Rectangle
import kotlin.properties.Delegates

class TitleControl(
    val name: String,
    val description: String? = null
) : ControlElement<TitleControl>() {

    override var x: Float by Delegates.notNull()
    override var y: Float by Delegates.notNull()
    override var width: Float by Delegates.notNull()
    override var height: Float = -1.0f

    override fun assemble(): Map<String, Widget<*>> = buildMap {
        put("name", TextField())
        put("description", TextField())
        put("horizontal-rule", Rectangle())
    }

    override fun updateStructure() {
        val nameWidget = "name"<TextField> {
            x = this@TitleControl.x
            y = this@TitleControl.y
            width = this@TitleControl.width * (2 / 3.0f)
            adaptHeight = true
            fontRenderer = font(Typography.HEADING_2)
            color = DragonflyPalette.background
            staticText = name
        }!!.also { it.adaptHeight() }

        if (description == null) {
            "description"<TextField> {
                x = 0.0f
                y = 0.0f
                width = 0.0f
                height = 0.0f
                isVisible = false
            }

            height = nameWidget.height
        } else {
            val descriptionWidget = "description"<TextField> {
                x = this@TitleControl.x
                y = nameWidget.y + nameWidget.height
                width = this@TitleControl.width * (2 / 3.0f)
                adaptHeight = true
                fontRenderer = font(Typography.SMALLEST)
                color = DragonflyPalette.background.altered { alphaFloat = 0.4f }
                staticText = description
            }!!.also { it.adaptHeight() }

            height = nameWidget.height + descriptionWidget.height
        }

        "horizontal-rule"<Rectangle> {
            x = this@TitleControl.x
            y = this@TitleControl.y + this@TitleControl.height + 5.0f
            height = 2.0f
            width = this@TitleControl.width
            color = WidgetColor(0, 0, 0, 30)
        }

        height += 7.0f
    }
}