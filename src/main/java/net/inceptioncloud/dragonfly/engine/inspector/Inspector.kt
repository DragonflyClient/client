package net.inceptioncloud.dragonfly.engine.inspector

import javafx.scene.input.*
import javafx.scene.text.Font
import tornadofx.*
import java.io.File
import kotlin.concurrent.thread

object Inspector {

    fun launch() = thread {
        FX.layoutDebuggerShortcut = KeyCodeCombination.valueOf("Ctrl+Shift+I") as KeyCodeCombination?

        System.setProperty("prism.lcdtext", "false")
        System.setProperty("prism.text", "t2k")

        Font.loadFont(File("dragonfly\\assets\\fonts\\SF Pro Display Medium.ttf").inputStream(), 15.0)
        Font.loadFont(File("dragonfly\\assets\\fonts\\SF Pro Display.ttf").inputStream(), 15.0)

        launch<InspectorApp>()
    }
}