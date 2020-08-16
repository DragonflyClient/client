package net.inceptioncloud.dragonfly.engine.inspector.extension

import javafx.scene.text.FontWeight
import tornadofx.*

/**
 * The default stylesheet for the [InspectorApp].
 *
 * This stylesheet only changes the appearance of the form that displays
 * information about the widget.
 */
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