package net.inceptioncloud.dragonfly.engine.sequence.types

import net.inceptioncloud.dragonfly.engine.internal.WidgetColor
import net.inceptioncloud.dragonfly.engine.sequence.Sequence

class WidgetColorSequence(from: WidgetColor, to: WidgetColor, duration: Long) :
    Sequence<WidgetColor>(from, to, duration) {

    override fun interpolate(progress: Double) = when {
        to.rainbow -> to
        else -> WidgetColor(
            (to.redFloat - from.redFloat) * progress + from.redFloat,
            (to.greenFloat - from.greenFloat) * progress + from.greenFloat,
            (to.blueFloat - from.blueFloat) * progress + from.blueFloat,
            (to.alphaFloat - from.alphaFloat) * progress + from.alphaFloat
        )
    }
}
