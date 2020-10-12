package net.inceptioncloud.dragonfly.cosmetics.logic

import net.inceptioncloud.dragonfly.engine.GraphicsEngine
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation.Companion.morph
import net.inceptioncloud.dragonfly.engine.animation.post
import net.inceptioncloud.dragonfly.engine.internal.WidgetColor
import net.inceptioncloud.dragonfly.engine.sequence.easing.EaseCubic
import net.inceptioncloud.dragonfly.engine.sequence.easing.EaseQuint
import net.inceptioncloud.dragonfly.engine.utils.getCirclePoints
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Line
import net.inceptioncloud.dragonfly.overlay.ScreenOverlay
import net.inceptioncloud.dragonfly.overlay.modal.Modal
import net.minecraft.client.renderer.texture.DynamicTexture
import java.net.URL
import javax.imageio.ImageIO

/**
 * Controls the appearance of [CosmeticsPreviewModal]s.
 */
object CosmeticsPreview {

    /**
     * Shows the preview for the given cosmetic [item].
     */
    fun show(item: CosmeticData) {
        val model = CosmeticsManager.getDatabaseModelById(item.cosmeticId)!!
        val image = ImageIO.read(URL(model.get("logo").asString))
        val dynamicTexture = DynamicTexture(image)

        Modal.showModal(CosmeticsPreviewModal(item, model, dynamicTexture)) {
            val w = ScreenOverlay.dimensions.getWidth()
            val h = ScreenOverlay.dimensions.getHeight()

            makeAnimation(w * 0.5, h * 0.5)
        }
    }

    /**
     * Generates a party animation around the given [x] and [y] position.
     */
    private fun makeAnimation(x: Double, y: Double) {
        val size = 540.0
        val deg = 0..360 step 7
        val innerPoints = getCirclePoints(x, y, 170.0, deg)
        val outerPoints = getCirclePoints(x, y, size, deg)
        val endOuterPoints = getCirclePoints(x, y, size + 30.0, deg)
        val partyColors = listOf(
            0xfed330, 0xf7b731, 0xfd9644, 0xfa8231, 0xfc5c65, 0xeb3b5a, 0xa55eea, 0x8854d0,
            0x4b7bec, 0x3867d6, 0x45aaf2, 0x2d98da, 0x2bcbba, 0x0fb9b1, 0x26de81, 0x20bf6b
        )

        innerPoints.forEachIndexed { index, inner ->
            val (originX, originY) = inner
            val (outX, outY) = outerPoints[index]
            val (endOutX, endOutY) = endOuterPoints[index]
            val partyColor = WidgetColor(partyColors[index % partyColors.size])

            ScreenOverlay.stage.add("party-${System.nanoTime()}" to Line().apply {
                this.x = originX
                this.y = originY
                this.endX = originX
                this.endY = originY
                lineWidth = 3.0
                color = partyColor

                morph(
                    (size / 2).toInt(), EaseCubic.OUT,
                    Line::x to outX, Line::y to outY,
                    Line::endX to endOutX, Line::endY to endOutY
                )?.start()

                GraphicsEngine.runAfter(size.toLong()) {
                    morph(
                        (size / 3).toInt(), EaseQuint.OUT, true,
                        Line::color to partyColor.altered { alphaDouble = 0.0 }
                    )?.post { _, widget -> ScreenOverlay.stage.remove(widget) }?.start()
                }
            })
        }
    }
}

