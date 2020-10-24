package net.inceptioncloud.dragonfly.controls

import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.internal.Widget
import net.inceptioncloud.dragonfly.engine.widgets.assembled.TextField
import net.inceptioncloud.dragonfly.engine.font.Typography
import net.inceptioncloud.dragonfly.engine.font.font
import kotlin.properties.Delegates

class TextControl(
    val text: String
) : ControlElement<TextControl>() {

    override var x: Float by Delegates.notNull()
    override var y: Float by Delegates.notNull()
    override var width: Float by Delegates.notNull()
    override var height: Float = -1.0f

    override fun assemble(): Map<String, Widget<*>> = buildMap {
        put("name", TextField())
    }

    override fun updateStructure() {
        val nameWidget = "name"<TextField> {
            x = this@TextControl.x
            y = this@TextControl.y
            width = this@TextControl.width * (2 / 3.0f)
            adaptHeight = true
            fontRenderer = font(Typography.BASE)
            color = DragonflyPalette.background
            staticText = text
        }!!.also { it.adaptHeight() }

        height += nameWidget.height + 2.0f
    }
}