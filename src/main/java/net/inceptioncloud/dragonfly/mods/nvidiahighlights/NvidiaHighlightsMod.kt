package net.inceptioncloud.dragonfly.mods.nvidiahighlights

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.apps.modmanager.controls.*
import net.inceptioncloud.dragonfly.mods.core.DragonflyMod
import net.inceptioncloud.dragonfly.overlay.toast.Toast
import java.util.*

object NvidiaHighlightsMod : DragonflyMod("Nvidia Highlights") {

    var enabled by option(true)
    var length by option(30)

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
            },1000)
        }
    )

}