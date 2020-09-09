package net.inceptioncloud.dragonfly.controls.ui

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.controls.ControlElement
import net.inceptioncloud.dragonfly.controls.ControlsManager
import net.inceptioncloud.dragonfly.controls.sidebar.SidebarEntry
import net.inceptioncloud.dragonfly.controls.sidebar.SidebarManager
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.internal.Alignment
import net.inceptioncloud.dragonfly.engine.internal.MouseData
import net.inceptioncloud.dragonfly.engine.switch
import net.inceptioncloud.dragonfly.engine.widgets.assembled.BackNavigation
import net.inceptioncloud.dragonfly.engine.widgets.assembled.TextField
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Image
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Rectangle
import net.minecraft.client.gui.GuiScreen
import net.minecraft.util.ResourceLocation
import kotlin.math.min

abstract class ControlsUI(val previousScreen: GuiScreen) : GuiScreen() {

    protected val controlsManager = ControlsManager(
        guiScreen = this,
        originY = 40.0,
        margin = 15.0
    )

    protected val sidebarManager = SidebarManager(
        guiScreen = this,
        x = 0.0,
        y = 0.0,
        entryHeight = 55.0,
        entryPadding = 15.0,
        entryGap = 5.0
    ).apply {
        produceEntries(::produceSidebar)
        consumeEntry { id, entry ->
            consumeEntry(id, entry)
            showControls()
        }
    }

    override var customScaleFactor: () -> Double? = {
        min(mc.displayWidth / 1920.0, mc.displayHeight / 1080.0)
    }

    override fun initGui() {
        +Rectangle {
            x = 0.0
            y = 0.0
            width = this@ControlsUI.width.toDouble()
            height = this@ControlsUI.height.toDouble()
            color = DragonflyPalette.foreground.brighter(0.7)
        } id "background-color"

        +BackNavigation {
            x = 30.0
            y = this@ControlsUI.height - height - 30.0
            action {
                onClose()
                previousScreen.switch()
            }
        } id "back-navigation"

        +Image {
            height = this@ControlsUI.height / 2.0
            width = height
            x = controlsX + (controlsWidth / 2.0 - width / 2.0)
            y = this@ControlsUI.height / 2.5 - height / 2.0
            resourceLocation = placeholderImage
            isVisible = placeholderImage != null
        } id "placeholder-image"

        +TextField {
            positionBelow("placeholder-image", 10.0)
            width = controlsWidth / 2.0
            adaptHeight = true
            x = controlsX + controlsWidth / 2.0 - width / 2.0
            staticText = placeholderText ?: ""
            fontRenderer = Dragonfly.fontManager.defaultFont.fontRenderer(size = 60, useScale = false)
            color = DragonflyPalette.background
            textAlignHorizontal = Alignment.CENTER
            isVisible = placeholderImage != null && placeholderText != null
        } id "placeholder-text"

        showSidebar()
        showControls()
    }

    protected fun showSidebar() {
        sidebarManager.apply {
            width = sidebarWidth
            height = this@ControlsUI.height.toDouble()
            entryWidth = sidebarEntryWidth
            reset()
            show()
        }
    }

    protected fun showControls() {
        controlsManager.reset()

        val placeholderImage = getWidget<Image>("placeholder-image")
        val placeholderText = getWidget<TextField>("placeholder-text")

        val selected = sidebarManager.selectedEntry
        val controls = selected?.let { produceControls(it) }

        placeholderImage?.isVisible = controls == null
        placeholderText?.isVisible = controls == null

        if (!controls.isNullOrEmpty()) {
            controlsManager.originX = controlsX
            controlsManager.width = controlsWidth
            controlsManager.show(controls)
        }
    }

    override fun handleMouseInput() {
        controlsManager.scrollbar.handleMouseInput()
        sidebarManager.scrollbar.handleMouseInput()
        super.handleMouseInput()
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        val data = MouseData(mouseX, mouseY, mouseButton)
        sidebarManager.mouseClicked(data)
        super.mouseClicked(mouseX, mouseY, mouseButton)
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        if (keyCode == 1 && canManuallyClose) {
            previousScreen.switch()
            onClose()
            return
        }

        super.keyTyped(typedChar, keyCode)
    }

    abstract val sidebarWidth: Double

    open val sidebarEntryWidth: Double
        get() = sidebarWidth - 30.0

    abstract val controlsWidth: Double

    open val controlsX: Double
        get() = sidebarWidth

    open val placeholderImage: ResourceLocation? = null

    open val placeholderText: String? = null

    abstract fun produceSidebar(): Collection<SidebarEntry>

    abstract fun produceControls(entry: SidebarEntry): Collection<ControlElement<*>>?

    open fun consumeEntry(id: String?, entry: SidebarEntry?) {}

    open fun onClose() {}
}