package net.inceptioncloud.dragonfly.cosmetics

import net.inceptioncloud.dragonfly.cosmetics.logic.CosmeticData
import net.inceptioncloud.dragonfly.mc
import net.minecraft.client.model.ModelBase
import net.minecraft.client.renderer.entity.RenderPlayer
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer

/**
 * Represents a model that can be rendered as part of a cosmetic item.
 */
abstract class CosmeticModel : ModelBase() {

    /**
     * Lazily-initialized [RenderPlayer] object that renders players.
     */
    protected val playerRenderer: RenderPlayer by lazy { mc.renderManager.playerRenderer }

    /**
     * Render the cosmetic on the [player]. The [cosmeticData] is fetched by the [Cosmetic] class.
     * It also makes sure that this model is only rendered if the cosmetic is available to the player.
     */
    abstract fun render(player: EntityPlayer, properties: CosmeticRenderProperties, cosmeticData: CosmeticData)

    override fun setLivingAnimations(entity: EntityLivingBase?, a: Float, b: Float, partialTicks: Float) {}

    override fun render(entity: Entity?, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float, headYaw: Float, headPitch: Float,
                              scale: Float) {}
}