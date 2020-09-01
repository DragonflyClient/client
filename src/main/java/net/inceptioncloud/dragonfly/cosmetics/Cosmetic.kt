package net.inceptioncloud.dragonfly.cosmetics

import com.google.gson.JsonObject
import net.minecraft.client.entity.AbstractClientPlayer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.layers.LayerRenderer

open class Cosmetic(
    val cosmeticId: Int
) : LayerRenderer<AbstractClientPlayer> {

    open val models = listOf<CosmeticModel>()

    var databaseModel: JsonObject? = null

    open fun render(player: AbstractClientPlayer, partialTicks: Float, properties: CosmeticRenderProperties) {
        val cosmeticData = player.cosmetics?.find { it.cosmeticId == cosmeticId }
        if (cosmeticData?.enabled != true) return
        if (cosmeticData.minecraft != player.gameProfile.id.toString()) return

        GlStateManager.pushMatrix()
        GlStateManager.color(1.0F, 1.0F, 1.0F)

        models.forEach { it.render(player, properties, cosmeticData) }

        GlStateManager.popMatrix()
    }

    override fun doRenderLayer(
        player: AbstractClientPlayer?,
        limbSwing: Float,
        limbSwingAmount: Float,
        partialTicks: Float,
        ageInTicks: Float,
        headYaw: Float,
        headPitch: Float,
        scale: Float
    ) {
        if (player != null && player.hasPlayerInfo() && !player.isInvisible) {
            render(
                player,
                partialTicks,
                CosmeticRenderProperties(limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch, scale)
            )
        }
    }

    override fun shouldCombineTextures(): Boolean = false
}