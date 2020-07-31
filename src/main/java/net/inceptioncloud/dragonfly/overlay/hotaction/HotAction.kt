package net.inceptioncloud.dragonfly.overlay.hotaction

import net.inceptioncloud.dragonfly.overlay.ScreenOverlay

object HotAction {
    @JvmStatic
    fun test() {
        ScreenOverlay.buffer.clear()
        ScreenOverlay.addComponent("hot-action", HotActionWidget(
            "Lorem ipsum dolor sit amet, co",
            "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut l",
            1000,
            listOf(
                Action("Save") { println("Save") },
                Action("Copy") { println("Copy") },
                Action("Open") { println("Open") },
                Action("Upload") { println("Upload") }
            )
        ))
    }
}