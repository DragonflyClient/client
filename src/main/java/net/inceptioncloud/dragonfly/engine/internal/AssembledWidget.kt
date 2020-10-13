package net.inceptioncloud.dragonfly.engine.internal

import org.apache.logging.log4j.LogManager
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

typealias Structure = MutableMap<String, Widget<*>>

/**
 * ## Assembled Widget
 *
 * An assembled widget is a widget that is based on the base of multiple other primitive or assembled
 * widgets. It has the same features but has more potential when it comes to designing complex UIs.
 */
abstract class AssembledWidget<W : AssembledWidget<W>>(
    initializerBlock: (W.() -> Unit)? = null
) : Widget<W>(initializerBlock) {

    private var structureField: Structure? = null

    /**
     * Contains the base structure which the widget is assembled with.
     * The key is the identifier of the widget.
     */
    var structure: Structure
        set(value) {
            structureField = value
        }
        get() {
            if (structureField == null) {
                reassemble()
            }

            return structureField!!
        }

    override var isModal: Boolean = false
        set(value) {
            structure.forEach { it.value.isModal = value }
            field = value
        }

    /**
     * Whether the assembled widget has been initialized by calling the first structure update.
     *
     * If this value is still set to false before a widget is rendered, a structure update
     * ([updateStructure]) will be performed before the rendering process is started.
     */
    protected var initialized = false

    override fun stateChanged() {
        structure.forEach { it.value.notifyStateChanged() }
        runStructureUpdate()
    }

    override fun update() {
        structure.values.toList().forEach { it.update() }
        super.update()
    }

    override fun render() {
        if (!initialized) {
            runStructureUpdate()
            initialized = true
        }

        structure.values.filter { it.isVisible }.forEach { it.draw() }
    }

    override fun handleMouseMove(data: MouseData) = Defaults.handleMouseMove(structure.values, data)

    override fun handleMousePress(data: MouseData) {
        super.handleMousePress(data)
        structure.values.forEach { it.handleMousePress(data) }
    }

    override fun handleMouseRelease(data: MouseData) {
        super.handleMouseRelease(data)
        structure.values.forEach { it.handleMouseRelease(data) }
    }

    /**
     * Assembles the widget and saves it in the [structure] variable.
     */
    fun reassemble() {
        structure = assemble().toMutableMap().also {
            it.forEach { (id, widget) ->
                widget.widgetId = id
                widget.parentAssembled = this
            }
        }
    }

    /**
     * Calls the [updateStructure] function while taking care of setting the [isInStateUpdate] boolean.
     */
    fun runStructureUpdate() {
        isInStateUpdate = true
        try {
            updateStructure()
        } finally {
            isInStateUpdate = false
        }
    }

    /**
     * Tries to get a widget and additionally cast it to the specified type. This will return
     * null if the widget was not found or cannot be cast.
     */
    @Suppress("UNCHECKED_CAST")
    fun <W : Widget<W>> getWidget(identifier: String): W? = structure[identifier] as? W

    /**
     * Updates the widget found by the [identifier] (via [getWidget]) and applies the given
     * [block] to it.
     */
    fun <W : Widget<W>> updateWidget(identifier: String, block: (W.() -> Unit)?): W? {
        val widget = getWidget<W>(identifier)
        block?.let { widget?.apply(it) }
        return widget
    }

    fun inherit(identifier: String) {
        val that = structure[identifier] ?: return

        that::class.memberProperties
            .forEach { thatProp ->
                val thisProp = this::class.memberProperties.find {
                    it.isAccessible && it.name == thatProp.name && it.returnType == thatProp.returnType
                } as? KMutableProperty<*> ?: return

                thisProp.setter.call(this, thatProp.getter.call(that))
                LogManager.getLogger().info("${this::class.simpleName} inherited ${thisProp.name} from ${that::class.simpleName}")
            }
    }

    /**
     * Convenient function for accessing [updateWidget].
     */
    operator fun <W : Widget<W>> String.invoke(block: (W.() -> Unit)? = null): W? = updateWidget(this, block)

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
    protected abstract fun updateStructure()
}