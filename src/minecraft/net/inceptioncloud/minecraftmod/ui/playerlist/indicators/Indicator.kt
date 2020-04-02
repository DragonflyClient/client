package net.inceptioncloud.minecraftmod.ui.playerlist.indicators

import net.minecraft.client.Minecraft
import net.minecraft.client.network.NetworkPlayerInfo
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.scoreboard.ScorePlayerTeam

/**
 * The list of all available indicators.
 */
val availableIndicators: List<Indicator> = listOf(ThePlayerIndicator(), SamePartyIndicator())

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
abstract class Indicator(val itemStack: ItemStack, val text: String, val xIndex: Int)
{
    /**
     * The function to be overwritten that checks if the indicator should be assigned to a certain player.
     */
    abstract fun check(playerInfo: NetworkPlayerInfo): Boolean

    /**
     * Draws the indicator into the current GUI at the given coordinates.
     */
    fun draw(x: Int, y: Int)
    {
        val factor = 0.6
        val defactor = 1 / factor
        GlStateManager.scale(factor, factor, factor)
        Minecraft.getMinecraft().renderItem.renderItemIntoGUI(itemStack, (x * defactor).toInt(), (y * defactor).toInt())
        GlStateManager.scale(defactor, defactor, defactor)
    }
}

class SamePartyIndicator : Indicator(ItemStack(Items.cake), "§5Party", 0)
{
    /**
     * The function to be overwritten that checks if the indicator should be assigned to a certain player.
     */
    override fun check(playerInfo: NetworkPlayerInfo): Boolean = check(if (playerInfo.displayName != null) playerInfo.displayName.formattedText else ScorePlayerTeam.formatPlayerName(playerInfo.playerTeam, playerInfo.gameProfile.name))

    /**
     * Customized [check] method.
     *
     * TODO: Optimize this method to work with other servers too!
     */
    fun check(displayName: String): Boolean = displayName.endsWith(" [§5Party§7]")
}

class ThePlayerIndicator : Indicator(ItemStack(Items.golden_apple), "§6Client player", 10)
{
    /**
     * The function to be overwritten that checks if the indicator should be assigned to a certain player.
     */
    override fun check(playerInfo: NetworkPlayerInfo): Boolean = playerInfo.gameProfile.name == Minecraft.getMinecraft().thePlayer.gameProfile.name
}