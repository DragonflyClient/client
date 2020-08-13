package net.inceptioncloud.dragonfly.engine.internal

import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.util.ResourceLocation

/**
 * Represents an image resource that can be either static ([ResourceLocation]) or
 * dynamic ([DynamicTexture]].
 */
class ImageResource {

    var dynamicTexture: DynamicTexture? = null
    var resourceLocation: ResourceLocation? = null

    /**
     * Instantiate using a dynamic texture
     */
    constructor(dynamicTexture: DynamicTexture) {
        this.dynamicTexture = dynamicTexture
    }

    /**
     * Instantiate using a static resource
     */
    constructor(resourceLocation: ResourceLocation) {
        this.resourceLocation = resourceLocation
    }

    /**
     * Convenient constructor using a static resource
     */
    constructor(resourceLocation: String) : this(ResourceLocation(resourceLocation))
}
