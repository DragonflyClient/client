package net.inceptioncloud.dragonfly.ui.loader

/**
 * Companion objects of gui screens can implement this interface (or its children) to tell
 * the client how much they need to be initialized.
 */
interface UILoader {

    /**
     * Whether the gui screen should be preloaded.
     */
    fun shouldPreload(): Boolean

    /**
     * The amount of milliseconds it takes to prepare the screen after it has been initialized
     * (only the actions that take part in the client thread).
     */
    fun getPreloadTimeMillis(): Long

    /**
     * Called when the gui screen is preloaded.
     */
    fun preload()
}