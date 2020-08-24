package net.inceptioncloud.dragonfly.ui.loader

/**
 * A ui loader that only activates pre-loading when called for the first time.
 *
 * @param timeMillis the value for [getPreloadTimeMillis]
 * @property firstTime whether the gui screen is called for the first time
 */
open class OneTimeUILoader(private val timeMillis: Long) : UILoader {

    private var firstTime = true

    override fun shouldPreload(): Boolean = firstTime

    override fun getPreloadTimeMillis(): Long = timeMillis

    override fun preload() {
        firstTime = false
    }
}