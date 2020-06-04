package net.inceptioncloud.minecraftmod.engine.internal

import net.minecraft.client.gui.GuiScreen

/**
 * A builder for easily creating pairs of a widget and an id.
 *
 * It is initialized when calling the unary plus function on a string in a [GuiScreen] with either the
 * widget or the id set. After that, you can specify the other value by using one of the two infix
 * functions. When they are called, the pair will automatically be built and added to the buffer.
 *
 * @property buffer the buffer that the widget is added to
 * @property widget the widget that, if given, is mapped to the id
 * @property id the id that, if given, is mapped to the widget
 */
class WidgetIdBuilder(val buffer: WidgetBuffer, var widget: Widget<*>? = null, var id: String? = null)
{
    /**
     * Provide an id for the widget.
     */
    infix fun id(id: String)
    {
        this.id = id
        build()
    }

    /**
     * Provide a widget for the id.
     */
    infix fun widget(widget: Widget<*>)
    {
        this.widget = widget
        build()
    }

    /**
     * Build the pair.
     */
    fun build()
    {
        buffer.add(id!! to widget!!)
    }
}