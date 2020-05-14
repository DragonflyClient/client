package net.inceptioncloud.minecraftmod.engine.internal

@Suppress("LeakingThis")
abstract class AssembledWidget<Child : AssembledWidget<Child>> : Widget<Child>()
{
    /**
     * Contains the base structure which the widget is assembled with.
     * The key is the identifier of the widget.
     */
    protected val structure: Map<String, Widget<*>>

    protected var initialized = false

    init
    {
        structure = assemble()
    }

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

    override fun stateChanged(new: Widget<*>)
    {
        structure.values.forEach { it.stateChanged(new) }
        updateStructure()
    }

    override fun render()
    {
        if (! initialized)
        {
            updateStructure()
        }

        structure.values.forEach { it.render() }
    }
}