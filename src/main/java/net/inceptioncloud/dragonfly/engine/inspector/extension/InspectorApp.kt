package net.inceptioncloud.dragonfly.engine.inspector.extension

import javafx.scene.image.Image
import javafx.stage.Stage
import org.apache.logging.log4j.LogManager
import tornadofx.*

/**
 * The main TornadoFx app that represents the inspector.
 *
 * The app specifies the [InspectorView] as the main view and the [InspectorStyle]
 * as a stylesheet by calling the [App] constructor.
 *
 * This class is only referenced by [Inspector.launch] and does nothing more
 * than initializing the stage and setting the [Inspector.stage] property in the
 * [start] function.
 */
class InspectorApp : App(
    InspectorView::class, InspectorStyle::class) {

    override fun start(stage: Stage) {
        Inspector.stage = stage
        super.start(stage)

        with(stage) {
            width = 1400.0
            height = 800.0
            isResizable = true
            icons += Image("./icon_inspector.png")

            setOnCloseRequest {
                close()
                LogManager.getLogger().info("Inspector closed!")
            }
        }
    }
}