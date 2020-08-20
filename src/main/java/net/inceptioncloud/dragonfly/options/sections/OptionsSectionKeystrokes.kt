package net.inceptioncloud.dragonfly.options.sections

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.keystrokes.KeyStrokesManager
import net.inceptioncloud.dragonfly.options.entries.factories.OptionEntryMultipleChoiceFactory
import net.inceptioncloud.dragonfly.options.entries.factories.OptionEntryRangeDoubleFactory
import net.inceptioncloud.dragonfly.options.entries.util.OptionChoice
import net.minecraft.client.Minecraft
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.math.roundToInt

object OptionsSectionKeystrokes {

    /**
     * On/Off switch of the keystrokes feature
     */
    @JvmStatic
    val switch = OptionEntryMultipleChoiceFactory.optionEntryMultipleChoice {
        name = "Keystrokes"
        description = "Turning this on activates the KeyStrokes feature, turning this off deactivates the KeyStrokes feature."
        +OptionChoice(0, "Off")
        +OptionChoice(1, "On")
        externalApply = { value, optionKey ->
            optionKey.set(value)
            reloadOverlay()
        }
        key {
            fileKey = "keystrokes"
            default = { 0 }
        }
    }

    /**
     * Position of the keystrokes overlay
     */
    @JvmStatic
    val position = OptionEntryMultipleChoiceFactory.optionEntryMultipleChoice {
        name = "Position"
        description = "Changes the position of the KeyStrokes overlay."
        +OptionChoice(0, "Top Left")
        externalApply = { value, optionKey ->
            optionKey.set(value)
            reloadOverlay()
        }
        key {
            fileKey = "position"
            default = { 0 }
        }
    }

    /**
     * Width and Height of the KeyStrokes overlay boxes
     */
    @JvmStatic
    val scale = OptionEntryRangeDoubleFactory.optionEntryRangeDouble {
        name = "Scale"
        description = "Scales the boxes of the keystrokes overlay."
        minValue = 10.0
        maxValue = 20.0
        externalApply = { value, optionKey ->
            optionKey.set(value)
            reloadOverlay()
        }
        formatter = {
            val round = (it * 10).roundToInt() / 10.0
            val nf: NumberFormat = NumberFormat.getNumberInstance(Locale.US)
            val df: DecimalFormat = nf as DecimalFormat

            df.applyPattern("0.0")
            df.format(round)
        }
        key {
            fileKey = "scale"
            default = { 15.0 }
        }
    }

    /**
     * FontSize of the KeyStrokes overlay text
     */
    @JvmStatic
    val fontSize = OptionEntryRangeDoubleFactory.optionEntryRangeDouble {
        name = "Font Size"
        description = "Changes the fontSize of the KeyStrokes Overlay."
        minValue = 10.0
        maxValue = 20.0
        externalApply = { value, optionKey ->
            optionKey.set(value)
            reloadOverlay()
        }
        formatter = {
            val round = (it * 10).roundToInt() / 10.0
            val nf: NumberFormat = NumberFormat.getNumberInstance(Locale.US)
            val df: DecimalFormat = nf as DecimalFormat

            df.applyPattern("0.0")
            df.format(round)
        }
        key {
            fileKey = "fontSize"
            default = { 15.0 }
        }
    }

    /**
     * Width and Height of the KeyStrokes overlay boxes
     */
    @JvmStatic
    val space = OptionEntryRangeDoubleFactory.optionEntryRangeDouble {
        name = "Space"
        description = "Changes the space between the boxes of the KeyStrokes feature."
        minValue = 0.0
        maxValue = 5.0
        externalApply = { value, optionKey ->
            optionKey.set(value)
            reloadOverlay()
        }
        formatter = {
            val round = (it * 10).roundToInt() / 10.0
            val nf: NumberFormat = NumberFormat.getNumberInstance(Locale.US)
            val df: DecimalFormat = nf as DecimalFormat

            df.applyPattern("0.0")
            df.format(round)
        }
        key {
            fileKey = "space"
            default = { 3.0 }
        }
    }

    /**
     * The init block creates the option section and adds all elements to it.
     */
    @JvmStatic
    fun init() {
        OptionSectionFactory.optionSection {
            title = "KeyStrokes"

            +switch
            +position
            +scale
            +space
            +fontSize
        }
    }

    fun reloadOverlay() {

        /*Minecraft.getMinecraft().ingameGUI.keyStrokesScale = scale.invoke()!!
        Minecraft.getMinecraft().ingameGUI.keyStrokesSpace = space.invoke()!!
        Minecraft.getMinecraft().ingameGUI.keyStrokesFontSize = fontSize.invoke()!!

        Minecraft.getMinecraft().ingameGUI.initKeyStrokes()*/

    }

}