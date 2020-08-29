package net.inceptioncloud.dragonfly.hotkeys

import java.awt.Color

abstract class Hotkey {

    /**
     * Primary key
     */
    abstract val key: Int

    /**
     * Secondary key, this key is not necessary if the user don't want a secondary key he can left this key empty,
     * but if one is set the key has to be pressed first
     */
    abstract val modifierKey: Int?

    /**
     * Time until the 'actionPerformed' function is called (on key/keys hold)
     */
    abstract val time: Double

    /**
     * Delay until the user can use the hotkey again after the 'actionPerformed' function was called
     */
    abstract val delay: Double

    /**
     * Color for the animation, ui or something else
     */
    abstract val color: Color

    /**
     * Function which is called after the time has expired (on key/keys hold)
     */
    protected abstract fun actionPerformed()

    /**
     * Function which is called while the key/keys are hold,
     * this function is used to draw an animation or something else
     */
    abstract fun draw()

}