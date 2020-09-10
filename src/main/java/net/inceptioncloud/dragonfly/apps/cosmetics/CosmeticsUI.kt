package net.inceptioncloud.dragonfly.apps.cosmetics

import net.inceptioncloud.dragonfly.controls.ControlElement
import net.inceptioncloud.dragonfly.controls.sidebar.SidebarEntry
import net.inceptioncloud.dragonfly.controls.ui.ControlsUI
import net.inceptioncloud.dragonfly.mods.keystrokes.KeystrokesMod
import net.minecraft.client.gui.GuiScreen
import net.minecraft.util.ResourceLocation

class CosmeticsUI(previousScreen: GuiScreen) : ControlsUI(previousScreen) {

    override val sidebarWidth: Double = 400.0

    override val controlsWidth: Double
        get() = width - 400.0 - 120.0 - 500.0

    override val controlsX: Double
        get() = sidebarWidth + 60.0

    override val placeholderImage: ResourceLocation? = ResourceLocation("dragonflyres/vectors/equipment.png")
    override val placeholderText: String? = "Select a cosmetic item from the left to customize it."

    override fun produceSidebar(): Collection<SidebarEntry> = listOf(
        SidebarEntry("Huhu"),
        SidebarEntry("Ich"),
        SidebarEntry("Bins", ResourceLocation("dragonflyres/icons/diagnostics.png")),
        SidebarEntry("12345", ResourceLocation("dragonflyres/icons/diagnostics.png"))
    )

    override fun produceControls(entry: SidebarEntry): Collection<ControlElement<*>>? =
        KeystrokesMod.publishControls()
}