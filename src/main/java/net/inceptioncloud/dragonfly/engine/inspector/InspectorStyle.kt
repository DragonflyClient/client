package net.inceptioncloud.dragonfly.engine.inspector

import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import tornadofx.*

class InspectorStyle : Stylesheet() {
    init {
        root {
            fontSize = 16.px
        }

        form {
            fieldset {
                label {
                    fontSize = 24.px
                    fontWeight = FontWeight.EXTRA_BOLD
                }
                fieldset {
                    label {
                        fontSize = 20.px
                        fontWeight = FontWeight.BOLD
                    }
                }
                field {
                    padding = box(2.px, 0.px)
                    label {
                        fontSize = 16.px
                        fontWeight = FontWeight.NORMAL
                    }
                }
            }
        }
    }
}