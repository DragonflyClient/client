package net.inceptioncloud.dragonfly.engine.inspector

import javafx.stage.Stage
import tornadofx.*

class InspectorApp : App(InspectorView::class, InspectorStyle::class) {

    override fun start(stage: Stage) {
        super.start(stage)
        stage.width = 1000.0
        stage.height = 600.0
        stage.isResizable = true
    }
}