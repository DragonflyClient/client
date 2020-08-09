package net.inceptioncloud.dragonfly.engine.inspector

import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.TreeItem
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.structure.*
import net.inceptioncloud.dragonfly.overlay.ScreenOverlay
import net.minecraft.client.Minecraft
import tornadofx.*
import kotlin.reflect.KClass
import kotlin.reflect.full.*
import kotlin.reflect.jvm.isAccessible

class InspectorView : View("My View") {
    val selectedProperty = SimpleObjectProperty<Any>()

    init {
        reloadViewsOnFocus()
        reloadStylesheetsOnFocus()
    }

    override val root = borderpane {
        left = treeview<Any> {
            root = TreeItem("Buffer Hierarchy")
            root.isExpanded = true

            populate {
                when (val value = it.value) {
                    root.value -> listOfNotNull(ScreenOverlay.stage, Minecraft.getMinecraft().currentScreen?.buffer)
                    is WidgetStage -> value.content.toList().also { _ -> it.isExpanded = true }
                    is Pair<*, *> -> (value.second as? AssembledWidget<*>)?.structure?.toList()
                    else -> null
                }
            }

            bindSelected(this@InspectorView.selectedProperty)

            cellFormat {
                text = when (it) {
                    is String -> it
                    is WidgetStage -> it.name
                    is Pair<*, *> -> it.first as? String
                    else -> null
                }
            }
        }
        center = scrollpane {
            form {
                selectedProperty.addListener { observable, oldValue, newValue ->
                    if (oldValue == newValue)
                        return@addListener

                    this.clear()

                    if (newValue == null || newValue !is Pair<*, *>)
                        return@addListener

                    val id = newValue.first as String
                    val widget = newValue.second as Widget<*>

                    val displayedProperties = mutableListOf<String>()

                    fieldset("Widget Info") {
                        field("Type") {
                            text(widget::class.simpleName)
                        }
                        field("ID") {
                            text(id)
                        }
                        field("Class") {
                            text(widget::class.qualifiedName)
                        }
                        field("Type") {
                            text(if (widget is AssembledWidget<*>) "Assembled" else "Primitive")
                        }
                        if (widget is AssembledWidget<*>) {
                            field("Children") {
                                text(widget.structure.size.toString())
                            }
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
                                    val name = it.name
                                    val value = it.getter.call(widget)

                                    field(name) {
                                        text(value?.toString())
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun Fieldset.forSupertype(widget: Widget<*>, structure: KClass<*>): List<String> {
        return if (structure.isInstance(widget)) {
            val targetProperties = structure.declaredMemberProperties
            val propertyNames = mutableListOf<String>()

            fieldset(structure.simpleName!!.removePrefix("I")) {
                for (property in targetProperties) {
                    property.isAccessible = true
                    propertyNames.add(property.name)

                    field(property.name).text(property.getter.call(widget).toString())
                }
            }

            propertyNames
        } else listOf()
    }

    private fun List<String>.addTo(list: MutableList<String>) = list.addAll(this)
}
