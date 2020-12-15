package net.inceptioncloud.dragonfly.overlay.hotaction

/**
 * An action that can be executed by a [HotAction].
 *
 * @param name the display name of the action
 * @param perform the lambda that is called when the action is executed
 */
data class Action(
    val name: String,
    val perform: (HotActionWidget) -> Unit
)