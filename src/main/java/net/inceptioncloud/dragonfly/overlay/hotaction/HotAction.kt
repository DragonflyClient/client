package net.inceptioncloud.dragonfly.overlay.hotaction

import net.inceptioncloud.dragonfly.overlay.ScreenOverlay

object HotAction {
    @JvmStatic
    fun test() {
        ScreenOverlay.buffer.clear()
        ScreenOverlay.addComponent("hot-action", HotActionWidget(
            "Screenshot",
            "A screenshot has been created! Do you wish to take further actions?",
            listOf(
                Action("Save") { println(it) }
            )
        ))
    }
}