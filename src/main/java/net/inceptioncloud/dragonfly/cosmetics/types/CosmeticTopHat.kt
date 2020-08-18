package net.inceptioncloud.dragonfly.cosmetics.types

import net.inceptioncloud.dragonfly.cosmetics.*
import net.minecraft.client.entity.AbstractClientPlayer
import net.minecraft.client.model.ModelRenderer
import net.minecraft.client.renderer.entity.RenderPlayer
import net.minecraft.entity.Entity
import net.minecraft.util.ResourceLocation

val resource = ResourceLocation("hat.png")

class CosmeticTopHat : Cosmetic() {

    override fun render(player: AbstractClientPlayer, partialTicks: Float, data: CosmeticRenderData) {
        TODO("Not yet implemented")
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