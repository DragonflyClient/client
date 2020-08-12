package net.inceptioncloud.dragonfly.engine.inspector

import net.inceptioncloud.dragonfly.engine.internal.Widget
import org.apache.logging.log4j.LogManager

/**
 * ## Inspector Service
 *
 * The inspector service acts as the interface between the Minecraft client and the inspector.
 * Since the inspector extension is only available to developers and is not distributed in the public
 * JAR file of Dragonfly, these "extension"-classes cannot be accessed using the default Java way.
 * Instead, this class uses reflections to invoke methods on the inspector core class if it is
 * available.
 */
object InspectorService {

    /**
     * The full qualified name of the inspector core class in the extension package.
     */
    private const val className = "net.inceptioncloud.dragonfly.engine.inspector.extension.Inspector"

    /**
     * The class instance of the core inspector class or null if it isn't available.
     */
    private var clazz: Class<*>? = null

    init {
        try {
            clazz = Class.forName(className)
        } catch (e: ClassNotFoundException) {
            LogManager.getLogger().warn("The inspector extension wasn't found in the classpath!")
        }
    }

    /**
     * Calls the `launch()` function on the inspector core class if it is available or prints an
     * error message to the console if it isn't.
     */
    @JvmStatic
    fun launch() {
        clazz?.apply {
            getMethod("launch").invoke(null)
        } ?: LogManager.getLogger().error("No inspector extension available!")
    }

    /**
     * Calls the `stageUpdated()` function on the inspector core class if it is available.
     */
    @JvmStatic
    fun stageUpdated() {
        clazz?.getMethod("stageUpdated")?.invoke(null)
    }

    /**
     * Calls the `platform()` function on the inspector core class if it is available.
     */
    @JvmStatic
    fun platform(block: () -> Unit) {
        clazz?.methods?.first { it.name == "platform" }?.invoke(null, block)
    }
}