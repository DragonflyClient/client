package net.inceptioncloud.dragonfly.mods.togglesneak

import net.inceptioncloud.dragonfly.controls.BooleanControl
import net.inceptioncloud.dragonfly.controls.ControlElement
import net.inceptioncloud.dragonfly.controls.NumberControl
import net.inceptioncloud.dragonfly.controls.TitleControl
import net.inceptioncloud.dragonfly.controls.color.ColorControl
import net.inceptioncloud.dragonfly.engine.internal.WidgetColor
import net.inceptioncloud.dragonfly.mods.core.DragonflyMod

object ToggleSneakMod : DragonflyMod("ToggleSneak") {

    var enabledSneak by option(false)
    var enabledSprint by option(false)
    var doSneak = false
    var doSprint = false

    var enabledOverlay by option(true)
    var overlayText = ""
        get() {
            updateOverlayText()
            return field
        }
    var overlayColor by option(WidgetColor(1.0, 1.0, 1.0, 1.0))
    var overlaySize by option(16)

    override fun publishControls(): List<ControlElement<*>> = listOf(
        TitleControl("General"),
        BooleanControl(ToggleSneakMod::enabledSneak, "Enable ToggleSneak"),
        BooleanControl(ToggleSneakMod::enabledSprint, "Enable ToggleSprint"),
        TitleControl("InGame Overlay"),
        BooleanControl(ToggleSneakMod::enabledOverlay, "Enable Overlay"),
        ColorControl(ToggleSneakMod::overlayColor, "Text Color"),
        NumberControl(ToggleSneakMod::overlaySize, "Text Size", min = 5.0, max = 25.0, decimalPlaces = 1)
    )

    fun updateOverlayText() {
        overlayText = if (enabledOverlay) {
            var resultText = ""

            if (enabledSneak && doSneak) {
                if (enabledSprint && doSprint) {
                    resultText = "Sneaking & Sprinting"
                } else {
                    resultText += "Sneaking"
                }
            } else if (enabledSprint && doSprint) {
                resultText = "Sprinting"
            }

            resultText
        } else {
            ""
        }
    }

}