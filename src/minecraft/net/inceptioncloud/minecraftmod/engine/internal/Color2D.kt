package net.inceptioncloud.minecraftmod.engine.internal

import org.lwjgl.opengl.GL11
import java.awt.Color

/**
 * ## Color 2D
 *
 * This is an advanced color class that extends the default [java.awt.Color] by providing more
 * security and number types. It keeps the color parameters in range when they are applied and
 * allows changing them without initializing a new color object.
 */
class Color2D
{
    /**
     * Instantiates a new [Color2D] object based on a [java.awt.Color].
     *
     * @param awt the color that is used for the base [Color2D.base] variable
     */
    constructor(awt: Color)
    {
        this.base = awt
    }

    /**
     * Instantiates a new [Color2D] object based on RGB integer values.
     *
     * @param r the red value (0 - 255)
     * @param g the green value (0 - 255)
     * @param b the blue value (0 - 255)
     * @param a the alpha value (0 - 255; by default 255)
     */
    constructor(r: Int, g: Int, b: Int, a: Int = 255)
    {
        base = Color(
                r.coerceAtLeast(0).coerceAtMost(255),
                g.coerceAtLeast(0).coerceAtMost(255),
                b.coerceAtLeast(0).coerceAtMost(255),
                a.coerceAtLeast(0).coerceAtMost(255)
        )
    }

    /**
     * Instantiates a new [Color2D] object based on RGB float values.
     *
     * @param r the red value (0.0F - 1.0F)
     * @param g the green value (0.0F - 1.0F)
     * @param b the blue value (0.0F - 1.0F)
     * @param a the alpha value (0.0F - 1.0F; by default 1.0F)
     */
    constructor(r: Float, g: Float, b: Float, a: Float = 1.0F)
    {
        base = Color(
                r.coerceAtLeast(0F).coerceAtMost(1F),
                g.coerceAtLeast(0F).coerceAtMost(1F),
                b.coerceAtLeast(0F).coerceAtMost(1F),
                a.coerceAtLeast(0F).coerceAtMost(1F)
        )
    }

    /**
     * Instantiates a new [Color2D] object based on RGB double values.
     *
     * @param r the red value (0.0 - 1.0)
     * @param g the green value (0.0 - 1.0)
     * @param b the blue value (0.0 - 1.0)
     * @param a the alpha value (0.0 - 1.0; by default 1.0)
     */
    constructor(r: Double, g: Double, b: Double, a: Double = 0.0)
    {
        base = Color(
                r.coerceAtLeast(0.0).coerceAtMost(1.0).toFloat(),
                g.coerceAtLeast(0.0).coerceAtMost(1.0).toFloat(),
                b.coerceAtLeast(0.0).coerceAtMost(1.0).toFloat(),
                a.coerceAtLeast(0.0).coerceAtMost(1.0).toFloat()
        )
    }

    /**
     * Instantiates a new [Color2D] object based on a hexadecimal value.
     *
     * @param hex the hexadecimal value in integer format (eg. `0xFE0A6C`)
     */
    constructor(hex: Int)
    {
        base = Color(hex)
    }

    /**
     * Binds the values of the color object to the current OpenGL context.
     */
    fun glBindColor()
    {
        GL11.glColor4d(redDouble, greenDouble, blueDouble, alphaDouble)
    }

    /**
     * Compares two object and checks whether they are the same.
     */
    override fun equals(other: Any?): Boolean
    {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Color2D

        if (base != other.base) return false

        return true
    }

    /**
     * Generates the hash-code for the color object.
     */
    override fun hashCode(): Int
    {
        return base.hashCode()
    }

    /**
     * Returns an exact copy of this color object.
     */
    fun clone(): Color2D
    {
        return Color2D(red, green, blue, alpha)
    }

    /**
     * The base color stored in a [java.awt.Color] object.
     *
     * Whenever values of the [Color2D] object are accessed, the values of the base color will be used.
     * Whenever values of the [Color2D] object are modified, the changes will be reflected on the base color.
     */
    var base: Color

