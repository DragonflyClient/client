package net.inceptioncloud.dragonfly.engine.inspector

import javafx.scene.image.Image
import javafx.stage.Stage
import org.apache.logging.log4j.LogManager
import tornadofx.*

class InspectorApp : App(InspectorView::class, InspectorStyle::class) {

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