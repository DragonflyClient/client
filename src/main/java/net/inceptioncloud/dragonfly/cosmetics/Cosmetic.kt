package net.inceptioncloud.dragonfly.cosmetics

import com.google.gson.JsonObject
import net.inceptioncloud.dragonfly.controls.ControlElement
import net.inceptioncloud.dragonfly.cosmetics.logic.*
import net.inceptioncloud.dragonfly.options.OptionKey
import net.inceptioncloud.dragonfly.options.PseudoOptionKey
import net.inceptioncloud.dragonfly.utils.Either
import net.minecraft.client.entity.AbstractClientPlayer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.layers.LayerRenderer
import net.minecraft.entity.player.EntityPlayer
import kotlin.reflect.*
import kotlin.reflect.jvm.isAccessible

/**
 * Represents a cosmetic item.
 *
 * This class is the superclass for all other cosmetic items. It handles adding
 * [models] to the cosmetics, loading the [databaseModel] from the servers, checking
 * whether a player owns this cosmetic item and much more background logic.
 *
 * @param cosmeticId The unique id to identify the cosmetic item.
 */
abstract class Cosmetic<ConfigType : CosmeticConfig>(
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
     * The class reference to the [ConfigType].
     */
    abstract val configClass: KClass<ConfigType>

    /**
     * Function to be implemented for generating controls to customize the cosmetic item.
     */
    abstract fun generateControls(config: ConfigType): Collection<ControlElement<*>>

    /**
     * Convenience function for generating controls with the given [cosmeticData].
     */
    fun generateControls(cosmeticData: CosmeticData): Collection<ControlElement<*>>
            = generateControls(parseConfig(cosmeticData))

    /**
     * Parses the given [data] as the [ConfigType].
     */
    fun parseConfig(data: CosmeticData): ConfigType {
        return data.parseConfigClass(configClass)
    }

    /**
     * Renders the cosmetic for the given [player].
     *
     * By default, this function handles retrieving the [cosmetics][EntityPlayer.cosmetics],
     * checking whether the player owns this cosmetic item and whether it is enabled. It pushes
     * a new matrix and resets the color before rendering all [models]. This function can be
     * overwritten to change this behavior.
     */
    open fun render(player: AbstractClientPlayer, properties: CosmeticRenderProperties) {
        val cosmeticData = player.cosmetics?.find { it.cosmeticId == cosmeticId }
        if (cosmeticData?.enabled != true) return
        if (cosmeticData.minecraft != player.gameProfile.id.toString()) return

        GlStateManager.pushMatrix()
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F)

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
                player, CosmeticRenderProperties(limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch, scale, partialTicks)
            )
        }
    }

    override fun shouldCombineTextures(): Boolean = false

    inline fun <reified T> KMutableProperty0<T>.pseudo(): Either<KMutableProperty0<out T>, OptionKey<T>> {
        isAccessible = true
        val delegate = getDelegate() as ConfigProperty<*>
        val default = delegate.defaultValue as T

        return Either(
            b = PseudoOptionKey.new<T>()
                .set(setter)
                .get(getter)
                .defaultValue { default }
                .build()
        )
    }
}