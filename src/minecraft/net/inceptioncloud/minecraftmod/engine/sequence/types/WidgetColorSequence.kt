package net.inceptioncloud.minecraftmod.engine.sequence.types

import net.inceptioncloud.minecraftmod.engine.internal.WidgetColor
import net.inceptioncloud.minecraftmod.engine.sequence.Sequence

class WidgetColorSequence(from: WidgetColor, to: WidgetColor, duration: Int) :
    Sequence<WidgetColor>(from, to, duration) {

    override fun interpolate(progress: Double) = WidgetColor(
        (to.redDouble - from.redDouble) * progress + from.redDouble,
        (to.greenDouble - from.greenDouble) * progress + from.greenDouble,
        (to.blueDouble - from.blueDouble) * progress + from.blueDouble,
        (to.alphaDouble - from.alphaDouble) * progress + from.alphaDouble
    )
}