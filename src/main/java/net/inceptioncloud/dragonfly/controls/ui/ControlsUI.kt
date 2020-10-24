package net.inceptioncloud.dragonfly.controls.ui

import net.inceptioncloud.dragonfly.controls.ControlElement
import net.inceptioncloud.dragonfly.controls.ControlsManager
import net.inceptioncloud.dragonfly.controls.sidebar.SidebarEntry
import net.inceptioncloud.dragonfly.controls.sidebar.SidebarManager
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.internal.Alignment
import net.inceptioncloud.dragonfly.engine.internal.MouseData
import net.inceptioncloud.dragonfly.engine.switch
import net.inceptioncloud.dragonfly.engine.widgets.assembled.TextField
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Image
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Rectangle
import net.inceptioncloud.dragonfly.engine.font.Typography
import net.inceptioncloud.dragonfly.engine.font.font
import net.inceptioncloud.dragonfly.utils.Keep
import net.minecraft.client.gui.GuiScreen
import net.minecraft.util.ResourceLocation

/**
 * A class for conveniently create so-called controls user interfaces.
 *
 * These gui screens consist of a sidebar and a dedicated controls area where the
 * control elements are shown based on which sidebar entry is selected. You can
 * fully customize the appearance of this gui by overriding the dedicated members.
 */
@Keep
abstract class ControlsUI(val previousScreen: GuiScreen) : GuiScreen() {

    /**
     * Manages the controls that are visible on the screen.
     */
    protected open val controlsManager = ControlsManager(
        guiScreen = this,
        originY = 40.0f,
        overflowY = 40.0f,
        margin = 15.0f
    )

    /**
     * Manages the sidebar that shows the different controls sections.
     */
    protected open val sidebarManager = SidebarManager(
        guiScreen = this,
        x = 0.0f,
        y = 0.0f,
        entryHeight = 55.0f,
        entryPadding = 15.0f,
        entryGap = 5.0f
    ).apply {
        produceEntries(::produceSidebar)
        consumeEntry { id, entry ->
            consumeEntry(id, entry)
            initControls()
        }
    }

    override var isNativeResolution: Boolean = true

    override fun initGui() {
        +Rectangle {
            x = 0.0f
            y = 0.0f
            width = this@ControlsUI.width.toFloat()
            height = this@ControlsUI.height.toFloat()
            color = DragonflyPalette.foreground.brighter(0.7)
        } id "background-color"

        +Image {
            height = this@ControlsUI.height / 2f
            width = height
            x = controlsX + (controlsWidth / 2 - width / 2)
            y = this@ControlsUI.height / 2.5f - height / 2
            resourceLocation = placeholderImage
            isVisible = placeholderImage != null
        } id "placeholder-image"

        +TextField {
            positionBelow("placeholder-image", 10.0f)
            width = controlsWidth / 2
            adaptHeight = true
            x = controlsX + controlsWidth / 2 - width / 2
            staticText = placeholderText ?: ""
            fontRenderer = font(Typography.HEADING_2)
            color = DragonflyPalette.background
            textAlignHorizontal = Alignment.CENTER
            isVisible = placeholderImage != null && placeholderText != null
        } id "placeholder-text"

        initSidebar()
        initControls()
    }

    /**
     * Initializes the sidebar using the [sidebarManager]. This function is called
     * when the gui is initialized.
     */
    protected fun initSidebar() {
        sidebarManager.reset()
        sidebarManager.apply {
            width = sidebarWidth
            height = this@ControlsUI.height.toFloat()
            entryWidth = sidebarEntryWidth
            reset()
            show()
        }
    }

    /**
     * Initializes the controls using the [initControls]. This function is called
     * when the gui is initialized. And when a sidebar entry is selected.
     */
    protected fun initControls() {
        controlsManager.reset()

        val placeholderImage = getWidget<Image>("placeholder-image")
        val placeholderText = getWidget<TextField>("placeholder-text")

        val selected = sidebarManager.selectedEntry
        val controls = selected?.let { produceControls(it) }

        placeholderImage?.isVisible = controls == null
        placeholderText?.isVisible = controls == null

        if (!controls.isNullOrEmpty()) {
            controlsManager.scrollbarX = scrollbarX
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

    /**
     * The width of the sidebar. This property has no default value and thus must
     * be implemented by the subclass.
     */
    abstract val sidebarWidth: Float

    /**
     * The width of an entry in the sidebar
     */
    open val sidebarEntryWidth: Float get() = sidebarWidth - 30.0f

    /**
     * The x position of the controls
     */
    open val controlsX: Float get() = sidebarWidth

    /**
     * The width of the controls. If no other widgets are on the screen, this value
     * and the [sidebarWidth] should together fill 100% of the gui width.
     */
    abstract val controlsWidth: Float

    /**
     * The x position of the scrollbar
     */
    open val scrollbarX: Float? = null

    /**
     * An image that is shown when no controls section in the sidebar is selected
     */
    open val placeholderImage: ResourceLocation? = null

    /**
     * A text that is shown below the [placeholderImage] when no controls section
     * in the sidebar is selected
     */
    open val placeholderText: String? = null

    /**
     * Returns the entries that are added to the sidebar.
     *
     * This functions return value can change during the lifetime of the gui and
     * the changes are reflected when the [initSidebar] function is called.
     */
    abstract fun produceSidebar(): Collection<SidebarEntry>

    /**
     * Returns the controls for the given selected sidebar [entry]. If this function
     * returns null or an empty list, no controls are shown. This function is evaluated
     * every time a sidebar entry is clicked.
     */
    abstract fun produceControls(entry: SidebarEntry): Collection<ControlElement<*>>?

    /**
     * Called additionally when a sidebar entry is selected. This function is called
     * before [initControls].
     */
    open fun consumeEntry(id: String?, entry: SidebarEntry?) {}

    /**
     * Called when the gui is left using the escape key or the back navigation. Note that
     * it is recommended to use [GuiScreen.onGuiClosed] since this function is more safe.
     */
    open fun onClose() {}
}