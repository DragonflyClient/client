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
    },
    LEFT {
        private val RoundedRectangle.midY get() = y + height / 2.0
        private val RoundedRectangle.endX get() = x + width

        override fun TooltipWidget.arrowPoint1() = background!!.endX to background!!.midY - arrowSize
        override fun TooltipWidget.arrowPoint2() = background!!.endX to background!!.midY + arrowSize
        override fun TooltipWidget.arrowPoint3() = background!!.endX + arrowSize to background!!.midY
    },
    RIGHT {
        private val RoundedRectangle.midY get() = y + height / 2.0

        override fun TooltipWidget.arrowPoint1() = background!!.x to background!!.midY - arrowSize
        override fun TooltipWidget.arrowPoint2() = background!!.x to background!!.midY + arrowSize
        override fun TooltipWidget.arrowPoint3() = background!!.x - arrowSize to background!!.midY
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