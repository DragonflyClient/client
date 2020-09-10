package net.inceptioncloud.dragonfly.apps.cosmetics

import net.inceptioncloud.dragonfly.controls.ControlElement
import net.inceptioncloud.dragonfly.controls.sidebar.SidebarEntry
import net.inceptioncloud.dragonfly.controls.ui.ControlsUI
import net.inceptioncloud.dragonfly.cosmetics.logic.CosmeticsManager
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.internal.ImageResource
import net.inceptioncloud.dragonfly.mods.keystrokes.KeystrokesMod
import net.inceptioncloud.dragonfly.utils.MojangRequest
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.util.ResourceLocation
import java.awt.image.BufferedImage

class CosmeticsUI(previousScreen: GuiScreen) : ControlsUI(previousScreen) {

    override val sidebarWidth: Double = 400.0

    override val controlsWidth: Double
        get() = width - 400.0 - 120.0 - 500.0

    override val controlsX: Double
        get() = sidebarWidth + 60.0

    override val placeholderImage: ResourceLocation? = ResourceLocation("dragonflyres/vectors/equipment.png")
    override val placeholderText: String? = "Select a cosmetic item from the left to customize it."

    private val cosmetics by lazy { CosmeticsManager.fetchDragonflyCosmetics() }

    override fun produceSidebar(): Collection<SidebarEntry> {
        val cosmetics = cosmetics ?: return listOf()
        val accounts = cosmetics.groupBy { it.minecraft }

        return accounts.flatMap { entry ->
            var playerName: String? = null
            var playerSkull: BufferedImage? = null

            MojangRequest()
                .withUUID(entry.key)
                .getName { playerName = it }
                .getSkull { playerSkull = it }

            val playerSkullTexture = playerSkull?.let { DynamicTexture(it) }
            val icon = playerSkullTexture?.let { ImageResource(it) }

            listOf(
                SidebarEntry("${DragonflyPalette.accentBright.chatCode}$playerName", icon).apply {
                    iconMargin = 10.0
                    isSelectable = false
                }
            ) + entry.value.mapNotNull {
                val cosmeticName = CosmeticsManager.getDatabaseModelById(it.cosmeticId)?.get("name")?.asString
                cosmeticName?.let { text -> SidebarEntry(text) }
            }
        }
    }

    override fun produceControls(entry: SidebarEntry): Collection<ControlElement<*>>? =
        KeystrokesMod.publishControls()
}