    /**
     * Getter & Setter for the **red** base value in **integer format**.
     *
     * Performs range checking before setting the value. (0 - 255)
     */
    var red
        get() = base.red
        set(value)
        {
            base = Color(value.coerceAtLeast(0).coerceAtMost(255), green, blue, alpha)
        }

    /**
     * Getter & Setter for the **green** base value in **integer format**.
     *
     * Performs range checking before setting the value. (0 - 255)
     */
    var green
        get() = base.green
        set(value)
        {
            base = Color(red, value.coerceAtLeast(0).coerceAtMost(255), blue, alpha)
        }

    /**
     * Getter & Setter for the **blue** base value in **integer format**.
     *
     * Performs range checking before setting the value. (0 - 255)
     */
    var blue
        get() = base.blue
        set(value)
        {
            base = Color(red, green, value.coerceAtLeast(0).coerceAtMost(255), alpha)
        }

    /**
     * Getter & Setter for the **alpha** base value in **integer format**.
     *
     * Performs range checking before setting the value. (0 - 255)
     */
    var alpha
        get() = base.alpha
        set(value)
        {
            base = Color(red, green, blue, value.coerceAtLeast(0).coerceAtMost(255))
        }

    /**
     * Getter & Setter for the **red** base value in **float format**.
     *
     * Performs range checking before setting the value. (0.0F - 1.0F)
     */
    var redFloat
        get() = base.red / 255.0F
        set(value)
        {
            base = Color((value.coerceAtLeast(0F).coerceAtMost(1F) * 255).toInt(), green, blue, alpha)
        }

    /**
     * Getter & Setter for the **green** base value in **float format**.
     *
     * Performs range checking before setting the value. (0.0F - 1.0F)
     */
    var greenFloat
        get() = base.green / 255.0F
        set(value)
        {
            base = Color(red, (value.coerceAtLeast(0F).coerceAtMost(1F) * 255).toInt(), blue, alpha)
        }

    /**
     * Getter & Setter for the **blue** base value in **float format**.
     *
     * Performs range checking before setting the value. (0.0F - 1.0F)
     */
    var blueFloat
        get() = base.blue / 255.0F
        set(value)
        {
            base = Color(red, green, (value.coerceAtLeast(0F).coerceAtMost(1F) * 255).toInt(), alpha)
        }

    /**
     * Getter & Setter for the **alpha** base value in **float format**.
     *
     * Performs range checking before setting the value. (0.0F - 1.0F)
     */
    var alphaFloat
        get() = base.alpha / 255.0F
        set(value)
        {
            base = Color(red, green, blue, (value.coerceAtLeast(0F).coerceAtMost(1F) * 255).toInt())
        }

    /**
     * Getter & Setter for the **red** base value in **double format**.
     *
     * Performs range checking before setting the value. (0.0 - 1.0)
     */
    var redDouble
        get() = base.red / 255.0
        set(value)
        {
            base = Color((value.coerceAtLeast(0.0).coerceAtMost(1.0).toFloat() * 255).toInt(), green, blue, alpha)
        }

    /**
     * Getter & Setter for the **green** base value in **double format**.
     *
     * Performs range checking before setting the value. (0.0 - 1.0)
     */
    var greenDouble
        get() = base.green / 255.0
        set(value)
        {
            base = Color(red, (value.coerceAtLeast(0.0).coerceAtMost(1.0).toFloat() * 255).toInt(), blue, alpha)
        }

    /**
     * Getter & Setter for the **blue** base value in **double format**.
     *
     * Performs range checking before setting the value. (0.0 - 1.0)
     */
    var blueDouble
        get() = base.blue / 255.0
        set(value)
        {
            base = Color(red, green, (value.coerceAtLeast(0.0).coerceAtMost(1.0).toFloat() * 255).toInt(), alpha)
        }

    /**
     * Getter & Setter for the **alpha** base value in **double format**.
     *
     * Performs range checking before setting the value. (0.0 - 1.0)
     */
    var alphaDouble
        get() = base.alpha / 255.0
        set(value)
        {
            base = Color(red, green, blue, (value.coerceAtLeast(0.0).coerceAtMost(1.0).toFloat() * 255).toInt())
        }
}
