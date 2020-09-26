package net.inceptioncloud.dragonfly.apps.modmanager

import net.inceptioncloud.dragonfly.controls.ControlElement
import net.inceptioncloud.dragonfly.controls.sidebar.SidebarEntry
import net.inceptioncloud.dragonfly.controls.ui.ControlsUI
import net.inceptioncloud.dragonfly.engine.internal.ImageResource
import net.inceptioncloud.dragonfly.mods.core.DragonflyMod
import net.inceptioncloud.dragonfly.mods.keystrokes.KeystrokesManager
import net.inceptioncloud.dragonfly.mods.keystrokes.KeystrokesMod
import net.inceptioncloud.dragonfly.ui.loader.OneTimeUILoader
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.util.ResourceLocation

class ModManagerUI(previousScreen: GuiScreen) : ControlsUI(previousScreen) {

    companion object : OneTimeUILoader(500)

    override val sidebarWidth = 500.0

    override val controlsWidth: Double
        get() = (width - sidebarWidth - 600.0).coerceIn(1000.0..1500.0)

    override val controlsX: Double
        get() = sidebarWidth + (width - sidebarWidth) / 2.0 - controlsWidth / 2.0

    override val placeholderImage: ResourceLocation? = ResourceLocation("dragonflyres/vectors/rocket.png")
    override val placeholderText: String? = "Choose a mod in the sidebar to (de-)activate it and customize it's appearance, behavior and much more."

    override fun produceSidebar(): Collection<SidebarEntry> =
        ModManagerApp.availableMods.map { SidebarEntry(it.name, ImageResource(it.iconResource), it) }

    override fun produceControls(entry: SidebarEntry): Collection<ControlElement<*>>? =
        (entry.metadata as? DragonflyMod)?.publishControls()

    override fun onClose() {
        reloadKeystrokesOverlay()
    }

    private fun reloadKeystrokesOverlay() {
        for (keystroke in KeystrokesManager.keystrokes) {
            keystroke.scale = KeystrokesMod.scale
            keystroke.space = KeystrokesMod.space
            keystroke.fontSize = KeystrokesMod.fontSize

            Minecraft.getMinecraft().ingameGUI.initInGameOverlay()
        }
    }
}