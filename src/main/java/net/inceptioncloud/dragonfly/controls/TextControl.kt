package net.inceptioncloud.dragonfly.controls

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.internal.Widget
import net.inceptioncloud.dragonfly.engine.widgets.assembled.TextField
import kotlin.properties.Delegates

class TextControl(
    val text: String
) : ControlElement<TextControl>() {

    override var x by Delegates.notNull<Double>()
    override var y by Delegates.notNull<Double>()
    override var width by Delegates.notNull<Double>()
    override var height: Double = -1.0

    override fun assemble(): Map<String, Widget<*>> = buildMap {
        put("name", TextField())
    }

    override fun updateStructure() {
        val nameWidget = "name"<TextField> {
            x = this@TextControl.x
            y = this@TextControl.y
            width = this@TextControl.width * (2 / 3.0)
            adaptHeight = true
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 50, useScale = false)
            color = DragonflyPalette.background
            staticText = text
        }!!.also { it.adaptHeight() }

        height += nameWidget.height + 2.0
    }
}