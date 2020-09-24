package net.inceptioncloud.dragonfly.engine.tooltip

enum class TooltipPosition {
    ABOVE {
        override fun TooltipWidget.arrowPoint1() = x - arrowSize to containerY + containerHeight
        override fun TooltipWidget.arrowPoint2() = x + arrowSize to containerY + containerHeight
        override fun TooltipWidget.arrowPoint3() = x to containerY + containerHeight + arrowSize
    },
    BELOW {
        override fun TooltipWidget.arrowPoint1() = x - arrowSize to containerY
        override fun TooltipWidget.arrowPoint2() = x + arrowSize to containerY
        override fun TooltipWidget.arrowPoint3() = x to containerY - arrowSize
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