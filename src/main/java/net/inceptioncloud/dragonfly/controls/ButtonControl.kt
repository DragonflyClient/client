package net.inceptioncloud.dragonfly.controls

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.internal.Widget
import net.inceptioncloud.dragonfly.engine.widgets.assembled.OutlineButton
import net.inceptioncloud.dragonfly.engine.widgets.assembled.TextField
import net.inceptioncloud.dragonfly.engine.font.Typography
import net.inceptioncloud.dragonfly.engine.font.font
import kotlin.properties.Delegates

class ButtonControl(
    val name: String,
    val description: String? = null,
    val text: String = "",
    val onClick: () -> Unit
) : ControlElement<ButtonControl>() {

    override fun assemble(): Map<String, Widget<*>> = mapOf(
        "button" to OutlineButton(),
        "name" to TextField(),
        "description" to TextField()
    )

    override fun updateStructure() {

        "button"<OutlineButton> {
            width = 200.0
            height = 50.0
            this@ButtonControl.height = height
            x = this@ButtonControl.x + this@ButtonControl.width - 200.0
            y = this@ButtonControl.y
            text = this@ButtonControl.text
            hoverColor = DragonflyPalette.accentNormal
            onClick(onClick)
        }

        val nameWidget = "name"<TextField> {
            x = this@ButtonControl.x
            y = this@ButtonControl.y
            width = this@ButtonControl.width * (2 / 3.0)
            adaptHeight = true
            fontRenderer = font(Typography.BASE)
            color = DragonflyPalette.background
            staticText = name
        }!!.also { it.adaptHeight() }

        if (description == null) {
            "description"<TextField> {
                x = 0.0
                y = 0.0
                width = 0.0
                height = 0.0
                isVisible = false
            }

            height = nameWidget.height
        } else {
            val descriptionWidget = "description"<TextField> {
                x = this@ButtonControl.x
                y = nameWidget.y + nameWidget.height
                width = this@ButtonControl.width * (2 / 3.0)
                adaptHeight = true
                fontRenderer = font(Typography.SMALLEST)
                color = DragonflyPalette.background.altered { alphaDouble = 0.4 }
                staticText = description
            }!!.also { it.adaptHeight() }

            height = nameWidget.height + descriptionWidget.height
        }

    }

    override var x by Delegates.notNull<Double>()
    override var y by Delegates.notNull<Double>()
    override var width by Delegates.notNull<Double>()
    override var height: Double = -1.0

}