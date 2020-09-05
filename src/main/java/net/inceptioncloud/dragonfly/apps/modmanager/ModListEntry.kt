package net.inceptioncloud.dragonfly.apps.modmanager

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.structure.IColor
import net.inceptioncloud.dragonfly.engine.structure.IDimension
import net.inceptioncloud.dragonfly.engine.structure.IPosition
import net.inceptioncloud.dragonfly.engine.widgets.assembled.RoundedRectangle
import net.inceptioncloud.dragonfly.engine.widgets.assembled.TextField
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Image
import net.inceptioncloud.dragonfly.mods.core.DragonflyMod
import net.minecraft.util.ResourceLocation

class ModListEntry(
    val mod: DragonflyMod,
    initializerBlock: (ModListEntry.() -> Unit)? = null
) : AssembledWidget<ModListEntry>(initializerBlock), IPosition, IDimension, IColor {

    override var x: Double by property(0.0)
    override var y: Double by property(0.0)
    override var width: Double by property(353.0)
    override var height: Double by property(53.0)

    override var color: WidgetColor by property(DragonflyPalette.background)

    var icon: ResourceLocation? by property(ResourceLocation("dragonflyres/icons/mods/${mod.cleanName}.png"))
    var text: String by property(mod.name)

    var selected = false

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
            dropShadow = true
            shadowDistance = 2.0
            shadowColor = WidgetColor(0, 0, 0, 80)
        }

    }
}