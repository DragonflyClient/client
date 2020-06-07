package net.inceptioncloud.minecraftmod.engine.internal

import net.inceptioncloud.minecraftmod.Dragonfly
import net.inceptioncloud.minecraftmod.engine.GraphicsEngine
import net.inceptioncloud.minecraftmod.engine.structure.IPosition
import net.minecraft.client.gui.Gui
import org.lwjgl.input.Keyboard

/**
 * The colors that are used to separate the individual structure widgets when
 * the developer mode is enabled.
 */
private val structureColors = arrayOf(
    0x1abc9c, 0x2ecc71, 0x3498db, 0x9b59b6, 0xf1c40f, 0xe67e22, 0xe74c3c
)

/**
 * ## Assembled Widget
 *
 * An assembled widget is a widget that is based on the base of multiple other primitive or assembled
 * widgets. It has the same features but has more potential when it comes to designing complex UIs.
 */
@Suppress("LeakingThis")
abstract class AssembledWidget<Child : AssembledWidget<Child>> : Widget<Child>() {
    /**
     * Contains the base structure which the widget is assembled with.
     * The key is the identifier of the widget.
     */
    protected val structure: Map<String, Widget<*>>

    /**
     * Whether the assembled widget has been initialized by calling the first structure update.
     *
     * If this value is still set to false before a widget is rendered, a structure update
     * ([updateStructure]) will be performed before the rendering process is started.
     */
    protected var initialized = false

    init {
        structure = assemble().also {
            it.values.forEach { widget -> widget.isInAssembled = true }
        }
    }

    override fun stateChanged(new: Widget<*>) {
        structure.values.forEach { it.stateChanged(new) }
        updateStructure()
    }

    override fun update() {
        structure.values.forEach { it.update() }
        super.update()
    }

    override fun render() {
        if (!initialized) {
            updateStructure()
            initialized = true
        }

        structure.values.filter { it.isVisible }.forEach { it.draw() }

        if (Dragonfly.isDeveloperMode && !isInAssembled && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            var index = 0
            structure.values.forEach { widget ->
                val x = (widget as IPosition).x
                val y = (widget as IPosition).y
                val (width, height) = Defaults.getSizeOrDimension(widget)

                Gui.drawRect(
                    x, y, x + width, y + height,
                    WidgetColor(structureColors[index % (structureColors.size)]).apply { alpha = 200 }.rgb
                )

                index++
            }
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && !this.isInAssembled) {
            GraphicsEngine.renderDebugOverlay(structure)
        }
    }

    override fun handleMouseMove(data: MouseData) = Defaults.handleMouseMove(structure.values, data)

    /**
     * Assembles the widget by initializing the base widgets.
     *
     * Returns all widgets mapped to a string identifier so they can be accessed in the
     * [updateStructure] function.
     */
    abstract fun assemble(): Map<String, Widget<*>>

    /**
     * Updates the structure of the assembled widget.
     */
    abstract fun updateStructure()
}