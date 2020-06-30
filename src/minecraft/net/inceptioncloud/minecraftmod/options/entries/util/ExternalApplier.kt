package net.inceptioncloud.minecraftmod.options.entries.util

import net.inceptioncloud.minecraftmod.options.OptionKey
import net.inceptioncloud.minecraftmod.transition.number.SmoothDoubleTransition
import net.inceptioncloud.minecraftmod.ui.screens.ModOptionsUI
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation

/**
 * An interface that can be implemented in option entries that support external appliers.
 */
interface ExternalApplier<T>
{
    /**
     * An external apply function.
     *
     * If this value isn't null, the value of the key won't automatically be set when the
     * entry value is modified. Instead, it will be saved when the "Save and Exit" button
     * in the options screen is pressed.
     *
     * @see ModOptionsUI
     */
    var externalApplier: ((T, OptionKey<T>) -> Unit)?

    /**
     * A cache for the current value in case it should be applied externally.
     */
    var valueCache: T

    /**
     * Whether the current value isn't the value of the key.
     */
    var valueChanged: Boolean

    /**
     * A transition that fades in the text offset and save icon.
     */
    var transitionExternalApplier: SmoothDoubleTransition

    /**
     * The method that applies the value using the [externalApplier].
     *
     * It is called when the "Save and Exit" button is pressed thus the value should be
     * applied.
     */
    fun applyExternally ()

    /**
     * Renders an icon to the left of the entry text that indicates that is has to be
     * saved externally. This icon only appears if the value isn't the same as the
     * key value.
     */
    fun renderChangeState (x: Int, y: Int, height: Int, width: Int, key: OptionKey<T>, value: T): Boolean
    {
        val f = 3.0
        val resource = ResourceLocation("dragonflyres/icons/save.png")
        Minecraft.getMinecraft().textureManager.bindTexture(resource)

        GlStateManager.scale(1 / f, 1 / f, 1 / f)
        GlStateManager.color(1F, 1F, 1F, 1F)

        val size = ((height - 10) * transitionExternalApplier.get() * f).toInt()
        val centerX = ((x + height / 2) * f).toInt()
        val centerY = ((y + height / 2) * f).toInt()

        Gui.drawModalRectWithCustomSizedTexture(
                centerX - size / 2,
                centerY - size / 2,
                0F,
                0F,
                size,
                size,
                size.toFloat(),
                size.toFloat()
        )

        GlStateManager.scale(f, f, f)

        return key.get() != value
    }
}