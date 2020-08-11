package net.inceptioncloud.dragonfly.engine.inspector

import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import net.minecraft.client.gui.GuiScreen
import org.reflections.Reflections
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder
import tornadofx.*

class GuiSelectionView : View("Dragonfly Inspector | GUI Selection") {

    val reflections = Reflections(ConfigurationBuilder().setUrls(
        ClasspathHelper.forPackage("net.inceptioncloud.dragonfly") + ClasspathHelper.forPackage("net.minecraft.client.gui")
    ))

    var classes = reflections.getSubTypesOf(GuiScreen::class.java)
    var treeView: TreeView<Any> by singleAssign()

    override val root = treeview<Any> {
        val packages = classes.map { it.`package`.name }.distinct()
        treeView = this

        root = TreeItem("${classes.size} available gui screens")
        root.isExpanded = true
        isShowRoot = false

        populate { parent ->
            when {
                parent == root -> {
                    packages
                        .map {
                            val shortName = it.split(".")[0]
                            PackageNode(shortName, shortName, 0)
                        }
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
                        }.distinctBy { it.shortName }
                    val childClasses = classes
                        .filter { it.`package`.name == packageNode.fullName }
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
    }
}

class PackageNode(val shortName: String, val fullName: String, val splitIndex: Int)
class ClassNode(val clazz: Class<*>)
