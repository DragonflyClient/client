package net.inceptioncloud.dragonfly.cosmetics.types.wings

import net.inceptioncloud.dragonfly.controls.*
import net.inceptioncloud.dragonfly.controls.color.ColorControl
import net.inceptioncloud.dragonfly.cosmetics.Cosmetic
import kotlin.math.min
import kotlin.reflect.KClass

/**
 * The base class for the "wings" cosmetic.
 */
object CosmeticWings : Cosmetic<CosmeticWingsConfig>(1) {

    override val models = listOf(CosmeticWingsModel())

    override val configClass: KClass<CosmeticWingsConfig> = CosmeticWingsConfig::class

    override fun generateControls(config: CosmeticWingsConfig) = config.run {
        listOf(
            ColorControl(::color.pseudo(), "Wings color"),
            BooleanControl(::enableBlending.pseudo(), "Enable blending", "Activate blending for your wings to create a glow effect"),
            BooleanControl(::enableShadows.pseudo(), "Enable shadows", "Enable lightning and shadows to appear on your wings"),
            NumberControl(::scale.pseudo(), "Scale", "Define the size of your wings", min = 0.4, max = 1.0, decimalPlaces = 2, liveUpdate = true),
            NumberControl(::speed.pseudo(), "Speed", "Customize the speed at which the flights swing", min = 0.0, max = 1.0, decimalPlaces = 2),

            TitleControl("Advanced settings", "In this section, you can find advanced settings for your Dragonfly wings"),
            NumberControl(::tilt.pseudo(), "Tilt", min = 0.0, max = 15.0, decimalPlaces = 2, liveUpdate = true),
            NumberControl(::height.pseudo(), "Height", min = 0.15, max = 0.3, decimalPlaces = 2, liveUpdate = true),
            NumberControl(::rotationX.pseudo(), "Rotation X", min = 0.0, max = 0.4, decimalPlaces = 2, liveUpdate = true),
            NumberControl(::rotationY.pseudo(), "Rotation Y", min = 0.0, max = 0.4, decimalPlaces = 2, liveUpdate = true),
            NumberControl(::rotationZ.pseudo(), "Rotation Z", min = 0.5, max = 1.0, decimalPlaces = 2, liveUpdate = true),
            NumberControl(::wingTipRotation.pseudo(), "Wing tip rotation", min = 0.0, max = 1.0, decimalPlaces = 2, liveUpdate = true),
            NumberControl(::folding.pseudo(), "Folding", min = 20.0, max = 40.0, decimalPlaces = 2, liveUpdate = true)
        )
    }
}