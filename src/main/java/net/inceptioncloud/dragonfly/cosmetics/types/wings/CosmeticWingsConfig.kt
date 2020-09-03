package net.inceptioncloud.dragonfly.cosmetics.types.wings

import com.google.gson.JsonObject
import net.inceptioncloud.dragonfly.cosmetics.logic.CosmeticConfig
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette

/**
 * The configuration for the [wings][CosmeticWings] cosmetic
 */
class CosmeticWingsConfig(jsonObject: JsonObject) : CosmeticConfig(jsonObject) {
    var enableBlending by boolean(false)
    var enableShadows by boolean(true)
    var color by color(DragonflyPalette.accentNormal)
    var scale by double(0.7) { it in 0.4..1.0 }
    var tilt by double(15.0) { it in 0.0..15.0 }
    var height by double(0.25) { it in 0.15..0.3 }
    var rotationX by double(0.4) { it in 0.0..0.4 }
    var rotationY by double(0.2) { it in 0.0..0.4 }
    var rotationZ by double(1.0) { it in 0.5..1.0 }
    var wingTipRotation by double(0.6) { it in 0.0..1.0 }
    var speed by double(0.6) { it in 0.0..1.0 }
    val folding by double(40.0) { it in 20.0..40.0 }
}