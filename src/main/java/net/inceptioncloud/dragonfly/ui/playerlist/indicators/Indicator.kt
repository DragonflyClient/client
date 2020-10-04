package net.inceptioncloud.dragonfly.ui.playerlist.indicators

import net.inceptioncloud.dragonfly.mc
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.network.NetworkPlayerInfo
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation

/**
 * The list of all available indicators.
 */
val availableIndicators: List<Indicator> = listOf(PlayingDragonflyIndicator(), SamePartyIndicator())

/**
 * Finds an indicator instance by the corresponding class.
 */
@Suppress("UNCHECKED_CAST")
fun <T : Indicator?> find(clazz: Class<T>): T? = availableIndicators.firstOrNull { it.javaClass == clazz } as T

/**
 * Calculates the maximum amount of indicators that the player list has to show for one player.
 */
fun calcMaxIndicatorCount(list: List<NetworkPlayerInfo>): Int = list.maxBy { it.activeIndicators?.size ?: 0 }?.activeIndicators?.size ?: 0

/**
 * The class that represents an instance of a player list indicator.
 *
 * This is an item that is displayed next to the player list entry to show that a
 * certain condition is filled.
 */
abstract class Indicator(val itemStack: ItemStack?, val resourceLocation: ResourceLocation?, val text: String, val xIndex: Int) {

    constructor(resourceLocation: ResourceLocation, text: String, xIndex: Int) :
            this(null, resourceLocation, text, xIndex)

    constructor(itemStack: ItemStack?, text: String, xIndex: Int) :
            this(itemStack, null, text, xIndex)

    /**
     * The function to be overwritten that checks if the indicator should be assigned to a certain player.
     */
    abstract fun check(playerInfo: NetworkPlayerInfo): Boolean

    /**
     * Draws the indicator into the current GUI at the given coordinates.
     */
    fun draw(x: Int, y: Int) {
        val factor = if (itemStack != null) 0.5 else 0.25
        val refactor = 1 / factor
        GlStateManager.scale(factor, factor, factor)

        if (itemStack != null) {
            Minecraft.getMinecraft().renderItem.renderItemIntoGUI(itemStack, (x * refactor).toInt(), (y * refactor).toInt())
        } else if (resourceLocation != null) {
            GlStateManager.color(1F, 1F, 1F, 1F)
            mc.textureManager.bindTexture(resourceLocation)
            Gui.drawModalRectWithCustomSizedTexture((x * refactor).toInt(), (y * refactor).toInt(), 0F, 0F, 32, 32, 32F, 32F)
        }

        GlStateManager.scale(refactor, refactor, refactor)
    }
}