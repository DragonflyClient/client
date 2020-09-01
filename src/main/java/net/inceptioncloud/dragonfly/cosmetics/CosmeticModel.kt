package net.inceptioncloud.dragonfly.cosmetics

import net.inceptioncloud.dragonfly.mc
import net.minecraft.client.model.ModelBase
import net.minecraft.client.model.ModelPlayer
import net.minecraft.client.renderer.entity.RenderPlayer
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer

abstract class CosmeticModel : ModelBase() {

    protected var partialTicks: Float = 0F
    protected val playerRenderer: RenderPlayer = mc.renderManager.playerRenderer
    protected val playerModel: ModelPlayer = playerRenderer.mainModel

    abstract fun render(player: EntityPlayer, properties: CosmeticRenderProperties)

    open fun setLivingAnimations(player: EntityPlayer, a: Float, b: Float, partialTicks: Float) {}

    final override fun render(
        entity: Entity?,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        headYaw: Float,
        headPitch: Float,
        scale: Float
    ) {
        render(
            entity as? EntityPlayer ?: return,
            CosmeticRenderProperties(limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch, scale)
        )
    }

    final override fun setLivingAnimations(entity: EntityLivingBase?, a: Float, b: Float, partialTickTime: Float) {
        partialTicks = partialTickTime
        setLivingAnimations(entity as? EntityPlayer ?: return, a, b, partialTickTime)
    }
}