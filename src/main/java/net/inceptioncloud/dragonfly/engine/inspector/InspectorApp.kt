package net.inceptioncloud.dragonfly.engine.inspector

import javafx.scene.image.Image
import javafx.stage.Stage
import tornadofx.*

class InspectorApp : App(InspectorView::class, InspectorStyle::class) {

    override fun start(stage: Stage) {
        super.start(stage)
        stage.width = 1400.0
        stage.height = 800.0
        stage.isResizable = true
        stage.icons += Image("./icon_inspector.png")
    }
}