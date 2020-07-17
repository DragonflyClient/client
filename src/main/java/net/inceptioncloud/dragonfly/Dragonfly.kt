package net.inceptioncloud.dragonfly

import net.inceptioncloud.dragonfly.design.DesignSubscribers
import net.inceptioncloud.dragonfly.design.splash.ModSplashScreen
import net.inceptioncloud.dragonfly.discord.RichPresenceManager
import net.inceptioncloud.dragonfly.engine.font.FontManager
import net.inceptioncloud.dragonfly.event.ModEventBus
import net.inceptioncloud.dragonfly.event.client.ClientShutdownEvent
import net.inceptioncloud.dragonfly.options.Options
import net.inceptioncloud.dragonfly.options.OptionsManager
import net.inceptioncloud.dragonfly.state.GameStateManager
import net.inceptioncloud.dragonfly.subscriber.DefaultSubscribers
import net.inceptioncloud.dragonfly.transition.Transition
import net.inceptioncloud.dragonfly.versioning.DragonflyVersion
import net.minecraft.client.Minecraft
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.lwjgl.opengl.Display
import java.util.*

/**
 * The main class of the Inception Cloud Dragonfly Modification.
 */
object Dragonfly {

    @JvmStatic
    lateinit var gameStateManager: GameStateManager
        private set

    @JvmStatic
    lateinit var richPresenceManager: RichPresenceManager
        private set

    @JvmStatic
    lateinit var fontDesign: FontManager
        private set

    @JvmStatic
    lateinit var splashScreen: ModSplashScreen
        private set

    @JvmStatic
    val eventBus: ModEventBus = ModEventBus()

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
    private lateinit var tickTimer: Timer

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
    @JvmStatic
    fun init() {
        Display.setTitle("Dragonfly ${DragonflyVersion.string} for Minecraft 1.8.8")

        OptionsManager.loadOptions()
        DefaultSubscribers.register(eventBus)
        DesignSubscribers.register(eventBus)

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
     * Perform the mod tick.
     */
    private fun tick() {
        synchronized(this) {
            transitions.toTypedArray().forEach { it.tick() }
            if (Minecraft.getMinecraft().currentScreen != null) Minecraft.getMinecraft().currentScreen.buffer.update()
        }
    }
}