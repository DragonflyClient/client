package net.inceptioncloud.dragonfly.cosmetics.types.capes

import net.minecraft.util.ResourceLocation

class AnimatedCape(
    val delay: Long,
    val frames: Array<ResourceLocation?>
) {

    fun getCurrentFrame(): ResourceLocation? {
        val index = ((System.currentTimeMillis() / (1000 / delay)) % frames.size).toInt()
        return frames.getOrNull(index)
    }
}