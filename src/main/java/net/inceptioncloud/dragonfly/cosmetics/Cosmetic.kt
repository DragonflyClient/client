package net.inceptioncloud.dragonfly.cosmetics

import net.minecraft.client.entity.AbstractClientPlayer
import net.minecraft.client.renderer.entity.RenderPlayer
import net.minecraft.client.renderer.entity.layers.LayerRenderer

abstract class Cosmetic(val renderPlayer: RenderPlayer) : LayerRenderer<AbstractClientPlayer> {

    override fun doRenderLayer(player: AbstractClientPlayer?, limbSwing: Float, limbSwingAmount: Float, partialTicks: Float, ageInTicks: Float,
                               headYaw: Float, headPitch: Float, scale: Float) {

        if (player != null && player.hasPlayerInfo() && !player.isInvisible) {
            render(player, partialTicks, CosmeticRenderData(limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch, scale))
        }
    }

    override fun shouldCombineTextures(): Boolean = false

    abstract fun render(player: AbstractClientPlayer, partialTicks: Float, data: CosmeticRenderData)
}