package net.inceptioncloud.dragonfly.ui.playerlist.indicators

import net.minecraft.client.network.NetworkPlayerInfo
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.scoreboard.ScorePlayerTeam

class SamePartyIndicator : Indicator(ItemStack(Items.cake), "§5Party", 0) {
    /**
     * The function to be overwritten that checks if the indicator should be assigned to a certain player.
     */
    override fun check(playerInfo: NetworkPlayerInfo): Boolean = check(
        if (playerInfo.displayName != null) playerInfo.displayName.formattedText else ScorePlayerTeam.formatPlayerName(playerInfo.playerTeam,
            playerInfo.gameProfile.name))

    /**
     * Customized [check] method.
     *
     * TODO: Optimize this method to work with other servers too!
     */
    fun check(displayName: String): Boolean = displayName.endsWith(" [§5Party§7]")
}