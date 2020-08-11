package net.inceptioncloud.dragonfly.engine.inspector

import javafx.application.Platform
import javafx.scene.input.*
import javafx.scene.text.Font
import net.inceptioncloud.dragonfly.engine.internal.WidgetStage
import org.apache.logging.log4j.LogManager
import tornadofx.*
import java.io.File
import kotlin.concurrent.thread

object Inspector {

    lateinit var inspectorView: InspectorView

    var isLaunched = false

    fun launch() = thread {
        if (isLaunched)
            return@thread

        isLaunched = true

        FX.layoutDebuggerShortcut = KeyCodeCombination.valueOf("Ctrl+Shift+I") as KeyCodeCombination?

        System.setProperty("prism.lcdtext", "false")
        System.setProperty("prism.text", "t2k")

        Font.loadFont(File("dragonfly\\assets\\fonts\\SF Pro Display Medium.ttf").inputStream(), 15.0)
        Font.loadFont(File("dragonfly\\assets\\fonts\\SF Pro Display.ttf").inputStream(), 15.0)

        launch<InspectorApp>()
    }

    @JvmStatic
    fun stageUpdated() {
        if (isLaunched) {
            Platform.runLater {
                inspectorView.repopulate()
            }
        }
    }
}