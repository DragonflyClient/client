package net.inceptioncloud.dragonfly.engine.inspector

import javafx.application.Platform
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiMainMenu
import net.minecraft.client.gui.GuiScreen
import org.apache.logging.log4j.LogManager
import org.reflections.Reflections
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder
import tornadofx.*
import java.lang.reflect.Constructor

class GuiSelectionView : View("GUI selector") {

    private val reflections = Reflections(ConfigurationBuilder().setUrls(
        ClasspathHelper.forPackage("net.inceptioncloud.dragonfly") + ClasspathHelper.forPackage("net.minecraft.client.gui")
    ))

    private val classes: Set<Class<out GuiScreen>> = reflections.getSubTypesOf(GuiScreen::class.java)
    private val packages = classes.map { it.`package`.name }.distinct()

    var treeView: TreeView<Any> by singleAssign()

    override val root = treeview<Any> {
        treeView = this

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

class PackageNode(val shortName: String, val fullName: String, val splitIndex: Int)
class ClassNode(val clazz: Class<*>)
