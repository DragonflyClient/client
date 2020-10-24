package net.inceptioncloud.dragonfly.controls

import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette.accentNormal
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation.Companion.morph
import net.inceptioncloud.dragonfly.engine.internal.Widget
import net.inceptioncloud.dragonfly.engine.sequence.easing.EaseQuad
import net.inceptioncloud.dragonfly.engine.widgets.assembled.TextField
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Image
import net.inceptioncloud.dragonfly.mods.core.OptionDelegate
import net.inceptioncloud.dragonfly.options.ChangeListener
import net.inceptioncloud.dragonfly.options.OptionKey
import net.inceptioncloud.dragonfly.utils.*
import net.inceptioncloud.dragonfly.engine.font.Typography
import net.inceptioncloud.dragonfly.engine.font.font
import net.minecraft.util.ResourceLocation
import kotlin.properties.Delegates
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.jvm.isAccessible

abstract class OptionControlElement<T>(
    val either: Either<KMutableProperty0<out T>, OptionKey<T>>,
    val name: String,
    val description: String? = null
) : ControlElement<OptionControlElement<T>>() {

    final override var x: Float by Delegates.notNull()
    final override var y: Float by Delegates.notNull()
    final override var width: Float by Delegates.notNull()
    override var height: Float = -1.0f

    val controlX by lazy { x + width * (2 / 3.0f) }
    val controlWidth by lazy { width / 3 }

    @Suppress("UNCHECKED_CAST")
    val optionKey = either.b ?: either.a!!.run {
        isAccessible = true
        (getDelegate() as OptionDelegate<T>).optionKey
    }

    var isResettable = false
    private var dirtyState = isDirty()

    private val listener = OptionControlElementListener(this)

    init {
        optionKey.addListener(listener)
    }

    override fun assemble(): Map<String, Widget<*>> = buildMap {
        put("name", TextField())
        put("description", TextField())
        put("reset", Image())
        putAll(controlAssemble())
    }

    override fun updateStructure() {
        val nameWidget = "name"<TextField> {
            x = this@OptionControlElement.x
            y = this@OptionControlElement.y
            width = this@OptionControlElement.width * (2 / 3.0f)
            adaptHeight = true
            fontRenderer = font(Typography.BASE)
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
                x = this@OptionControlElement.x
                y = nameWidget.y + nameWidget.height + 2.0f
                width = this@OptionControlElement.width * (2 / 3.0f)
                adaptHeight = true
                fontRenderer = font(Typography.SMALLEST)
                color = DragonflyPalette.background.altered { alphaFloat = 0.4f }
                staticText = description
            }!!.also { it.adaptHeight() }

            height = nameWidget.height + descriptionWidget.height + 2.0f
        }

        controlUpdateStructure()

        "reset"<Image> {
            width = 28.0f
            height = width
            x = this@OptionControlElement.x + this@OptionControlElement.width + 15.0f
            y = this@OptionControlElement.y + this@OptionControlElement.height / 2 - height / 2
            color = accentNormal.altered { alphaFloat = if (isDirty()) 1.0f else 0.0f }
            isVisible = isResettable
            resourceLocation = ResourceLocation("dragonflyres/icons/reset.png")
            clickAction = {
                optionKey.set(optionKey.getDefaultValue())
            }
        }
    }

    fun removeListener() {
        optionKey.removeListener(listener)
    }

    fun handleNewValue(newValue: T) {
        react(newValue)

        if (!isResettable) return
        "reset"<Image> {
            val dirty = isDirty()
            if (dirtyState != dirty) {
                dirtyState = dirty
                detachAnimation<MorphAnimation>()
                morph(30, EaseQuad.IN_OUT, Image::color to accentNormal.altered { alphaFloat = if (dirty) 1.0f else 0.0f })?.start()
            }
        }
    }

    private fun isDirty() = optionKey.get() != optionKey.getDefaultValue()

    /**
     * Assembling function for the control element that inherits from this class.
     */
    abstract fun controlAssemble(): Map<String, Widget<*>>

    /**
     * Structure updating function for the control element that inherits from this class.
     */
    abstract fun controlUpdateStructure()

    /**
     * React to changes to the option key.
     */
    abstract fun react(newValue: T)
}

@Keep
private class OptionControlElementListener<T>(val elem: OptionControlElement<T>) : ChangeListener<T> {
    override fun invoke(oldValue: T, newValue: T) {
        elem.handleNewValue(newValue)
    }
}