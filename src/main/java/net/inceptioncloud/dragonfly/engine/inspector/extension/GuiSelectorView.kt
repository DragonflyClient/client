package net.inceptioncloud.dragonfly.engine.inspector.extension

import javafx.application.Platform
import javafx.scene.control.TreeItem
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiMainMenu
import net.minecraft.client.gui.GuiScreen
import org.apache.logging.log4j.LogManager
import org.reflections.Reflections
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder
import tornadofx.*
import java.lang.reflect.Constructor

/**
 * A view that allows the user to select available gui screens of the client.
 *
 * The view is opened as an [internal window][openInternalWindow] of the [InspectorView]
 * when the user clicks `Gui > Open gui selector` in the menu bar.
 */
class GuiSelectorView : View("GUI selector") {

    /**
     * The instance of `org.reflections` that is used to scan the classpath for subtypes
     * of the [GuiScreen] class.
     */
    private val reflections = Reflections(ConfigurationBuilder().setUrls(
        ClasspathHelper.forPackage("net.inceptioncloud.dragonfly") + ClasspathHelper.forPackage("net.minecraft.client.gui")
    ))

    /**
     * A set of all subtypes of the [GuiScreen] class found using the [reflections].
     */
    private val classes: Set<Class<out GuiScreen>> = reflections.getSubTypesOf(GuiScreen::class.java)

    /**
     * A distinct list of the package's names of the found [classes].
     */
    private val packages = classes.map { it.`package`.name }.distinct()

    override val root = treeview<Any> {
        prefWidth = 400.0
        prefHeight = 600.0

        root = TreeItem("${classes.size} available gui screens")
        root.isExpanded = true

        populate { parent ->
            when {
                parent == root -> {
                    packages
                        .map {
                            val shortName = it.split(".")[0]
                            PackageNode(shortName, shortName, 0)
                        }
                        .sortedBy { it.shortName }
                        .distinctBy { it.shortName }
                }
                parent.value is PackageNode -> {
                    val packageNode = parent.value as PackageNode
                    val childPackages = packages
                        .filter { it.split(".").size > packageNode.splitIndex + 1 && it.startsWith(packageNode.fullName) }
                        .map {
                            val shortName = it.split(".")[packageNode.splitIndex + 1]
                            PackageNode(
                                shortName,
                                packageNode.fullName + "." + shortName,
                                packageNode.splitIndex + 1
                            )
                        }
                        .sortedBy { it.shortName }
                        .distinctBy { it.shortName }
                    val childClasses = classes
                        .filter { it.`package`.name == packageNode.fullName }
                        .sortedBy { it.simpleName }
                        .map { ClassNode(it) }

                    childPackages + childClasses
                }
                else -> listOf()
            }
        }

        cellFormat {
            text = when (it) {
                is String -> it
                is PackageNode -> it.shortName
                is ClassNode -> it.clazz.simpleName
                else -> it.toString()
            }
        }

        onDoubleClick {
            val selected = selectedValue as? ClassNode ?: return@onDoubleClick
            val clazz = selected.clazz

            Minecraft.getMinecraft().addScheduledTask {
                val instance: GuiScreen
                val constructor: Constructor<*>?

                when {
                    clazz.constructors.find { it.parameters.isEmpty() } != null -> {
                        LogManager.getLogger().info("Using no-argument-constructor to instantiate ${clazz.simpleName}")
                        instance = clazz.newInstance() as GuiScreen
                    }
                    clazz.constructors.find {
                        it.parameters.size == 1 && it.parameterTypes.getOrNull(0) == GuiScreen::class.java
                    }.also { constructor = it } != null -> {
                        LogManager.getLogger().info("Using parent-screen-constructor to instantiate ${clazz.simpleName}")
                        instance = constructor!!.newInstance(Minecraft.getMinecraft().currentScreen ?: GuiMainMenu()) as GuiScreen
                    }
                    else -> {
                        LogManager.getLogger().error("Cannot instantiate ${clazz.simpleName}!")
                        Platform.runLater {
                            selectionModel.selectedItem.value = "${clazz.simpleName} (not instantiatable)"
                        }
                        return@addScheduledTask
                    }
                }

                Minecraft.getMinecraft().displayGuiScreen(instance)
            }
        }
    }
}

/**
 * A node that represents a package in the package explorer of the selector.
 *
 * @param shortName The part of the name of the package that is displayed by the node (e.g. "net", "minecraft", "client", ...)
 * @param fullName The full name of the package so far (eg. "net", "net.minecraft", "net.minecraft.client", ...)
 * @param splitIndex The last index of the package name split by dots that was appended to the [fullName] and that is equal to the [shortName]
 */
class PackageNode(val shortName: String, val fullName: String, val splitIndex: Int)

/**
 * A node that represents a class in the package explorer of the selector.
 *
 * @param clazz The class that is represented by this node
 */
class ClassNode(val clazz: Class<*>)
