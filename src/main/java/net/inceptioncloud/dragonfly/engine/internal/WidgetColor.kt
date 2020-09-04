package net.inceptioncloud.dragonfly.engine.internal

import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.absoluteValue

/**
 * ## Widget Color
 *
 * This is an advanced color class that extends the default [java.awt.Color] by providing more
 * security and number types. It keeps the color parameters in range when they are applied and
 * allows changing them without initializing a new color object.
 */
class WidgetColor {
    companion object {
        val DEFAULT = WidgetColor(1F, 1F, 1F, 1F)
    }

    /**
     * Instantiates a new [WidgetColor] object based on a [java.awt.Color].
     *
     * @param awt the color that is used for the base [WidgetColor.base] variable
     */
    constructor(awt: Color) {
        this.base = awt
    }

    /**
     * Instantiates a new [WidgetColor] object based on RGB integer values.
     *
     * @param r the red value (0 - 255)
     * @param g the green value (0 - 255)
     * @param b the blue value (0 - 255)
     * @param a the alpha value (0 - 255; by default 255)
     */
    constructor(r: Int, g: Int, b: Int, a: Int = 255) {
        base = Color(
            r.coerceAtLeast(0).coerceAtMost(255),
            g.coerceAtLeast(0).coerceAtMost(255),
            b.coerceAtLeast(0).coerceAtMost(255),
            a.coerceAtLeast(0).coerceAtMost(255)
        )
    }

    /**
     * Instantiates a new [WidgetColor] object based on RGB float values.
     *
     * @param r the red value (0.0F - 1.0F)
     * @param g the green value (0.0F - 1.0F)
     * @param b the blue value (0.0F - 1.0F)
     * @param a the alpha value (0.0F - 1.0F; by default 1.0F)
     */
    constructor(r: Float, g: Float, b: Float, a: Float = 1.0F) {
        base = Color(
            r.coerceAtLeast(0F).coerceAtMost(1F),
            g.coerceAtLeast(0F).coerceAtMost(1F),
            b.coerceAtLeast(0F).coerceAtMost(1F),
            a.coerceAtLeast(0F).coerceAtMost(1F)
        )
    }

    /**
     * Instantiates a new [WidgetColor] object based on RGB double values.
     *
     * @param r the red value (0.0 - 1.0)
     * @param g the green value (0.0 - 1.0)
     * @param b the blue value (0.0 - 1.0)
     * @param a the alpha value (0.0 - 1.0; by default 1.0)
     */
    constructor(r: Double, g: Double, b: Double, a: Double = 0.0) {
        base = Color(
            r.coerceAtLeast(0.0).coerceAtMost(1.0).toFloat(),
            g.coerceAtLeast(0.0).coerceAtMost(1.0).toFloat(),
            b.coerceAtLeast(0.0).coerceAtMost(1.0).toFloat(),
            a.coerceAtLeast(0.0).coerceAtMost(1.0).toFloat()
        )
    }

    /**
     * Instantiates a new [WidgetColor] object based on a hexadecimal value.
     *
     * @param hex the hexadecimal value in integer format (eg. `0xFE0A6C`)
     */
    constructor(hex: Int) {
        base = Color(hex)
    }

    /**
     * Binds the values of the color object to the current OpenGL context.
     */
    fun glBindColor() {
//        GlStateManager.color(redFloat, greenFloat, blueFloat, alphaFloat)
        GL11.glColor4d(redDouble, greenDouble, blueDouble, alphaDouble)
    }

    /**
     * Clones the color and applies the [block] to it.
     */
    fun altered(block: WidgetColor.() -> Unit) = clone().apply(block)

    /**
     * Returns the [red] value as the first destructuring value.
     */
    operator fun component1(): Int = red

    /**
     * Returns the [green] value as the second destructuring value.
     */
    operator fun component2(): Int = green

    /**
     * Returns the [blue] value as the third destructuring value.
     */
    operator fun component3(): Int = blue

    /**
     * Returns the [alpha] value as the second destructuring value.
     */
    operator fun component4(): Int = alpha

    /**
     * Compares two object and checks whether they are the same.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WidgetColor

        if (base != other.base) return false

        return true
    }

    /**
     * Generates the hash-code for the color object.
     */
    override fun hashCode(): Int {
        return base.hashCode()
    }

    override fun toString(): String {
        return "WidgetColor($red, $green, $blue, $alpha)"
    }

    /**
     * Returns an exact copy of this color object.
     */
    fun clone(): WidgetColor {
        return WidgetColor(red, green, blue, alpha)
    }

