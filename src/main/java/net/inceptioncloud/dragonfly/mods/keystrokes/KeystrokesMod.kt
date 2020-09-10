package net.inceptioncloud.dragonfly.mods.keystrokes

import javafx.beans.value.ChangeListener
import net.inceptioncloud.dragonfly.controls.*
import net.inceptioncloud.dragonfly.controls.color.ColorControl
import javafx.beans.value.ObservableValue
import net.inceptioncloud.dragonfly.engine.internal.WidgetColor
import net.inceptioncloud.dragonfly.mods.core.DragonflyMod
import net.inceptioncloud.dragonfly.mods.core.OptionDelegate
import net.inceptioncloud.dragonfly.utils.Keep
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
    var bgActiveColor by option(WidgetColor(0.9, 0.5, 0.1, 1.0))
    var bgInactiveColor by option(WidgetColor(0.9, 0.9, 0.9, 0.2))

    private val listener = KeystrokesModListener()

    init {

        val textColorAcProp = KeystrokesMod::textActiveColor
        textColorAcProp.isAccessible = true
        (textColorAcProp.getDelegate() as OptionDelegate<*>).optionKey.objectProperty.addListener(listener)

        val textColorInProp = KeystrokesMod::textInactiveColor
        textColorInProp.isAccessible = true
        (textColorInProp.getDelegate() as OptionDelegate<*>).optionKey.objectProperty.addListener(listener)

        val bgColorAcProp = KeystrokesMod::bgActiveColor
        bgColorAcProp.isAccessible = true
        (bgColorAcProp.getDelegate() as OptionDelegate<*>).optionKey.objectProperty.addListener(listener)

        val bgColorInProp = KeystrokesMod::bgInactiveColor
        bgColorInProp.isAccessible = true
        (bgColorInProp.getDelegate() as OptionDelegate<*>).optionKey.objectProperty.addListener(listener)

    }

    override fun publishControls(): List<ControlElement<*>> = listOf(
        TitleControl("General"),
        BooleanControl(KeystrokesMod::enabled, "Enable mod"),
        TitleControl("Appearance", "Customize the appearance of the keystrokes mod on your screen"),
        NumberControl(
            KeystrokesMod::scale,
            "Scale",
            "The size of the keystroke boxes",
            min = 10.0,
            max = 20.0,
            decimalPlaces = 2,
            liveUpdate = true
        ),
        NumberControl(
            KeystrokesMod::fontSize,
            "Font size",
            "The size of the text in the keystroke boxes",
            min = 10.0,
            max = 20.0,
            decimalPlaces = 2,
            liveUpdate = true
        ),
        NumberControl(
            KeystrokesMod::space,
            "Space",
            "The space between the keystroke boxes",
            min = 0.0,
            max = 5.0,
            decimalPlaces = 2,
            liveUpdate = true
        ),
        DropdownElement(KeystrokesMod::position, "Position", "Position of the keystroke boxes"),
        TitleControl("Colors (pressed)", "Set the colors of the keystroke box if the corresponding key/button is pressed"),
        ColorControl(KeystrokesMod::textActiveColor, "Text"),
        ColorControl(KeystrokesMod::bgActiveColor, "Background"),
        TitleControl("Colors (released)", "Set the colors of the keystroke box if the corresponding key/button is not pressed"),
        ColorControl(KeystrokesMod::textInactiveColor, "Text"),
        ColorControl(KeystrokesMod::bgInactiveColor, "Background")
    )

}

@Keep
private class KeystrokesModListener : ChangeListener<Any?> {
    override fun changed(observable: ObservableValue<out Any?>?, oldValue: Any?, newValue: Any?) {
        if (oldValue is WidgetColor && newValue is WidgetColor) {
            if (oldValue != newValue) {
                Minecraft.getMinecraft().ingameGUI.initKeystrokes()
            }
        }
    }
}