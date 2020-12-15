package net.inceptioncloud.dragonfly.engine.inspector.extension

import javafx.animation.Transition
import javafx.application.Platform
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ChangeListener
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.structure.*
import net.inceptioncloud.dragonfly.overlay.ScreenOverlay
import net.minecraft.client.Minecraft
import org.apache.logging.log4j.LogManager
import tornadofx.*
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

/**
 * The main view of the [InspectorApp].
 *
 * The view is based on a [BorderPane] layout where the [menuBar] is at the top-position,
 * the [treeView] left and the [inspectForm] in the center.
 *
 * The [GuiSelectorView] is opened as an internal window of this view.
 */
class InspectorView : View("Dragonfly Inspector") {

    /**
     * An observable property representing the currently selected value in the [treeView].
     */
    private val selectedProperty = SimpleObjectProperty<Any>()

    /**
     * An observable property representing the value of menu bar check menu item `Menu Bar >
     * Widget > Highlight selected on stage`.
     */
    private val highlightSelectedWidgetProperty = SimpleBooleanProperty(true)

    /**
     * The widget that is currently being inspected. This property is used to set the [Widget.isInspected]
     * state based on the [selectedProperty] and the [highlightSelectedWidgetProperty].
     */
    private var inspectedWidget: Widget<*>? = null

    /**
     * The tree view that represents the stage- and widget-hierarchy.
     */
    private lateinit var treeView: TreeView<Any>

    /**
     * The form that displays information about the currently [inspected widget][inspectedWidget]
     * which is simultaneously the [selectedProperty] if a widget is currently selected in the
     * [treeView].
     */
    private lateinit var inspectForm: ScrollPane

    /**
     * The menu bar that provides useful actions for the user.
     */
    private lateinit var menuBar: MenuBar

    /**
     * A map representing an observable property on a [Widget] and all property listeners that
     * have been added by the inspector. When the [inspectedWidget] is changed, all previously
     * bound property listeners are unbound using [unbindPropertyListeners].
     */
    private val propertyListeners = mutableMapOf<SimpleObjectProperty<out Any>, MutableList<ChangeListener<in Any>>>()

    /**
     * The color to which the background of a fieldset is changed when its property value changes.
     */
    private val updatePropertyColor: Color = c("#2ecc71")

    init {
        reloadViewsOnFocus()
        reloadStylesheetsOnFocus()

        Inspector.inspectorView = this
    }

    override val root = borderpane {
        menuBar = menubar {
            menu("Widget") {
                checkmenuitem("Highlight selected on stage", selected = highlightSelectedWidgetProperty) {
                    action {
                        if (!isSelected) {
                            inspectedWidget?.isInspected = false
                            inspectedWidget = null
                        }
                    }
                }
                item("Remove from parent", "Ctrl+D").action {
                    val pair = getSelectedWidget() ?: return@action
                    val (id, widget) = pair

                    if (widget.parentAssembled != null) {
                        widget.parentAssembled?.structure?.remove(id, widget)
                        LogManager.getLogger().info("Removed $id from parent assembled ${widget.parentAssembled}")
                    } else if (widget.parentStage != null) {
                        widget.parentStage?.remove(id)
                        LogManager.getLogger().info("Removed $id from parent stage ${widget.parentStage}")
                    }
                }
                item("Toggle visibility", "Ctrl+F").action {
                    val widget = getSelectedWidget()?.second ?: return@action
                    widget.isVisible = !widget.isVisible
                }
            }
            menu("Stage") {
                item("Clear", "Ctrl+G").action { getSelectedStage()?.clear() }
                item("Refresh", "Ctrl+R").action { repopulate() }
            }
            menu("GUI") {
                item("Open gui selector").action { openInternalWindow<GuiSelectorView>() }
            }
        }
        treeView = treeview {
            root = TreeItem("Stage Hierarchy")
            root.isExpanded = true

            onUserSelect { selectedValue ->
                unbindPropertyListeners()

                inspectedWidget?.isInspected = false
                inspectedWidget = null

                if (highlightSelectedWidgetProperty.value) {
                    val pair = selectedValue as? Pair<*, *> ?: return@onUserSelect
                    inspectedWidget = pair.second as Widget<*>
                    inspectedWidget?.isInspected = true
                }
            }
            bindSelected(this@InspectorView.selectedProperty)

            populate(childFactory = getChildFactory(this))

            cellFormat {
                text = when (it) {
                    is String -> it
                    is WidgetStage -> it.name
                    is Pair<*, *> -> it.first as? String
                    else -> null
                }
            }
        }
        inspectForm = scrollpane {
            form {
                selectedProperty.addListener { _, oldValue, newValue ->
                    if (oldValue == newValue)
                        return@addListener

                    this.clear()

                    if (newValue == null || newValue !is Pair<*, *>)
                        return@addListener

                    val id = newValue.first as String
                    val widget = newValue.second as Widget<*>

                    val displayedProperties = mutableListOf<String>()

                    fieldset("Widget Info") {
                        field("Type").text(widget::class.simpleName)
                        field("ID").text(id)
                        field("Class").text(widget::class.qualifiedName)
                        field("Type").text(if (widget is AssembledWidget<*>) "Assembled" else "Primitive")
                        if (widget is AssembledWidget<*>) {
                            field("Children").text(widget.structure.size.toString())
                        }
                    }
                    fieldset("Properties") {
                        forSupertype(widget, IPosition::class).addTo(displayedProperties)
                        forSupertype(widget, IDimension::class).addTo(displayedProperties)
                        forSupertype(widget, ISize::class).addTo(displayedProperties)
                        forSupertype(widget, IColor::class).addTo(displayedProperties)
                        forSupertype(widget, IOutline::class).addTo(displayedProperties)
                        forSupertype(widget, IAlign::class).addTo(displayedProperties)
                        forSupertype(widget, Widget::class).addTo(displayedProperties)

                        val customProperties = widget::class.memberProperties
                            .filter { it.name !in displayedProperties }

                        if (customProperties.isNotEmpty()) {
                            fieldset("Custom") {
                                customProperties.forEach {
                                    it.isAccessible = true
                                    createFieldForProperty(widget, it)
                                }
                            }
                        }
                    }
                }
            }
        }

        top = menuBar
        left = treeView
        center = inspectForm
    }

