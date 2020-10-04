package net.inceptioncloud.dragonfly.ui.playerlist.indicators

import net.minecraft.client.network.NetworkPlayerInfo
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.scoreboard.ScorePlayerTeam
import net.minecraft.util.ResourceLocation

class PlayingDragonflyIndicator : Indicator(ResourceLocation("dragonflyres/logos/32x.png"), "§6Dragonfly", -100) {

    override fun check(playerInfo: NetworkPlayerInfo): Boolean {
        return true
    }
}