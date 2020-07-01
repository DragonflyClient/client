package net.inceptioncloud.dragonfly.engine.internal

abstract class DynamicText : () -> String {

    private var lastValue = "unknown"

    override fun invoke(): String = supply().also { lastValue = it }

    abstract fun supply(): String

    override fun toString(): String {
        return "DynamicText(lastValue=$lastValue)"
    }
}