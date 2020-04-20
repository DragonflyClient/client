package net.inceptioncloud.minecraftmod.engine.structure

/**
 * ## Drawable Interface
 *
 * Every graphics object that can be drawn implements this interface.
 */
interface IDrawable
{
    /**
     * A simple function that is used to tell the object that it should drawn itself on the screen.
     * It should be the last call when constructing a screen object as it returns nothing.
     */
    fun draw()
}