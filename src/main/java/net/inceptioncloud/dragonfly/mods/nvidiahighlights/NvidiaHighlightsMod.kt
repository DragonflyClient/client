package net.inceptioncloud.dragonfly.mods.nvidiahighlights

import dev.decobr.mcgeforce.utils.EnumHighlightType
import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.apps.modmanager.controls.*
import net.inceptioncloud.dragonfly.mods.core.DragonflyMod
import net.inceptioncloud.dragonfly.overlay.toast.Toast
import net.minecraft.client.Minecraft
import java.util.*

object NvidiaHighlightsMod : DragonflyMod("Nvidia Highlights") {

    val gommehd = HashMap<String, String>()

    var enabled by option(true)
    var length by option(30)

    init {
        registerMap()
    }

    override fun publishControls(): List<ControlElement<*>> = listOf(
        TitleControl("General"),
        BooleanControl(NvidiaHighlightsMod::enabled, "Enable mod"),
        NumberControl(::length, "Length", "The length of a Highlight in seconds.", min = 5.0, max = 60.0),
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
        }
    )

    private fun registerMap() {
        gommehd["highlights.bedwars.kill.en"] = "[BedWars] You have killed"
        gommehd["highlights.bedwars.death.en"] = "[BedWars] You were killed by"
        gommehd["highlights.bedwars.win.en"] = "[BedWars] You have won"
        gommehd["highlights.bedwars.kill.de"] = "[BedWars] Du hast"
        gommehd["highlights.bedwars.death.de"] = "[BedWars] Du wurdest von"
        gommehd["highlights.bedwars.win.de"] = "[BedWars] Du hast die Runde gewonnen"
    }

    fun checkMessage(message: String): Boolean {
        if (!Minecraft.getMinecraft().isSingleplayer) {
            val serverIp = Minecraft.getMinecraft().currentServerData.serverIP
            val playerName = Minecraft.getMinecraft().session.username

            if(serverIp.toLowerCase().endsWith("gommehd.net")) {
                for(entry in gommehd.keys) {
                    if(message.startsWith(gommehd[entry]!!)) {

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

                    }
                }
            }
        }
        return false
    }

}