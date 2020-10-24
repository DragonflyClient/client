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

    override val sidebarWidth: Float = 500.0f

    override val controlsWidth: Float
        get() = (width - sidebarWidth - 600.0f).coerceIn(1000.0f..1500.0f)

    override val controlsX: Float
        get() = sidebarWidth + (width - sidebarWidth) / 2 - controlsWidth / 2

    override val placeholderImage: ResourceLocation? = ResourceLocation("dragonflyres/vectors/rocket.png")
    override val placeholderText: String? = "Choose a mod in the sidebar to (de-)activate it and customize it's appearance, behavior and much more."

    override fun produceSidebar(): Collection<SidebarEntry> =
        ModManagerApp.availableMods.map { SidebarEntry(it.name, ImageResource(it.iconResource), it) }

    override fun produceControls(entry: SidebarEntry): Collection<ControlElement<*>>? =
        (entry.metadata as? DragonflyMod)?.publishControls()

    override fun onClose() {
        for(keyStroke in KeystrokesManager.keystrokes) {
            keyStroke.backgroundColor = KeystrokesMod.bgInactiveColor
            keyStroke.textColor = KeystrokesMod.textInactiveColor
            keyStroke.fontSize = KeystrokesMod.fontSize
            keyStroke.scale = KeystrokesMod.scale
            keyStroke.space = KeystrokesMod.space
        }

        Minecraft.getMinecraft().ingameGUI.initInGameOverlay()
    }

}