    /**
     * Re-populates (refreshes) the entries in the [treeView] to represent changes to the stage-
     * and widget-hierarchy.
     */
    fun repopulate() {
        treeView.root.children.clear()
        treeView.populate(childFactory = getChildFactory(treeView))
    }

    /**
     * Unbinds all property listeners added by the inspector when switching the [inspectedWidget].
     * These listeners are stored in [propertyListeners].
     */
    private fun unbindPropertyListeners() {
        propertyListeners.forEach { (prop, listeners) ->
            listeners.forEach {
                prop.removeListener(it)
            }
        }
        propertyListeners.clear()
    }

    /**
     * Creates a child-factory that is used to populate the items in the [treeView]. This function
     * is only used in [repopulate].
     *
     * @param view the tree view that uses this child factory
     */
    private fun getChildFactory(view: TreeView<*>): (TreeItem<Any>) -> Iterable<Any>? {
        return { treeItem ->
            when (val value = treeItem.value) {
                view.root.value -> getAvailableStages()
                is WidgetStage -> value.observableContent.also { treeItem.isExpanded = true }
                is Pair<*, *> -> (value.second as? AssembledWidget<*>)?.structure?.toList()
                else -> null
            }
        }
    }

    /**
     * Returns a list of all available stages. At the time of writing, a stage can be either the
     * screen overlay or an opened gui screen. This function allows the developer to easily add
     * stages to the inspector. All null values are filtered out since the current gui screen
     * could be null and thus there is no stage for it.
     */
    private fun getAvailableStages() = listOfNotNull(
        ScreenOverlay.stage, Minecraft.getMinecraft().currentScreen?.stage, Minecraft.getMinecraft().ingameGUI?.stage
    )

    /**
     * Creates the fieldset for all properties of the [widget] defined in the given [supertype].
     * The fields are created using [createFieldForProperty] and the title of the fieldset is
     * the the name of the [supertype] class with a possible 'I'-prefix removed.
     */
    private fun Fieldset.forSupertype(widget: Widget<*>, supertype: KClass<*>): List<String> {
        return if (supertype.isInstance(widget)) {
            val targetProperties = supertype.declaredMemberProperties
            val propertyNames = mutableListOf<String>()

            fieldset(supertype.simpleName!!.removePrefix("I")) {
                for (property in targetProperties) {
                    property.isAccessible = true
                    propertyNames.add(property.name)

                    createFieldForProperty(widget, property)
                }
            }

            propertyNames
        } else listOf()
    }

    /**
     * Creates a field for the given [property] of the [widget]. If the [property] is an
     * observable property, all required listeners are added and also stored in the [propertyListeners]
     * map to remove them when they are no longer needed. If the [property] is not observable,
     * its value is evaluated once the function is called and the value in the inspector is not updated
     * until the filed is re-created. To indicate this, a '*'-suffix is appended to the property name.
     */
    private fun Fieldset.createFieldForProperty(widget: Widget<*>, property: KProperty<*>) {
        val objectProperty = widget.propertyDelegates[property.name]?.objectProperty

        if (objectProperty != null) {
            val field = field(property.name)
            val text = field.text(objectProperty.get().toString())
            val changeListener = ChangeListener<Any?> { _, oldValue, newValue ->
                if (oldValue != newValue) {
                    Platform.runLater {
                        text.text = newValue.toString()
                        if (field.background != updatePropertyColor.asBackground()) {
                            field.background = updatePropertyColor.asBackground()

                            object : Transition() {
                                init {
                                    cycleDuration = 0.5.seconds
                                }

                                override fun interpolate(fracIn: Double) {
                                    val frac = (1.0 - fracIn)
                                    field.background = Color.color(
                                        updatePropertyColor.red,
                                        updatePropertyColor.green,
                                        updatePropertyColor.blue,
                                        updatePropertyColor.opacity * frac
                                    ).asBackground()
                                }
                            }.play()
                        }
                    }
                }
            }

            objectProperty.addListener(changeListener)

            val listeners = propertyListeners.getOrDefault(objectProperty, mutableListOf())
            listeners.add(changeListener)
            propertyListeners.put(objectProperty, listeners)
        } else {
            field(property.name + " *").text(property.getter.call(widget).toString())
        }
    }

    /**
     * A convenient extension function to add the contents of the receiver list to a mutable [list].
     */
    private fun List<String>.addTo(list: MutableList<String>) = list.addAll(this)

    /**
     * A convenient function to get the value of the [selectedProperty] as a pair of [String]
     * and [Widget] which represent the id and the instance of the selected widget. If no widget
     * is selected in the [treeView] this function will return null.
     */
    private fun getSelectedWidget(): Pair<String, Widget<*>>? {
        @Suppress("UNCHECKED_CAST")
        return (selectedProperty.value ?: return null) as? Pair<String, Widget<*>>
    }

    /**
     * A convenient function to get the value of the [selectedProperty] as a [WidgetStage]. If
     * no widget stage is selected in the [treeView] this function will return null.
     */
    private fun getSelectedStage(): WidgetStage? {
        return (selectedProperty.value ?: return null) as? WidgetStage
    }
}