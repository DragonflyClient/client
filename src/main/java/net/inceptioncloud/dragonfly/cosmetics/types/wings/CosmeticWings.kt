package net.inceptioncloud.dragonfly.cosmetics.types.wings

import net.inceptioncloud.dragonfly.controls.*
import net.inceptioncloud.dragonfly.controls.color.ColorControl
import net.inceptioncloud.dragonfly.cosmetics.Cosmetic
import kotlin.reflect.KClass

/**
 * The base class for the "wings" cosmetic.
 */
object CosmeticWings : Cosmetic<CosmeticWingsConfig>(1) {

    override val models = listOf(CosmeticWingsModel())

    override val configClass: KClass<CosmeticWingsConfig> = CosmeticWingsConfig::class

    override fun generateControls(config: CosmeticWingsConfig) = listOf(
        ColorControl(config::color.pseudo(), "Wings color"),
        BooleanControl(config::enableBlending.pseudo(), "Enable blending", "Activate blending for your wings to create a glow effect"),
        BooleanControl(config::enableShadows.pseudo(), "Enable shadows", "Enable lightning and shadows to appear on your wings"),
        NumberControl(config::scale.pseudo(), "Scale", "Define the size of your wings", min = 0.4, max = 1.0, decimalPlaces = 2, liveUpdate = true)
    )
}