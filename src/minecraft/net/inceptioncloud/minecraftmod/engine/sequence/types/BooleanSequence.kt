package net.inceptioncloud.minecraftmod.engine.sequence.types

import net.inceptioncloud.minecraftmod.engine.sequence.Sequence

class BooleanSequence(from: Boolean, to: Boolean, duration: Int) : Sequence<Boolean>(from, to, duration) {

    override fun interpolate(progress: Double) = if (progress >= 0.5) to else from
}