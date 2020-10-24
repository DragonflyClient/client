package net.inceptioncloud.dragonfly.engine.scrollbar

import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.structure.*
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Rectangle

/**
 * This widget displays a scrollbar on the screen.
 *
 * While the main logic is handled by the [Scrollbar] instance, every scrollbar refers to an instance
 * of this class which is used to represent the scroll progress. A common position for this widget is
 * the right corner.
 *
 * @param scrollbar The scrollbar instance which serves this widget
 *
 * @property color The color of the inner rectangle in the scrollbar. This represents
 * [the currently visible part of the screen][screenHeight] in relation to the total [contentHeight].
 * @property backgroundColor The background color of the scrollbar
 */
class ScrollbarWidget(
    val scrollbar: Scrollbar
) : AssembledWidget<ScrollbarWidget>(), IPosition, IDimension, IColor {

    override var x: Float by property(0.0F)
    override var y: Float by property(0.0F)
    override var width: Float by property(0.0F)
    override var height: Float by property(0.0F)
    override var color: WidgetColor by property(DragonflyPalette.accentNormal)

    var backgroundColor: WidgetColor by property(DragonflyPalette.background.altered { alphaFloat = 0.2f })

    /**
     * The height of the viewport. This represents the maximum part of the content that can be visible
     * at a time.
     */
    var screenHeight: Float by property(0.0F)

    /**
     * The full height of the available content through which can be scrolled.
     */
    var contentHeight: Float by property(0.0F)

    /**
     * The current top-y position inside of the scrollable content. This property will always be greater
     * or equal to `0` and less or equal to [contentHeight] - [screenHeight].
     */
    var currentY: Float by property(0.0F)

    /**
     * Whether the scrollbar is enabled. If the [contentHeight] is less than or equal to the [screenHeight],
     * the scrollbar (and the dedicated widget) is disabled.
     */
    var isEnabled by property(false)

    /**
     * The visible part in percent. Is equal to [screenHeight] / [contentHeight]. Recalculated on every
     * structure update.
     */
    var visiblePart = 0.0f

    /**
     * The vertical position in percent. This value will never reach 100% since it is equal to [currentY] /
     * [screenHeight] and [currentY] will always be less than or equal to [contentHeight] - [screenHeight].
     */
    var verticalPositionPart = 0.0f

    /**
     * The progress of the scrollbar. Unlike [verticalPositionPart] this property will reach 100% if the
     * scrollbar is at the very bottom.
     */
    var progress = 0.0f

    override fun assemble(): Map<String, Widget<*>> = mapOf(
        "slider-background" to Rectangle(),
        "slider-foreground" to Rectangle()
    )

    override fun updateStructure() {
        if (!isEnabled) {
            "slider-background"<Rectangle> { isVisible = false }
            "slider-foreground"<Rectangle> { isVisible = false }
            return
        }

        visiblePart = screenHeight / contentHeight
        verticalPositionPart = currentY / contentHeight
        progress = currentY / (contentHeight - screenHeight)

        "slider-background"<Rectangle> {
            isVisible = true
            x = this@ScrollbarWidget.x
            y = this@ScrollbarWidget.y
            width = this@ScrollbarWidget.width
            height = this@ScrollbarWidget.height
            color = this@ScrollbarWidget.backgroundColor
        }

        "slider-foreground"<Rectangle> {
            isVisible = true
            x = this@ScrollbarWidget.x
            width = this@ScrollbarWidget.width
            y = this@ScrollbarWidget.y + this@ScrollbarWidget.height * verticalPositionPart
            height = this@ScrollbarWidget.height * visiblePart
            color = this@ScrollbarWidget.color
        }
    }

    override fun update() {
        scrollbar.update()
        super.update()
    }
}