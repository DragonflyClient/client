package net.inceptioncloud.dragonfly.engine.inspector

import javafx.animation.Transition
import javafx.application.Platform
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ChangeListener
import javafx.scene.control.*
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


class InspectorView : View("My View") {

    val selectedWidgetProperty = SimpleObjectProperty<Any>()
    private val highlightSelectedWidgetProperty = SimpleBooleanProperty(true)

    var inspectedWidget: Widget<*>? = null

    lateinit var treeView: TreeView<Any>
    lateinit var inspectForm: ScrollPane
    lateinit var menuBar: MenuBar

    private val propertyListeners = mutableMapOf<SimpleObjectProperty<out Any>, MutableList<ChangeListener<in Any>>>()
    private val changeFocusColor: Color = c("#2ecc71")

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
                item("Remove selected from parent", "Ctrl+D").action {
                    val pair = getSelectedWidget() ?: return@action
                    val (id, widget) = pair

                    if (widget.parentAssembled != null) {
                        widget.parentAssembled?.structure?.remove(id, widget)
                        LogManager.getLogger().info("Removed $id from parent assembled ${widget.parentAssembled}")
                    } else if(widget.parentStage != null) {
                        widget.parentStage?.remove(id)
                        LogManager.getLogger().info("Removed $id from parent stage ${widget.parentStage}")
                    }
                }
            }
        }
        treeView = treeview {
            root = TreeItem("Buffer Hierarchy")
            root.isExpanded = true

            onUserSelect { selectedValue ->
                clearPropertyListeners()

                inspectedWidget?.isInspected = false
                inspectedWidget = null

                if (highlightSelectedWidgetProperty.value) {
                    val pair = selectedValue as? Pair<*, *> ?: return@onUserSelect
                    inspectedWidget = pair.second as Widget<*>
                    inspectedWidget?.isInspected = true
                }
            }
            bindSelected(this@InspectorView.selectedWidgetProperty)

            populate(childFactory = buildChildFactory(this))

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
                selectedWidgetProperty.addListener { _, oldValue, newValue ->
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

    private fun clearPropertyListeners() {
        propertyListeners.forEach { (prop, listeners) ->
            listeners.forEach {
                prop.removeListener(it)
            }
        }
        propertyListeners.clear()
    }

    fun repopulate() {
        treeView.root.children.clear()
        treeView.populate(childFactory = buildChildFactory(treeView))
    }

    private fun buildChildFactory(view: TreeView<*>): (TreeItem<Any>) -> Iterable<Any>? {
        return { treeItem ->
            when (val value = treeItem.value) {
                view.root.value -> getAvailableStages()
                is WidgetStage -> value.observableContent.also { treeItem.isExpanded = true }
                is Pair<*, *> -> (value.second as? AssembledWidget<*>)?.structure?.toList()
                else -> null
            }
        }
    }

    private fun getAvailableStages() = listOfNotNull(ScreenOverlay.stage, Minecraft.getMinecraft().currentScreen?.stage)

    private fun Fieldset.forSupertype(widget: Widget<*>, structure: KClass<*>): List<String> {
        return if (structure.isInstance(widget)) {
            val targetProperties = structure.declaredMemberProperties
            val propertyNames = mutableListOf<String>()

            fieldset(structure.simpleName!!.removePrefix("I")) {
                for (property in targetProperties) {
                    property.isAccessible = true
                    propertyNames.add(property.name)

                    createFieldForProperty(widget, property)
                }
            }

            propertyNames
        } else listOf()
    }

    private fun Fieldset.createFieldForProperty(widget: Widget<*>, prop: KProperty<*>) {
        val objectProperty = widget.propertyDelegates[prop.name]?.objectProperty

        if (objectProperty != null) {
            val field = field(prop.name)
            val text = field.text(objectProperty.get().toString())
            val changeListener = ChangeListener<Any?> { _, oldValue, newValue ->
                if (oldValue != newValue) {
                    Platform.runLater {
                        text.text = newValue.toString()
                        if (field.background != changeFocusColor.asBackground()) {
                            field.background = changeFocusColor.asBackground()

                            object : Transition() {
                                init {
                                    cycleDuration = 0.5.seconds
                                }

                                override fun interpolate(fracIn: Double) {
                                    val frac = (1.0 - fracIn)
                                    field.background = Color.color(
                                        changeFocusColor.red,
                                        changeFocusColor.green,
                                        changeFocusColor.blue,
                                        changeFocusColor.opacity * frac
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
            field(prop.name).text(prop.getter.call(widget).toString() + "*")
        }
    }

    private fun List<String>.addTo(list: MutableList<String>) = list.addAll(this)

    private fun getSelectedWidget(): Pair<String, Widget<*>>? {
        val value = selectedWidgetProperty.value ?: return null
        @Suppress("UNCHECKED_CAST")
        return value as? Pair<String, Widget<*>>
    }
}