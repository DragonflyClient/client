package net.inceptioncloud.minecraftmod.ui.components.button

import net.inceptioncloud.minecraftmod.transition.number.SmoothDoubleTransition
import net.inceptioncloud.minecraftmod.transition.supplier.ForwardBackward
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation

/**
 * A button that displays an image that can be clicked.
 *
 * @property resourceLocation the location of the image resource that is displayed on the button
 */
class ImageButton(
    buttonId: Int,
    x: Int,
    y: Int,
    width: Int = 20,
    height: Int = 20,
    private val resourceLocation: ResourceLocation
) : GuiButton(buttonId, x, y, width, height, "") {

    /**
     * The transition that moves the image a little bit up when hovering it.
     * This creates a smooth rise effect as the second layer with 20% opacity in black stays at the current position.
     */
    private val transitionHover = SmoothDoubleTransition.builder()
        .start(0.0).end(1.0)
        .fadeIn(10).stay(10).fadeOut(10)
        .autoTransformator(ForwardBackward { hovered })
        .build()

    /**
     * Draws this button on the screen.
     */
    override fun drawButton(mc: Minecraft, mouseX: Int, mouseY: Int) {
        if (visible) {
            val left = xPosition
            val top = yPosition
            val right = xPosition + width
            val bottom = yPosition + height

            hovered = mouseX >= left && mouseY >= top && mouseX < right && mouseY < bottom
            mouseDragged(mc, mouseX, mouseY)

            val f = 2.0F
            GlStateManager.scale(1 / f, 1 / f, 1 / f)
            GlStateManager.enableAlpha()
            GlStateManager.enableBlend()
            GlStateManager.color(0F, 0F, 0F, .2F)

            mc.textureManager.bindTexture(resourceLocation)
            Gui.drawModalRectWithCustomSizedTexture(
                (left * f).toInt(), (top * f).toInt(), 0F, 0F,
                (width * f).toInt(), (height * f).toInt(), width * f, height * f
            )

            GlStateManager.color(1F, 1F, 1F, 1F)

            mc.textureManager.bindTexture(resourceLocation)
            Gui.drawModalRectWithCustomSizedTexture(
                (left * f).toInt(), (top * f - (2 * transitionHover.get())).toInt(),
                0F, 0F, (width * f).toInt(), (height * f).toInt(), width * f, height * f
            )

            GlStateManager.scale(f, f, f)
            GlStateManager.disableBlend()
            GlStateManager.disableAlpha()
        }
    }

    /**
     * Destroys the Button by removing all transitions.
     */
    override fun destroy() {
        transitionHover.destroy()
    }
}