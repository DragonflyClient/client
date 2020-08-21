package net.inceptioncloud.dragonfly.apps.modmanager

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.animation.PostAction
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation.Companion.morph
import net.inceptioncloud.dragonfly.engine.animation.post
import net.inceptioncloud.dragonfly.engine.contains
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.internal.annotations.Interpolate
import net.inceptioncloud.dragonfly.engine.sequence.easing.EaseQuad
import net.inceptioncloud.dragonfly.engine.sequence.easing.EaseQuart
import net.inceptioncloud.dragonfly.engine.structure.IColor
import net.inceptioncloud.dragonfly.engine.structure.IDimension
import net.inceptioncloud.dragonfly.engine.structure.IPosition
import net.inceptioncloud.dragonfly.engine.widgets.assembled.RoundedRectangle
import net.inceptioncloud.dragonfly.engine.widgets.assembled.TextField
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Image
import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation

class ModListEntry(
    initializerBlock: (ModListEntry.() -> Unit)? = null
) : AssembledWidget<ModListEntry>(initializerBlock), IPosition, IDimension, IColor {

    override var x: Double by property(0.0)
    override var y: Double by property(0.0)
    override var width: Double by property(353.0)
    override var height: Double by property(53.0)
    @Interpolate
    override var color: WidgetColor by property(DragonflyPalette.background)

    var icon: ResourceLocation? by property(null)
    var text: String by property("No text given")

    override fun assemble(): Map<String, Widget<*>> = mapOf(
        "background" to RoundedRectangle(),
        "icon" to Image(),
        "text" to TextField()
    )

    override fun updateStructure() {
        val iconMargin = 7.0

        "background"<RoundedRectangle> {
            x = this@ModListEntry.x
            y = this@ModListEntry.y
            width = this@ModListEntry.width
            height = this@ModListEntry.height
            color = this@ModListEntry.color
            arc = 5.0
        }

        val iconWidget = "icon"<Image> {
            x = this@ModListEntry.x + iconMargin
            y = this@ModListEntry.y + iconMargin
            width = this@ModListEntry.height - (iconMargin * 2)
            height = width
            resourceLocation = icon
        }!!

        "text"<TextField> {
            x = iconWidget.x + iconWidget.width + iconMargin
            y = this@ModListEntry.y
            width = this@ModListEntry.width - (x - this@ModListEntry.x)
            height = this@ModListEntry.height - 4
            staticText = text
            textAlignVertical = Alignment.CENTER
            textAlignHorizontal = Alignment.START
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 28 * 2, useScale = false)
            color = DragonflyPalette.foreground
        }

    }

    override fun handleMousePress(data: MouseData) {
        if (data in this) {
            morph(100, EaseQuad.IN_OUT, ::color to DragonflyPalette.accentNormal)?.start()?.post { animation, widget ->
                if (Minecraft.getMinecraft().currentScreen is ModManagerUI) {
                    (Minecraft.getMinecraft().currentScreen as ModManagerUI).selectedEntry = this
                    (Minecraft.getMinecraft().currentScreen as ModManagerUI).updateScreen()
                }
            }
        } else {
            morph(100, EaseQuad.IN_OUT, ::color to DragonflyPalette.background)?.start()?.post { animation, widget ->
                if (Minecraft.getMinecraft().currentScreen is ModManagerUI) {
                    (Minecraft.getMinecraft().currentScreen as ModManagerUI).selectedEntry = null
                    (Minecraft.getMinecraft().currentScreen as ModManagerUI).updateScreen()
                }
            }
        }
    }

}