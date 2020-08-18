package net.inceptioncloud.dragonfly.cosmetics.types

import net.inceptioncloud.dragonfly.cosmetics.*
import net.minecraft.client.entity.AbstractClientPlayer
import net.minecraft.client.model.ModelRenderer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.RenderPlayer
import net.minecraft.entity.Entity
import net.minecraft.util.ResourceLocation

class CosmeticTopHat(renderPlayer: RenderPlayer) : Cosmetic(renderPlayer) {

    private val resource = ResourceLocation("dragonflyres/hat.png")

    private val modelTopHat = ModelTopHat(renderPlayer)

    override fun render(player: AbstractClientPlayer, partialTicks: Float, data: CosmeticRenderData) {

        GlStateManager.pushMatrix()

        renderPlayer.bindTexture(resource)
        if (player.isSneaking)
            GlStateManager.translate(0.0, 0.225, 0.0)

        GlStateManager.color(0.5F, 0.0F, 1.0F)
        modelTopHat.render(player, data)

        GlStateManager.popMatrix()

    }

    private class ModelTopHat(renderPlayer: RenderPlayer) : CosmeticModel(renderPlayer) {

        private val rim = ModelRenderer(playerModel, 0, 0).apply {
            addBox(-5.5F, -9F, -5.5F, 11, 2, 11)
        }

        private val pointy = ModelRenderer(playerModel, 0, 13).apply {
            addBox(-3.5F, -17F, -3.5F, 7, 8, 7)
        }

        override fun render(entity: Entity, data: CosmeticRenderData) {
            with(rim) {
                rotateAngleX = playerModel.bipedHead.rotateAngleX
                rotateAngleY = playerModel.bipedHead.rotateAngleY
                rotationPointX = 0.0F
                rotationPointY = 0.0F
                render(data.scale)
            }

            with(pointy) {
                rotateAngleX = playerModel.bipedHead.rotateAngleX
                rotateAngleY = playerModel.bipedHead.rotateAngleY
                rotationPointX = 0.0F
                rotationPointY = 0.0F
                render(data.scale)
            }
        }
    }
}