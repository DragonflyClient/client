package net.inceptioncloud.dragonfly.mods.nvidiahighlights

import dev.decobr.mcgeforce.bindings.MCGeForceHelper
import dev.decobr.mcgeforce.utils.EnumHighlightType
import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.controls.*
import net.inceptioncloud.dragonfly.mods.core.DragonflyMod
import net.inceptioncloud.dragonfly.overlay.toast.Toast
import net.minecraft.client.Minecraft
import java.util.*

object NvidiaHighlightsMod : DragonflyMod("Nvidia Highlights") {

    val gommehd = HashMap<String, String>()
    val royalpixels = HashMap<String, String>()

    var enabled by option(false)

    var length by option(30)
    var saveKills by option(true)
    var saveDeaths by option(true)
    var saveWins by option(true)

    init {
        registerMap()
    }

    override fun publishControls(): List<ControlElement<*>> = listOf(
        TitleControl("General"),
        BooleanControl(::enabled, "Enable mod") {
            if (Dragonfly.geforceHelper.isSystemValid) {
                true
            } else {
                Toast.queue("§c You can't use this feature, take a look at the notes!", 400)
                false
            }
        },
        ButtonControl("Show Highlights", "Open the GeForce Editor.", "Open Editor") {
            Dragonfly.geforceHelper.showHighlights()
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    /*NECESSARY INFO:

                        Displaying error toast always, because error message is
                        covered by the GeForce Experience Overlay if the error doesn't occur!

                        ERROR: Occurs if overlay will not be open,
                               if no highlight is in the highlights folder of dragonfly.
                     */
                    Toast.queue("§cSeems like you don't have any saved Highlight!", 400)
                }
            }, 1000)
        },
        TitleControl("Options"),
        NumberControl(::length, "Length", "The length of a Highlight in seconds.", min = 5.0, max = 60.0),
        BooleanControl(::saveKills, "Save Kills", ""),
        BooleanControl(::saveDeaths, "Save Deaths", ""),
        BooleanControl(::saveWins, "Save Wins", ""),
        TitleControl("Supported Server, Languages, Modes"),
        TextControl("GommeHD.net: German, English"),
        TextControl("  - BedWars: Kills, Deaths, Wins"),
        TextControl("  - SkyWars: Kills, Deaths, Wins"),
        TextControl("  - Cores: Kills, Deaths"),
        TextControl("RoyalPixels: German"),
        TextControl("  - SkyWars: Kills, Deaths, Wins"),
        TitleControl("Note"),
        TextControl("- To use this feature you need to have an Nvidia Graphics Card, Nvidia GeForce Experience installed and an Windows System"),
        TextControl("- Please make sure you have allowed Highlighting for Minecraft in your Nvidia GeForce Experience Settings")
    )

    private fun registerMap() {
        gommehd["highlights.bedwars.kill.en"] = "[BedWars] You have killed"
        gommehd["highlights.bedwars.death.en"] = "[BedWars] You were killed by"
        gommehd["highlights.bedwars.win.en"] = "[BedWars] You have won"
        gommehd["highlights.bedwars.kill.de"] = "[BedWars] Du hast"
        gommehd["highlights.bedwars.death.de"] = "[BedWars] Du wurdest von"
        gommehd["highlights.bedwars.win.de"] = "[BedWars] Du hast die Runde gewonnen"

        gommehd["highlights.skywars.kill.en"] = "was killed by %playername%"
        gommehd["highlights.skywars.death.en"] = "[SkyWars] %playername% was killed by"
        gommehd["highlights.skywars.win.en"] = "[SkyWars] %playername% won SkyWars"
        gommehd["highlights.skywars.kill.de"] = "wurde von %playername% getötet"
        gommehd["highlights.skywars.death.de"] = "[SkyWars] %playername% wurde "
        gommehd["highlights.skywars.win.de"] = "[SkyWars] %playername% hat SkyWars gewonnen"

        gommehd["highlights.cores.kill.en"] = "was killed by %playername%"
        gommehd["highlights.cores.death.en"] = "[Cores] %playername% was killed by"
        gommehd["highlights.cores.kill.de"] = "wurde von %playername% getötet"
        gommehd["highlights.cores.death.de"] = "[Cores] %playername% wurde "

        royalpixels["highlights.skywars.kill.de"] = "Du hast"
        royalpixels["highlights.skywars.kill2.de"] = "getötet"
        royalpixels["highlights.skywars.death.de"] = "Du wurdest"
        royalpixels["highlights.skywars.win.de"] = "%playername% hat das Spiel gewonnen"

    }

    fun checkMessage(message: String): Boolean {
        if (!Minecraft.getMinecraft().isSingleplayer) {
            val serverIp = Minecraft.getMinecraft().currentServerData.serverIP
            val playerName = Minecraft.getMinecraft().session.username

            if (serverIp.toLowerCase().endsWith("gommehd.net")) {
                for (entry in gommehd.keys) {

                    if (message.startsWith("[BedWars]") && message.startsWith(gommehd[entry]!!)) {
                        when {
                            entry.startsWith("highlights.bedwars.kill") -> {
                                Dragonfly.geforceHelper.saveHighlight(EnumHighlightType.KILL)
                                return true
                            }
                            entry.startsWith("highlights.bedwars.death") -> {
                                Dragonfly.geforceHelper.saveHighlight(EnumHighlightType.DEATH)
                                return true
                            }
                            entry.startsWith("highlights.bedwars.win") -> {
                                Dragonfly.geforceHelper.saveHighlight(EnumHighlightType.WIN)
                                return true
                            }
                        }
                    } else if (message.startsWith("[SkyWars]") && message.contains(
                            gommehd[entry]!!.replace(
                                "%playername%",
                                playerName
                            )
                        )
                    ) {

                        when {
                            entry.startsWith("highlights.skywars.kill") -> {
                                Dragonfly.geforceHelper.saveHighlight(EnumHighlightType.KILL)
                                return true
                            }
                            entry.startsWith("highlights.skywars.death") -> {
                                Dragonfly.geforceHelper.saveHighlight(EnumHighlightType.DEATH)
                                return true
                            }
                            entry.startsWith("highlights.skywars.win") -> {
                                Dragonfly.geforceHelper.saveHighlight(EnumHighlightType.WIN)
                                return true
                            }
                        }

                    } else if (message.startsWith("[Cores]") && message.contains(
                            gommehd[entry]!!.replace(
                                "%playername%",
                                playerName
                            )
                        )
                    ) {

                        when {
                            entry.startsWith("highlights.cores.kill") -> {
                                Dragonfly.geforceHelper.saveHighlight(EnumHighlightType.KILL)
                                return true
                            }
                            entry.startsWith("highlights.cores.death") -> {
                                Dragonfly.geforceHelper.saveHighlight(EnumHighlightType.DEATH)
                                return true
                            }
                        }

                    }
                }
            } else if (serverIp.toLowerCase().endsWith("royalpixels.de")) {
                for (entry in royalpixels.keys) {

                    if (message.startsWith("SkyWars") && message.contains(
                            royalpixels[entry]!!.replace(
                                "%playername%",
                                playerName
                            )
                        )
                    ) {

                        if (message.contains(royalpixels["highlights.skywars.kill.de"]!!) && message.contains(royalpixels["highlights.skywars.kill2.de"]!!)) {
                            Dragonfly.geforceHelper.saveHighlight(EnumHighlightType.KILL)
                            return true
                        }

                        when {
                            entry.startsWith("highlights.skywars.death") -> {
                                Dragonfly.geforceHelper.saveHighlight(EnumHighlightType.DEATH)
                                return true
                            }
                            entry.startsWith("highlights.skywars.win") -> {
                                Dragonfly.geforceHelper.saveHighlight(EnumHighlightType.WIN)
                                return true
                            }
                        }
                    }

                }
            }
        }
        return false
    }

}