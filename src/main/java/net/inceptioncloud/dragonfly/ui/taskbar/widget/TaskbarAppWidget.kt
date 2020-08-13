package net.inceptioncloud.dragonfly.ui.taskbar.widget

import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.internal.AssembledWidget
import net.inceptioncloud.dragonfly.engine.internal.Widget
import net.inceptioncloud.dragonfly.engine.structure.IDimension
import net.inceptioncloud.dragonfly.engine.structure.IPosition
import net.inceptioncloud.dragonfly.engine.widgets.primitive.FilledCircle
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Image
import net.inceptioncloud.dragonfly.ui.taskbar.TaskbarApp

class TaskbarAppWidget(
    val app: TaskbarApp,
    initializerBlock: (TaskbarAppWidget.() -> Unit)? = null
) : AssembledWidget<TaskbarAppWidget>(initializerBlock), IPosition, IDimension {

    override var x: Double by property(0.0)
    override var y: Double by property(0.0)
    override var width: Double by property(200.0)
    override var height: Double by property(20.0)

    override fun assemble(): Map<String, Widget<*>> = mapOf(
        "background" to FilledCircle(),
        "icon" to Image()
    )

    override fun updateStructure() {
        "background"<FilledCircle> {
            x = this@TaskbarAppWidget.x
            y = this@TaskbarAppWidget.y
            size = this@TaskbarAppWidget.width
            color = DragonflyPalette.background
        }

        "icon"<Image> {
            x = this@TaskbarAppWidget.x + 3.0
            y = this@TaskbarAppWidget.y + 3.0
            width = this@TaskbarAppWidget.width - 6.0
            height = this@TaskbarAppWidget.height - 6.0
            resourceLocation = app.resourceLocation
        }
    }
}