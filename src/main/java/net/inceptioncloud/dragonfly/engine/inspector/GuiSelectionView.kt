package net.inceptioncloud.dragonfly.engine.inspector

import javafx.scene.control.TreeItem
import net.inceptioncloud.dragonfly.ui.screens.ModOptionsUI
import net.minecraft.client.gui.*
import tornadofx.*

class GuiSelectionView : View("Dragonfly Inspector | GUI Selection") {

    val classes = listOf(GuiMainMenu::class.java, GuiIngameMenu::class.java, ModOptionsUI::class.java)

    override val root = treeview<Any> {
        val packages = classes.map { it.`package`.name }.distinct()

        root = TreeItem("Available GUIs")
        root.isExpanded = true
        isShowRoot = false

        populate { parent ->
            when {
                parent == root -> packages
                    .map { it.split(".")[0] }
                    .distinct()
                    .map { PackageNode(it, it, 0) }
                parent.value is PackageNode -> {
                    val packageNode = parent.value as PackageNode
                    println("--- " + packageNode.fullName)
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
                    val childClasses = classes
                        .filter { it.`package`.name == packageNode.fullName }
                        .map {
                            println("Class: ${it.simpleName}")
                            ClassNode(it)
                        }

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

    class PackageNode(val shortName: String, val fullName: String, val splitIndex: Int)

    class ClassNode(val clazz: Class<*>)
}