    /**
     * Selects the color with the highest contrast related to this color.
     */
    fun selectHighestContrast(vararg colors: WidgetColor): WidgetColor {
        require(colors.isNotEmpty()) { "There must be at least one possible color!" }
        return colors.maxBy { getContrastValue(it) }!!
    }

    /**
     * Clone of the [Color.brighter] function with a custom [factor].
     */
    fun brighter(factor: Double): WidgetColor {
        var r: Int = red
        var g: Int = green
        var b: Int = blue
        val alpha: Int = alpha

        val i = (1.0 / (1.0 - factor)).toInt()
        if (r == 0 && g == 0 && b == 0) {
            return WidgetColor(i, i, i, alpha)
        }

        if (r in 1 until i) r = i
        if (g in 1 until i) g = i
        if (b in 1 until i) b = i

        return WidgetColor((r / factor).toInt().coerceAtMost(255),
            (g / factor).toInt().coerceAtMost(255),
            (b / factor).toInt().coerceAtMost(255),
            alpha)
    }

    /**
     * Calculates the contrast value between this and the [other] color.
     */
    fun getContrastValue(other: WidgetColor): Int = (red - other.red).absoluteValue +
            (green - other.green).absoluteValue +
            (blue - other.blue).absoluteValue

    /**
     * The RGB value specified by the [base] color.
     */
    val rgb: Int
        get() = base.rgb

    /**
     * Returns the Minecraft chat color code that can be used to display this color in
     * rendered strings.
     */
    val chatCode: String
        get() = "§${String.format("#%06X", Integer.valueOf(rgb and 0x00FFFFFF))}"

    /**
     * The base color stored in a [java.awt.Color] object.
     *
     * Whenever values of the [WidgetColor] object are accessed, the values of the base color will be used.
     * Whenever values of the [WidgetColor] object are modified, the changes will be reflected on the base color.
     */
    var base: Color

    /**
     * Getter & Setter for the **red** base value in **integer format**.
     *
     * Performs range checking before setting the value. (0 - 255)
     */
    var red
        get() = base.red
        set(value) {
            base = Color(value.coerceAtLeast(0).coerceAtMost(255), green, blue, alpha)
        }

    /**
     * Getter & Setter for the **green** base value in **integer format**.
     *
     * Performs range checking before setting the value. (0 - 255)
     */
    var green
        get() = base.green
        set(value) {
            base = Color(red, value.coerceAtLeast(0).coerceAtMost(255), blue, alpha)
        }

    /**
     * Getter & Setter for the **blue** base value in **integer format**.
     *
     * Performs range checking before setting the value. (0 - 255)
     */
    var blue
        get() = base.blue
        set(value) {
            base = Color(red, green, value.coerceAtLeast(0).coerceAtMost(255), alpha)
        }

    /**
     * Getter & Setter for the **alpha** base value in **integer format**.
     *
     * Performs range checking before setting the value. (0 - 255)
     */
    var alpha
        get() = base.alpha
        set(value) {
            base = Color(red, green, blue, value.coerceAtLeast(0).coerceAtMost(255))
        }

    /**
     * Getter & Setter for the **red** base value in **double format**.
     *
     * Performs range checking before setting the value. (0.0 - 1.0)
     */
    var redDouble
        get() = base.red / 255.0
        set(value) {
            base = Color((value.coerceAtLeast(0.0).coerceAtMost(1.0).toFloat() * 255).toInt(), green, blue, alpha)
        }

    /**
     * Getter & Setter for the **green** base value in **double format**.
     *
     * Performs range checking before setting the value. (0.0 - 1.0)
     */
    var greenDouble
        get() = base.green / 255.0
        set(value) {
            base = Color(red, (value.coerceAtLeast(0.0).coerceAtMost(1.0).toFloat() * 255).toInt(), blue, alpha)
        }

    /**
     * Getter & Setter for the **blue** base value in **double format**.
     *
     * Performs range checking before setting the value. (0.0 - 1.0)
     */
    var blueDouble
        get() = base.blue / 255.0
        set(value) {
            base = Color(red, green, (value.coerceAtLeast(0.0).coerceAtMost(1.0).toFloat() * 255).toInt(), alpha)
        }

    /**
     * Getter & Setter for the **alpha** base value in **double format**.
     *
     * Performs range checking before setting the value. (0.0 - 1.0)
     */
    var alphaDouble
        get() = base.alpha / 255.0
        set(value) {
            base = Color(red, green, blue, (value.coerceAtLeast(0.0).coerceAtMost(1.0).toFloat() * 255).toInt())
        }
}
