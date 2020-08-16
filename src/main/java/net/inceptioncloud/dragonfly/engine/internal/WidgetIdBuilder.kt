package net.inceptioncloud.dragonfly.engine.internal

/**
 * A builder for easily creating pairs of a widget and an id.
 *
 * It is initialized when calling the unary plus function on a string with the
 * widget set. After that, you can specify the id by using the infix function `id`.
 * When it is called, the pair will automatically be built and added to the stage.
 *
 * @property onBuild a lambda that is executed when the builder is being built
 * @property widget the widget that is mapped to the id
 */
class WidgetIdBuilder<W : Widget<W>>(val widget: W, val onBuild: (String, W) -> Unit) {

    /**
     * A convenient constructor for directly adding the widget to a [WidgetStage].
     */
    constructor(stage: WidgetStage, widget: W) : this(widget, { id, _ ->
        stage.add(id to widget)
    })

    /**
     * The id to identify the widget.
     */
    var id: String? = null

    /**
     * Provide an id for the widget.
     */
    infix fun id(id: String): W {
        this.id = id
        build()
        return widget
    }

    /**
     * Build the pair.
     */
    private fun build() {
        widget.isInStateUpdate = true

        try {
            widget.initializerBlock?.invoke(widget)
        } finally {
            widget.isInStateUpdate = false
        }

        onBuild(id!!, widget)
    }
}