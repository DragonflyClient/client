package net.inceptioncloud.dragonfly.controls

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.internal.Widget
import net.inceptioncloud.dragonfly.engine.widgets.assembled.TextField
import net.inceptioncloud.dragonfly.mods.core.OptionDelegate
import net.inceptioncloud.dragonfly.options.ChangeListener
import net.inceptioncloud.dragonfly.options.OptionKey
import net.inceptioncloud.dragonfly.utils.*
import kotlin.properties.Delegates
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.jvm.isAccessible

abstract class OptionControlElement<T>(
    val either: Either<KMutableProperty0<out T>, OptionKey<T>>,
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
    val optionKey = either.b ?: either.a!!.run {
        isAccessible = true
        (getDelegate() as OptionDelegate<T>).optionKey
    }

    private val listener = OptionControlElementListener(this)

    init {
        optionKey.addListener(listener)
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
        optionKey.removeListener(listener)
    }

    abstract fun controlAssemble(): Map<String, Widget<*>>

    abstract fun controlUpdateStructure()

    abstract fun react(newValue: T)
}

@Keep
private class OptionControlElementListener<T>(val elem: OptionControlElement<T>) : ChangeListener<T> {
    override fun invoke(oldValue: T, newValue: T) {
        elem.react(newValue)
    }
}