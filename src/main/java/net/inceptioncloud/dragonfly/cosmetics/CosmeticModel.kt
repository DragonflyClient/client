package net.inceptioncloud.dragonfly.cosmetics

import net.minecraft.client.model.ModelBase
import net.minecraft.client.model.ModelBiped
import net.minecraft.client.renderer.entity.RenderPlayer
import net.minecraft.entity.Entity

abstract class CosmeticModel(val renderPlayer: RenderPlayer) : ModelBase() {

    val playerModel: ModelBiped = renderPlayer.mainModel

    override fun render(entity: Entity?, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float, headYaw: Float, headPitch: Float,
                        scale: Float) {
        render(entity ?: return, CosmeticRenderData(limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch, scale))
    }

    abstract fun render(entity: Entity, data: CosmeticRenderData)
}