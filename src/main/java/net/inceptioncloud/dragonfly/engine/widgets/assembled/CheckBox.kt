package net.inceptioncloud.dragonfly.engine.widgets.assembled

import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation.Companion.morph
import net.inceptioncloud.dragonfly.engine.internal.AssembledWidget
import net.inceptioncloud.dragonfly.engine.internal.MouseData
import net.inceptioncloud.dragonfly.engine.internal.Widget
import net.inceptioncloud.dragonfly.engine.internal.WidgetColor
import net.inceptioncloud.dragonfly.engine.sequence.easing.EaseQuad
import net.inceptioncloud.dragonfly.engine.structure.IColor
import net.inceptioncloud.dragonfly.engine.structure.IDimension
import net.inceptioncloud.dragonfly.engine.structure.IPosition
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Image
import net.minecraft.client.Minecraft
import net.minecraft.client.audio.PositionedSoundRecord
import net.minecraft.util.ResourceLocation

class CheckBox(
    initializerBlock: (CheckBox.() -> Unit)? = null
) : AssembledWidget<CheckBox>(initializerBlock), IPosition, IDimension, IColor {

    override var x: Float by property(0.0F)
    override var y: Float by property(0.0F)
    override var width: Float by property(200.0F)
    override var height: Float by property(20.0F)
    override var color: WidgetColor by property(DragonflyPalette.foreground)
    var check: Image? = null

    val resourceLocation = ResourceLocation("dragonflyres/icons/check.png")

    var arc: Float by property(3.0F)

    var isChecked: Boolean = false
    var enableClickSound: Boolean = true
    private var onClick: () -> Unit = {}

    override fun assemble(): Map<String, Widget<*>> = mapOf(
        "container" to RoundedRectangle(),
        "check" to Image()
    )

    override fun updateStructure() {
        "container"<RoundedRectangle> {
            x = this@CheckBox.x
            y = this@CheckBox.y
            width = this@CheckBox.width
            height = this@CheckBox.height
            color = this@CheckBox.color
            arc = this@CheckBox.arc
        }

        check = "check"<Image> {
            x = this@CheckBox.x + 3.0f
            y = this@CheckBox.y + 3.0f
            width = this@CheckBox.width - 6.0f
            height = this@CheckBox.height - 6.0f
            resourceLocation = this@CheckBox.resourceLocation
            color = color.altered {
                alphaFloat = if (isChecked) {
                    1.0f
                } else {
                    0.0f
                }
            }
        }

    }

    override fun handleMousePress(data: MouseData) {
        if (isHovered) {
            if (enableClickSound) {
                Minecraft.getMinecraft().soundHandler.playSound(
                    PositionedSoundRecord.create(
                        ResourceLocation("gui.button.press"),
                        1.0f
                    )
                )
            }

            toggle()
            onClick()
        }
    }

    /**
     * Changes whether the button is currently toggled.
     */
    fun toggle() {
        isChecked = !isChecked

        getWidget<Image>("check")?.morph(
            30, EaseQuad.IN_OUT,
            Image::color to check?.color?.altered {
                alphaFloat = if (isChecked) {
                    1.0f
                } else {
                    0.0f
                }
            }
        )?.start()
    }

    /**
     * Type safe builder to set the value for the [onClick] function.
     */
    fun onClick(block: () -> Unit) {
        onClick = block
    }
}