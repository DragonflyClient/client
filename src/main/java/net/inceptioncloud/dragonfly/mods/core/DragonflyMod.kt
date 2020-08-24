package net.inceptioncloud.dragonfly.mods.core

import net.inceptioncloud.dragonfly.apps.modmanager.controls.ControlElement
import net.inceptioncloud.dragonfly.options.OptionsBase
import net.minecraft.util.ResourceLocation
import java.io.File

/**
 * The directory in which all mod-specific files are stored.
 */
private val globalModsDirectory = File("dragonfly/mods").also { it.mkdirs() }

/**
 * "Cleans" the string by converting it to lower case and replacing all spaces
 * with dashes.
 */
private fun String.clean() = toLowerCase().replace(Regex("""\W+"""), "-")

/**
 * The base class of any Dragonfly mod.
 *
 * @param name The name of the mod that is used to identify it
 * @param iconResource The resource location of the mod icon (default value uses the [name])
 */
open class DragonflyMod(
    val name: String,
    val iconResource: ResourceLocation = ResourceLocation("dragonflyres/icons/mods/${name.clean()}.png")
) {

    /**
     * The [name] of the mod in a [clean][String.clean] format.
     */
    val cleanName = name.clean()

    /**
     * The directory inside the [globalModsDirectory] that is dedicated to this mod. Contains
     * the [optionsBase] file and potentially more configuration or assets files.
     */
    val directory = File(globalModsDirectory, cleanName).also { it.mkdirs() }

    /**
     * The options base instance created for this mod in which its settings are saved. The file
     * is named like the [directory] and is placed inside of it.
     */
    val optionsBase = OptionsBase(File(directory, "${cleanName}.json"))

    /**
     * Publishes the [controls][ControlElement] for this mod.
     */
    open fun publishControls(): List<ControlElement<*>> = listOf()

    /**
     * Creates a new [OptionDelegate] instance that allows creating options for the mod simply
     * by delegating properties.
     */
    protected fun <T> option(validator: (T) -> Boolean = { true }, defaultValue: () -> T): OptionDelegate<T> {
        return OptionDelegate(validator, defaultValue, optionsBase)
    }
}