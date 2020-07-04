package net.inceptioncloud.dragonfly.ui.components.list

import net.minecraft.client.Minecraft

/**
 * A type-safe factory for creating a [UIList]. Supports only Kotlin.
 */
class UIListFactory
{
    /**
     * Dimensions Instance
     */
    val dimensions = Dimensions()

    /**
     * Entries Instance
     */
    val entries = Entries()

    /**
     * Companion Object for static access on [uiListFactory].
     */
    companion object
    {
        /**
         * Type-Safe Builder Method.
         */
        @JvmStatic
        fun uiListFactory(init: UIListFactory.() -> Unit): UIList
        {
            val factory = UIListFactory()
            factory.init()
            return factory.finish()
        }
    }

    /**
     * Type-Safe Builder Method.
     */
    fun dimensions (init: Dimensions.() -> Unit): Dimensions
    {
        dimensions.init()
        return dimensions
    }

    /**
     * Type-Safe Builder Method.
     */
    fun entries (init: Entries.() -> Unit): Entries
    {
        entries.init()
        return entries
    }

    /**
     * Contains a list of all [UIListEntry] instances.
     */
    class Entries
    {
        /**
         * List with all entries.
         */
        val list = mutableListOf<UIListEntry>()

        /**
         * Makes it possible to add a [UIListEntry] using + before the initialization.
         */
        operator fun UIListEntry.unaryPlus()
        {
            list.add(this)
        }
    }

    /**
     * Contains information about the dimensions of the list.
     * (width, height, x-location, y-location)
     */
    class Dimensions
    {
        /**
         * Slots Instance
         */
        val slots = Slots()

        var widthIn: Int = 100
        var heightIn: Int = 100
        var xIn: Int = 0
        var yIn: Int = 0

        /**
         * Type-Safe Builder Method.
         */
        fun slots (init: Slots.() -> Unit): Slots
        {
            slots.init()
            return slots
        }

        /**
         * Contains information about the dimensions of the slots.
         * (width, height)
         */
        class Slots
        {
            var widthIn: Int = 100
            var heightIn: Int = 100
        }
    }

    /**
     * Finishes the building of the [UIList] by instantiating it.
     */
    private fun finish(): UIList
    {
        return UIList(Minecraft.getMinecraft(), dimensions.widthIn, dimensions.heightIn, dimensions.xIn, dimensions.yIn, dimensions.slots.heightIn, dimensions.slots.widthIn, entries.list)
    }
}