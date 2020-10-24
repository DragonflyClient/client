package net.inceptioncloud.dragonfly.controls.sidebar

import net.inceptioncloud.dragonfly.design.color.DragonflyPalette.accentNormal
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette.background
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation.Companion.morph
import net.inceptioncloud.dragonfly.engine.contains
import net.inceptioncloud.dragonfly.engine.internal.MouseData
import net.inceptioncloud.dragonfly.engine.scrollbar.Scrollbar
import net.inceptioncloud.dragonfly.engine.scrollbar.attachTo
import net.inceptioncloud.dragonfly.engine.sequence.easing.EaseQuad
import net.inceptioncloud.dragonfly.engine.widgets.assembled.BackNavigation
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Rectangle
import net.minecraft.client.gui.GuiScreen

/**
 * Produces a list of sidebar entries.
 */
typealias EntryFactory = () -> Collection<SidebarEntry>

/**
 * Consumes an entry and its id when it is selected.
 */
typealias EntryConsumer = (id: String?, entry: SidebarEntry?) -> Unit

/**
 * Manages the sidebar for a controls ui screen.
 *
 * @param guiScreen The gui screen for which the sidebar is created
 * @param x The x coordinate of the sidebar
 * @param y The y coordinate of the sidebar
 * @param width The width of the sidebar
 * @param height The height of the sidebar
 * @param entryHeight The height of the individual entries
 * @param entryWidth The width of the individual entries
 * @param entryPadding The vertical distance between the sidebar container and the gaps
 * @param entryGap The vertical gap between the individual entries
 */
class SidebarManager(
    val guiScreen: GuiScreen,
    var x: Float = 0.0f,
    var y: Float = 0.0f,
    var width: Float = 0.0f,
    var height: Float = 0.0f,
    var entryHeight: Float = 0.0f,
    var entryWidth: Float = 0.0f,
    var entryPadding: Float = 0.0f,
    var entryGap: Float = 0.0f
) {
    /**
     * Produces the entries for this sidebar
     */
    private var entryFactory: EntryFactory? = null

    /**
     * Consumes the selected entry for this sidebar
     */
    private var entryConsumer: EntryConsumer? = null

    /**
     * The stage of the [guiScreen] to which the widgets are added
     */
    private val stage = guiScreen.stage

    /**
     * All entries that are shown by this sidebar by the [show] function
     */
    var entries = mutableListOf<SidebarEntry>()

    /**
     * The scrollbar for this sidebar that is activated if it contains too much entries
     */
    val scrollbar = Scrollbar(guiScreen, entryPadding)

    /**
     * The id if the currently selected sidebar entry
     */
    var selected: String? = null
        set(value) {
            field = value
            entryConsumer?.invoke(selected, selectedEntry)
            entries.forEach {
                it.detachAnimation<MorphAnimation>()
                it.morph(
                    30, EaseQuad.IN_OUT,
                    SidebarEntry::color to if (it.widgetId == value) accentNormal else background
                )?.start()
            }
        }

    /**
     * The selected entry widget. This is a convenience property that delegates its getter
     * and setter to the [selected] property.
     */
    var selectedEntry: SidebarEntry?
        get() = entries.firstOrNull { it.widgetId == selected }
        set(value) {
            selected = value?.widgetId
        }

    /**
     * Shows the widget produced by the [entryFactory]. It is recommended to call [reset]
     * before invoking this function.
     */
    fun show() {
        stage.add("sidebar-background" to Rectangle().apply {
            x = this@SidebarManager.x
            y = this@SidebarManager.y
            width = this@SidebarManager.width
            height = this@SidebarManager.height
            color = background
        })

        val producedEntries = entryFactory?.invoke() ?: return
        var currentY = y + entryPadding

        for ((index, entry) in producedEntries.withIndex()) {
            val id = "sidebar-entry-$index"
            val configuredEntry = entry.apply {
                sidebarManager = this@SidebarManager
                width = entryWidth
                height = entryHeight
                x = this@SidebarManager.x + (this@SidebarManager.width / 2.0f) - (width / 2.0f)
                y = currentY
                color = if (this@SidebarManager.selected == id) accentNormal else background
                attachTo(scrollbar)
            }

            stage.add(id to configuredEntry)
            currentY += configuredEntry.height + entryGap
        }

        entries = producedEntries.toMutableList()

        stage.add("sidebar-scrollbar" to scrollbar.prepareWidget().apply {
            width = 5.0f
            x = this@SidebarManager.x + this@SidebarManager.width - width
            y = this@SidebarManager.y
        })
    }

    /**
     * Resets the sidebar manager by removing the sidebar widgets and resetting the scrollbar.
     */
    fun reset() {
        scrollbar.reset()
        stage.remove("sidebar-background")
        entries.forEach { it.widgetId?.let { id -> stage.remove(id) } }
        entries.clear()
    }

    /**
     * Must be called by the gui that uses this sidebar to check which entry is selected.
     */
    fun mouseClicked(data: MouseData) {
        if (data in stage["sidebar-background"] as? Rectangle && data !in stage["back-navigation"] as? BackNavigation) {
            entries.filter { it.isSelectable }.firstOrNull { data in it }?.widgetId?.let { selected = it }
        }
    }

    /**
     * Convenient function to set the [entryFactory]
     */
    fun produceEntries(entryFactory: EntryFactory) {
        this.entryFactory = entryFactory
    }

    /**
     * Convenient function to set the [entryConsumer]
     */
    fun consumeEntry(entryConsumer: EntryConsumer) {
        this.entryConsumer = entryConsumer
    }
}