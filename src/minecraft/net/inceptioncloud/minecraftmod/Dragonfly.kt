package net.inceptioncloud.minecraftmod

import net.inceptioncloud.minecraftmod.design.splash.ModSplashScreen
import net.inceptioncloud.minecraftmod.discord.RichPresenceManager
import net.inceptioncloud.minecraftmod.engine.font.FontManager
import net.inceptioncloud.minecraftmod.event.ModEventBus
import net.inceptioncloud.minecraftmod.event.client.ClientShutdownEvent
import net.inceptioncloud.minecraftmod.impl.Tickable
import net.inceptioncloud.minecraftmod.options.Options
import net.inceptioncloud.minecraftmod.options.OptionsManager
import net.inceptioncloud.minecraftmod.state.GameStateManager
import net.inceptioncloud.minecraftmod.transition.Transition
import net.inceptioncloud.minecraftmod.version.InceptionCloudVersion
import net.minecraft.client.Minecraft
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.lwjgl.opengl.Display
import java.util.*

/**
 * The main class of the Inception Cloud Dragonfly Modification.
 */
object Dragonfly {
    /**
     * The current Dragonfly version
     */
    const val version: String = "1.0.2.3-eap"

    @JvmStatic
    val gameStateManager: GameStateManager

    @JvmStatic
    val richPresenceManager: RichPresenceManager

    @JvmStatic
    val eventBus: ModEventBus

    @JvmStatic
    val fontDesign: FontManager

    @JvmStatic
    val splashScreen: ModSplashScreen

    @JvmStatic
    val logger: Logger = LogManager.getLogger()

    /**
     * All transitions handled by the mod.
     */
    @JvmStatic
    val transitions: MutableList<Transition> = Collections.synchronizedList(ArrayList())

    /**
     * The last amount of mod ticks per second.
     */
    @JvmStatic
    var lastTPS = 0

    /**
     * Whether the developer mode is currently enabled.
     * It provides several features for developers or users to find, identify and fix bugs.
     */
    @JvmStatic
    var isDeveloperMode = false

    /**
     * The [Timer] that performs the mod ticks.
     */
    private val tickTimer: Timer

    /**
     * All classes that implement the tickable interface.
     */
    private val tickables = Collections.synchronizedList(ArrayList<Tickable>())

    /**
     * If a tickable has an associated class, it is stored in here so it can be replaced.
     */
    private val associatedTickables: MutableMap<Class<*>, Tickable> = HashMap()

    /**
     * The amount of ticks that have been executed in the current second.
     */
    private var ticks = 0

    /**
     * When the first tick of the second was recorded. Used for calculating reasons.
     */
    private var firstTick: Long = 0

    /**
     * Dragonfly Initializer Block
     *
     * Called when loading the Minecraft client.
     */
    init {
        Display.setTitle("${InceptionCloudVersion.FULL_VERSION} | Dragonfly $version for MC-1.8.8")

        eventBus = ModEventBus()
        OptionsManager.loadOptions()

        fontDesign = FontManager()
        splashScreen = ModSplashScreen()
        richPresenceManager = RichPresenceManager()
        gameStateManager = GameStateManager()
        tickTimer = Timer("Dragonfly Tick Timer")
        tickTimer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                try {
                    tick()
                    recordTick()
                } catch (exception: Exception) {
                    LogManager.getLogger().error("Dragonfly tick failed!")
                    exception.printStackTrace()
                }
            }
        }, 0, 5)

        Runtime.getRuntime().addShutdownHook(Thread(Runnable {
            // EVENTBUS - ClientShutdownEvent when the game is being closed
            val event = ClientShutdownEvent()
            eventBus.post(event)
        }))
    }

    /**
     * Used to shut down the current Dragonfly Instance.
     */
    @JvmStatic
    fun shutdownInstance() {
        LogManager.getLogger().info("Shutting down Dragonfly instance...")
        tickTimer.cancel()
    }

    /**
     * Reloads Dragonfly.
     */
    @JvmStatic
    fun reload() {
        Options.contentSave()
        fontDesign.clearCache()
    }

    /**
     * Record the procedure of the tick for the debug screen.
     */
    private fun recordTick() {
        ticks++
        if (firstTick == 0L) firstTick =
            System.currentTimeMillis() else if (System.currentTimeMillis() - firstTick >= 1000) {
            lastTPS = ticks
            firstTick = 0
            ticks = 0
        }
    }

    /**
     * Add a client transition to handle.
     *
     * @param target The target transition
     */
    @JvmStatic
    fun handleTransition(target: Transition) {
        transitions.add(target)
    }

    /**
     * Removes the target transition from the handler list.
     *
     * @param target The target transition
     */
    @JvmStatic
    fun stopTransition(target: Transition) = transitions.remove(target)

    /**
     * Add a tickable interface to handle.
     *
     * @param tickable        The implementing class
     * @param associatedClass The class that this tickable is connected with if it should be replaced
     */
    @JvmOverloads
    @JvmStatic
    fun handleTickable(tickable: Tickable, associatedClass: Class<*>? = null) {
        if (associatedClass != null && associatedTickables.containsKey(associatedClass)) {
            val previous = associatedTickables[associatedClass]
            tickables.remove(previous)
            LogManager.getLogger().info("Replaced previous Tickable " + previous!!.javaClass.simpleName)
        }

        LogManager.getLogger().info("Mod is now handling Tickable " + tickable.javaClass.simpleName)
        tickables.add(tickable)
        if (associatedClass != null) associatedTickables[associatedClass] = tickable
    }

    /**
     * Perform the mod tick.
     */
    private fun tick() {
        synchronized(this) {
            transitions.toTypedArray().forEach { it.tick() }
            tickables.toTypedArray().forEach { it!!.modTick() }
            if (Minecraft.getMinecraft().currentScreen != null) Minecraft.getMinecraft().currentScreen.buffer.update()
        }
    }
}