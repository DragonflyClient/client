package net.inceptioncloud.minecraftmod.options.sections

import net.inceptioncloud.minecraftmod.options.entries.OptionEntry
import net.inceptioncloud.minecraftmod.options.entries.TitleEntry
import net.inceptioncloud.minecraftmod.options.sections.OptionList.all
import net.inceptioncloud.minecraftmod.options.sections.OptionSectionFactory.Companion.optionSection
import net.inceptioncloud.minecraftmod.ui.ModOptionsUI

/**
 * A static accessible object that keeps all created option sections.
 *
 * When an [OptionSection] is created by the [OptionSectionFactory], it will automatically
 * be added to the [list][all]. The options ui accesses that list and renders the content.
 *
 * @see net.inceptioncloud.minecraftmod.ui.ModOptionsUI
 */
object OptionList
{
    /**
     * This is the list where all [Option Sections][OptionSection] are kept in.
     * The adding process is automatically done by the [factory][OptionSectionFactory].
     */
    val all = mutableListOf<OptionSection>()
}

/**
 * A list of [Option Entries][OptionEntry] collected under a title.
 *
 * These sections are displayed in the options menu and start with a title under which all
 * option entries are rendered.
 *
 * @see ModOptionsUI
 * @see TitleEntry
 *
 * @property title the title of the section that should describe the area to which the options apply
 * @property entries all entries that belong to this area
 */
data class OptionSection(val title: String, val entries: List<OptionEntry<*>>)

/**
 * A factory for creating [Option Sections][OptionSection].
 *
 * To start the creation, call the [optionSection] function and put the content into the parameter function.
 * The [finish] method is called after the init function was executed and the [OptionSection] will be stored in [OptionList.all].
 */
class OptionSectionFactory
{
    /**
     * The title of the section that should describe the area to which the options apply.
     *
     * The options screen will render this in a [TitleEntry] on top of the section.
     */
    var title: String = "Section"

    /**
     * The collection of all option entries that belong to the section.
     *
     * They will be rendered below the [title] in the options screen.
     */
    val list = mutableListOf<OptionEntry<*>>()

    /**
     * A companion object that allows static access on [optionSection].
     */
    companion object
    {
        /**
         * The function that starts the factory.
         *
         * All content belongs in the init function and will be executed automatically. After that, the [finish] function
         * is called and the [OptionSection] is added to the [list][OptionList.all].
         *
         * @param init a function as a parameter that will be executed on the [OptionSectionFactory] receiver
         */
        @JvmStatic
        fun optionSection(init: OptionSectionFactory.() -> Unit)
        {
            val factory = OptionSectionFactory()
            factory.init()

            all.add(factory.finish())
        }
    }

    /**
     * Specifies a function for the unary plus operator so option entries can be easily added.
     *
     * It works as follows:
     * ```
     *  optionSection {
     *      name = ...
     *
     *      + OptionEntry#1
     *      + OptionEntry#2
     *  }
     * ```
     */
    operator fun OptionEntry<*>.unaryPlus()
    {
        list.add(this)
    }

    /**
     * Finishes the factory by creating the [OptionSection].
     *
     * This function is automatically called after the factory processed.
     */
    private fun finish(): OptionSection
    {
        return OptionSection(title, list)
    }
}