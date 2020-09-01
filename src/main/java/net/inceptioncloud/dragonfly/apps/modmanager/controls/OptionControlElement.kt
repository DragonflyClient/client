package net.inceptioncloud.dragonfly.apps.modmanager.controls

import javafx.beans.value.ChangeListener
import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.internal.Widget
import net.inceptioncloud.dragonfly.engine.widgets.assembled.TextField
import net.inceptioncloud.dragonfly.mods.core.OptionDelegate
import kotlin.properties.Delegates
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.jvm.isAccessible

abstract class OptionControlElement<T>(
    val property: KMutableProperty0<out T>,
    val name: String,
    val description: String? = null
) : ControlElement<OptionControlElement<T>>() {

    final override var x by Delegates.notNull<Double>()
    final override var y by Delegates.notNull<Double>()
    final override var width by Delegates.notNull<Double>()
    override var height: Double = -1.0

    val controlX by lazy { x + width * (2 / 3.0) }
    val controlWidth by lazy { width / 3.0 }

    @Suppress("UNCHECKED_CAST")
    val optionKey = kotlin.run {
        property.isAccessible = true
        (property.getDelegate() as OptionDelegate<T>).optionKey
    }

    val listener: ChangeListener<T> = ChangeListener { _, oldValue, newValue ->
        if (oldValue != newValue) {
            react(newValue)
        }
    }

    init {
        optionKey.objectProperty.addListener(listener)
    }

    override fun assemble(): Map<String, Widget<*>> = buildMap {
        put("name", TextField())
        put("description", TextField())
        putAll(controlAssemble())
    }

    override fun updateStructure() {
        val nameWidget = "name"<TextField> {
            x = this@OptionControlElement.x
            y = this@OptionControlElement.y
            width = this@OptionControlElement.width * (2 / 3.0)
            adaptHeight = true
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 50, useScale = false)
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
                x = this@OptionControlElement.x
                y = nameWidget.y + nameWidget.height + 2.0
                width = this@OptionControlElement.width * (2 / 3.0)
                adaptHeight = true
                fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 35, useScale = false)
                color = DragonflyPalette.background.altered { alphaDouble = 0.4 }
                staticText = description
            }!!.also { it.adaptHeight() }

            height = nameWidget.height + descriptionWidget.height + 2.0
        }

        controlUpdateStructure()
    }

    fun removeListener() {
        optionKey.objectProperty.removeListener(listener)
    }

    abstract fun controlAssemble(): Map<String, Widget<*>>

    abstract fun controlUpdateStructure()

    abstract fun react(newValue: T)
}