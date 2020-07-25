package net.inceptioncloud.dragonfly.engine.font.renderer

import net.inceptioncloud.dragonfly.engine.GraphicsEngine

class ScaledFontRenderer(
    val base: IFontRenderer, // 30 -> 15
    val scale: Double        // 2
) : IFontRenderer {

    override fun trimStringToWidth(text: String?, width: Int): String {
        return base.trimStringToWidth(text, (width * scale).toInt())
    }

    override fun trimStringToWidth(text: String?, width: Int, reverse: Boolean): String {
        return base.trimStringToWidth(text, (width * scale).toInt())
    }

    override fun getHeight(): Int {
        return (base.height / scale).toInt()
    }

    override fun wrapFormattedStringToWidth(text: String?, width: Int): String {
        return base.wrapFormattedStringToWidth(text, (width * scale).toInt())
    }

    override fun getCharWidthFloat(c: Char): Float {
        return (base.getCharWidthFloat(c) / scale).toFloat()
    }

    override fun sizeStringToWidth(text: String?, width: Int): Int {
        return base.sizeStringToWidth(text, (width * scale).toInt())
    }

    override fun getStringWidth(text: String?): Int {
        return (base.getStringWidth(text) / scale).toInt()
    }

    override fun drawString(text: String?, x: Int, y: Int, color: Int): Int {
        pushScale()
        return base.drawString(text, (x * scale).toInt(), (y * scale).toInt(), color)
            .also(::popScale)
    }

    override fun drawString(text: String?, x: Float, y: Float, color: Int, dropShadow: Boolean): Int {
        pushScale()
        return if (dropShadow) {
            base.drawStringWithCustomShadow(text, (x * scale).toInt(), (y * scale).toInt(), color, color, (0.7F * scale).toFloat())
        } else {
            base.drawString(text, (x * scale).toFloat(), (y * scale).toFloat(), color, dropShadow)
        }.also(::popScale)
    }

    override fun drawStringWithShadow(text: String?, x: Float, y: Float, color: Int): Int {
        pushScale()
        return base.drawStringWithShadow(text, (x * scale).toFloat(), (y * scale).toFloat(), color)
            .also(::popScale)
    }

    override fun drawStringWithCustomShadow(text: String?, x: Int, y: Int, color: Int, shadowColor: Int, distance: Float): Int {
        pushScale()
        return base.drawStringWithCustomShadow(text, (x * scale).toInt(), (y * scale).toInt(), color, shadowColor, (distance * scale).toFloat())
            .also(::popScale)
    }

    override fun listFormattedStringToWidth(text: String?, width: Int): MutableList<String> {
        return base.listFormattedStringToWidth(text, (width * scale).toInt())
    }

    override fun getCharWidth(c: Char): Int {
        return (base.getCharWidth(c) / scale).toInt()
    }

    private fun pushScale() = GraphicsEngine.pushScale(1 / scale to 1 / scale)

    private fun popScale(i: Int) = GraphicsEngine.popScale()
}