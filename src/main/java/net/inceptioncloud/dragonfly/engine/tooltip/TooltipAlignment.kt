package net.inceptioncloud.dragonfly.engine.tooltip

enum class TooltipAlignment {
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

    abstract fun TooltipWidget.arrowPoint1(): Pair<Float, Float>
    abstract fun TooltipWidget.arrowPoint2(): Pair<Float, Float>
    abstract fun TooltipWidget.arrowPoint3(): Pair<Float, Float>
}