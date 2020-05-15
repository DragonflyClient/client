package net.inceptioncloud.minecraftmod.engine.internal

/**
 * ## Mouse Data
 *
 * Holds several information about the mouse that are required when firing move-,
 * click- or drag events.
 *
 * @param mouseX the x position of the mouse, always present
 * @param mouseY the y position of the mouse, always present
 * @param button the button of the mouse that was pressed/released/dragged
 * @param draggingDuration duration in milliseconds since the drag was started
 */
data class MouseData(
    val mouseX: Int,
    val mouseY: Int,
    val button: Int? = null,
    val draggingDuration: Long? = null
)