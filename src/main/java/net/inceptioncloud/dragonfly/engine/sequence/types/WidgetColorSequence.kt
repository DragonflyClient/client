package net.inceptioncloud.dragonfly.engine.sequence.types

import net.inceptioncloud.dragonfly.engine.internal.WidgetColor
import net.inceptioncloud.dragonfly.engine.sequence.Sequence

class WidgetColorSequence(from: WidgetColor, to: WidgetColor, duration: Int) :
    Sequence<WidgetColor>(from, to, duration) {

    override fun interpolate(progress: Double) = when {
        to.rainbow -> to
        else -> WidgetColor(
            (to.redDouble - from.redDouble) * progress + from.redDouble,
            (to.greenDouble - from.greenDouble) * progress + from.greenDouble,
            (to.blueDouble - from.blueDouble) * progress + from.blueDouble,
            (to.alphaDouble - from.alphaDouble) * progress + from.alphaDouble
        )
    }
}
