package net.inceptioncloud.dragonfly.cosmetics

import com.google.gson.JsonObject
import net.minecraft.client.entity.AbstractClientPlayer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.layers.LayerRenderer
import net.minecraft.entity.player.EntityPlayer

/**
 * Represents a cosmetic item.
 *
 * This class is the superclass for all other cosmetic items. It handles adding
 * [models] to the cosmetics, loading the [databaseModel] from the servers, checking
 * whether a player owns this cosmetic item and much more background logic.
 *
 * @param cosmeticId The unique id to identify the cosmetic item.
 */
open class Cosmetic(
    val cosmeticId: Int
) : LayerRenderer<AbstractClientPlayer> {

    /**
     * The models that are rendered as part of the cosmetic.
     */
    open val models = listOf<CosmeticModel>()

    /**
     * The database model for the cosmetic item that holds several information like
     * type, name and much more about it. Note that this [JsonObject] is loaded lazily
     * and may be null shortly after the client launch.
     */
    var databaseModel: JsonObject? = null

    /**
     * Renders the cosmetic for the given [player].
     *
     * By default, this function handles retrieving the [cosmetics][EntityPlayer.cosmetics],
     * checking whether the player owns this cosmetic item and whether it is enabled. It pushes
     * a new matrix and resets the color before rendering all [models]. This function can be
     * overwritten to change this behavior.
     */
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