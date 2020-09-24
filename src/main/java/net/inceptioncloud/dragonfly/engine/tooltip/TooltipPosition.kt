package net.inceptioncloud.dragonfly.engine.tooltip

import net.inceptioncloud.dragonfly.engine.widgets.assembled.RoundedRectangle

enum class TooltipPosition {
    ABOVE {
        private val RoundedRectangle.endY get() = y + height

        override fun TooltipWidget.arrowPoint1() = x - arrowSize to background!!.endY
        override fun TooltipWidget.arrowPoint2() = x + arrowSize to background!!.endY
        override fun TooltipWidget.arrowPoint3() = x to background!!.endY + arrowSize
    },
    BELOW {
        override fun TooltipWidget.arrowPoint1() = x - arrowSize to background!!.y
        override fun TooltipWidget.arrowPoint2() = x + arrowSize to background!!.y
        override fun TooltipWidget.arrowPoint3() = x to background!!.y - arrowSize
    };

    open fun TooltipWidget.arrowPoint1(): Pair<Double, Double> {
        error("Not implemented")
    }

    open fun TooltipWidget.arrowPoint2(): Pair<Double, Double> {
        error("Not implemented")
    }

    open fun TooltipWidget.arrowPoint3(): Pair<Double, Double> {
        error("Not implemented")
    }
}