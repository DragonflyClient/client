package net.inceptioncloud.dragonfly.apps.modmanager.controls

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.font.FontWeight
import net.inceptioncloud.dragonfly.engine.internal.AssembledWidget
import net.inceptioncloud.dragonfly.engine.internal.Widget
import net.inceptioncloud.dragonfly.engine.structure.IDimension
import net.inceptioncloud.dragonfly.engine.structure.IPosition
import net.inceptioncloud.dragonfly.engine.widgets.assembled.TextField
import kotlin.properties.Delegates

class TitleControl(
    val name: String,
    val description: String? = null
) : AssembledWidget<TitleControl>(), IPosition, IDimension {

    override var x by Delegates.notNull<Double>()
    override var y by Delegates.notNull<Double>()
    override var width by Delegates.notNull<Double>()
    override var height: Double = -1.0

    override fun assemble(): Map<String, Widget<*>> = buildMap {
        put("name", TextField())
        put("description", TextField())
    }

    override fun updateStructure() {
        val nameWidget = "name"<TextField> {
            x = this@TitleControl.x
            y = this@TitleControl.y
            width = this@TitleControl.width * (2 / 3.0)
            adaptHeight = true
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 60, useScale = false, fontWeight = FontWeight.MEDIUM)
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
                x = this@TitleControl.x
                y = nameWidget.y + nameWidget.height
                width = this@TitleControl.width * (2 / 3.0)
                adaptHeight = true
                fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 45, useScale = false)
                color = DragonflyPalette.background.altered { alphaDouble = 0.6 }
                staticText = description
            }!!.also { it.adaptHeight() }

            height = nameWidget.height + descriptionWidget.height
        }
    }
}