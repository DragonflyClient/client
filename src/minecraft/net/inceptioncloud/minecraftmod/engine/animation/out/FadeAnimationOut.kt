package net.inceptioncloud.minecraftmod.engine.animation.out

import net.inceptioncloud.minecraftmod.engine.animation.`in`.FadeAnimationIn

class FadeAnimationOut(smooth: Boolean) : FadeAnimationIn(smooth)
{
    override val startValue: Double
        get() = 1.0

    override val endValue: Double
        get() = 0.0

    override fun finish()
    {
        parent.visible = false
        super.finish()
    }
}