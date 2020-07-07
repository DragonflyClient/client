package net.inceptioncloud.dragonfly.ui.components.button

import net.inceptioncloud.dragonfly.Dragonfly.fontDesign
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette.accentBright
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette.accentNormal
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette.foreground
import net.inceptioncloud.dragonfly.engine.font.FontWeight
import net.inceptioncloud.dragonfly.transition.color.ColorTransitionBuilder
import net.inceptioncloud.dragonfly.transition.supplier.ForwardBackward
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiButton
import java.awt.Color

/**
 * A button in the [BluePalette] style.
 */
class DragonflyPaletteButton : GuiButton {
    /**
     * Transition changes the fill color when hovering.
     */
    private var fillColor = ColorTransitionBuilder()
        .start(accentBright.base).end(foreground.base)
        .amountOfSteps(30).autoTransformator(ForwardBackward { hovered }).build()

    /**
     * Transition changes the text color when hovering.
     */
    private var textColor = ColorTransitionBuilder()
        .start(foreground.base).end(accentNormal.base)
        .amountOfSteps(30).autoTransformator(ForwardBackward { hovered }).build()

    /**
     * The opacity of the button.
     */
    var opacity = 1.0f

    /**
     * Inherit Constructor
     *
     * @see GuiButton
     */
    constructor(buttonId: Int, x: Int, y: Int, buttonText: String?) : super(buttonId, x, y, buttonText)

    /**
     * Inherit Constructor
     *
     * @see GuiButton
     */
    constructor(
        buttonId: Int,
        x: Int,
        y: Int,
        widthIn: Int,
        heightIn: Int,
        buttonText: String?
    ) : super(buttonId, x, y, widthIn, heightIn, buttonText)

    /**
     * Draws this button on the screen.
     */
    override fun drawButton(mc: Minecraft, mouseX: Int, mouseY: Int) {
        if (visible) {
            val fontrenderer = fontDesign.defaultFont.fontRendererAsync { size = 19; fontWeight = FontWeight.MEDIUM }
            val border = 1.0
            val left = xPosition + border
            val top = yPosition + border
            val right = xPosition + width - border
            val bottom = yPosition + height - border
            hovered = mouseX >= left && mouseY >= top && mouseX < right && mouseY < bottom
            mouseDragged(mc, mouseX, mouseY)
            Gui.drawRect(left - border, top - border, right + border, bottom + border, accentNormal.rgb)
            Gui.drawRect(left, top, right, bottom, fillColor.get().rgb)
            val stringWith = fontrenderer?.getStringWidth(displayString) ?: 0 + 4
            fontrenderer?.drawStringWithCustomShadow(
                displayString,
                (left + width / 2 - stringWith / 2).toInt(),
                top.toInt() + height / 2 - 4,
                textColor.get().rgb,
                Color(0, 0, 0, if (hovered) 40 else 70).rgb,
                0.6f
            )
        }
    }

    /**
     * Destroys the Button by removing all transitions.
     */
    override fun destroy() {
        fillColor.destroy()
        textColor.destroy()
    }
}