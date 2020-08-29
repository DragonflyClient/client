package net.inceptioncloud.dragonfly.mods

import javafx.beans.value.ChangeListener
import net.inceptioncloud.dragonfly.apps.modmanager.controls.*
import net.inceptioncloud.dragonfly.apps.modmanager.controls.color.ColorControl
import net.inceptioncloud.dragonfly.engine.internal.WidgetColor
import net.inceptioncloud.dragonfly.mods.core.DragonflyMod
import net.inceptioncloud.dragonfly.mods.core.OptionDelegate
import net.inceptioncloud.dragonfly.mods.keystrokes.EnumKeystrokesPosition
import net.minecraft.client.Minecraft
import kotlin.reflect.jvm.isAccessible


object KeystrokesMod : DragonflyMod("Keystrokes") {

    var enabled by option(true)
    var scale by option(15.0) { it in 10.0..20.0 }
    var fontSize by option(15.0) { it in 10.0..20.0 }
    var space by option(3.0) { it in 0.0..5.0 }
    var position by option(EnumKeystrokesPosition.TOP_LEFT)

    var textActiveColor by option(WidgetColor(1.0, 1.0, 1.0, 1.0))
    var textInactiveColor by option(WidgetColor(1.0, 1.0, 1.0, 1.0))
    var bgActiveColor by option(WidgetColor(0.9, 0.5, 0.1, 0.7))
    var bgInactiveColor by option(WidgetColor(0.9, 0.9, 0.9, 0.2))

    val listener: ChangeListener<Any?> = ChangeListener { _, oldValue, newValue ->
        if (oldValue is WidgetColor && newValue is WidgetColor) {
            if (oldValue != newValue) {
                Minecraft.getMinecraft().ingameGUI.initKeyStrokes(true)
            }
        }
    }

    init {

        val textColorAcProp = ::textActiveColor
        textColorAcProp.isAccessible = true
        (textColorAcProp.getDelegate() as OptionDelegate<*>).optionKey.objectProperty.addListener(listener)

        val textColorInProp = ::textInactiveColor
        textColorInProp.isAccessible = true
        (textColorInProp.getDelegate() as OptionDelegate<*>).optionKey.objectProperty.addListener(listener)

        val bgColorAcProp = ::bgActiveColor
        bgColorAcProp.isAccessible = true
        (bgColorAcProp.getDelegate() as OptionDelegate<*>).optionKey.objectProperty.addListener(listener)

        val bgColorInProp = ::bgInactiveColor
        bgColorInProp.isAccessible = true
        (bgColorInProp.getDelegate() as OptionDelegate<*>).optionKey.objectProperty.addListener(listener)

    }

    override fun publishControls(): List<ControlElement<*>> = listOf(
        TitleControl("General"),
        BooleanControl(::enabled, "Enable mod"),
        TitleControl("Appearance", "Customize the appearance of the keystrokes mod on your screen"),
        NumberControl(
            ::scale,
            "Scale",
            "The size of the keystroke boxes",
            min = 10.0,
            max = 20.0,
            decimalPlaces = 2,
            liveUpdate = true
        ),
        NumberControl(
            ::fontSize,
            "Font size",
            "The size of the text in the keystroke boxes",
            min = 10.0,
            max = 20.0,
            decimalPlaces = 2,
            liveUpdate = true
        ),
        NumberControl(
            ::space,
            "Space",
            "The space between the keystroke boxes",
            min = 0.0,
            max = 5.0,
            decimalPlaces = 2,
            liveUpdate = true
        ),
        DropdownElement(::position, "Position", "Position of the keystroke boxes"),
        TitleControl("Colors (pressed)", "Set the colors of the keystroke box if the corresponding key/button is pressed"),
        ColorControl(::textActiveColor, "Text"),
        ColorControl(::bgActiveColor, "Background"),
        TitleControl("Colors (released)", "Set the colors of the keystroke box if the corresponding key/button is not pressed"),
        ColorControl(::textInactiveColor, "Text"),
        ColorControl(::bgInactiveColor, "Background")
    )

}