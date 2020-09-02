package net.inceptioncloud.dragonfly

import dev.decobr.mcgeforce.bindings.MCGeForceHelper
import net.inceptioncloud.dragonfly.account.*
import net.inceptioncloud.dragonfly.design.splash.DragonflySplashScreen
import net.inceptioncloud.dragonfly.discord.RichPresenceManager
import net.inceptioncloud.dragonfly.engine.font.FontManager
import net.inceptioncloud.dragonfly.event.*
import net.inceptioncloud.dragonfly.event.client.*
import net.inceptioncloud.dragonfly.apps.settings.DragonflyOptions
import net.inceptioncloud.dragonfly.options.sections.StorageOptions
import net.inceptioncloud.dragonfly.overlay.ScreenOverlay
import net.inceptioncloud.dragonfly.state.GameStateManager
import net.inceptioncloud.dragonfly.subscriber.DefaultSubscribers
import net.inceptioncloud.dragonfly.transition.Transition
import net.inceptioncloud.dragonfly.ui.taskbar.Taskbar
import net.inceptioncloud.dragonfly.versioning.DragonflyVersion
import net.minecraft.client.Minecraft
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.lwjgl.opengl.Display
import java.io.File
import java.nio.file.*
import java.util.*

/*
 *   🎇   🔥300🔥   🎆
 */

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
    lateinit var fontManager: FontManager
        private set

    @JvmStatic
    lateinit var splashScreen: DragonflySplashScreen
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
     * Whether the developer mode is currently enabled.
     * It provides several features for developers or users to find, identify and fix bugs.
     */
    @JvmStatic
    var isDeveloperMode = false

    /**
     * The directory in which secret Dragonfly files are stored.
     */
    @JvmStatic
    val secretsDirectory = File("dragonfly/.secrets").also {
        if(it.mkdirs()) {
            val path: Path = FileSystems.getDefault().getPath("dragonfly/.secrets")
            Files.setAttribute(path, "dos:hidden", true)
        }
    }

    /**
     * The Dragonfly account with which the user is currently authenticated or null if
     * there is no connected Dragonfly account.
     */
    var account: DragonflyAccount? = null

    @JvmStatic
    lateinit var geforceHelper: MCGeForceHelper

    /**
     * Dragonfly Initializer Block
     *
     * Called when loading the Minecraft client.
     */
    @JvmStatic
    fun init() {
        Display.setTitle("Dragonfly ${DragonflyVersion.string} for Minecraft 1.8.8")

        geforceHelper = MCGeForceHelper()

        Taskbar
        DefaultSubscribers.register(eventBus)

        fontManager = FontManager()
        splashScreen = DragonflySplashScreen()
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

        try {
            LogManager.getLogger().info("Checking for authenticated Dragonfly account...")
            val stored = AuthenticationBridge.validateStoredToken()

            stored?.token = AuthenticationBridge.readStoredToken()
            account = stored

            if (stored == null) LogManager.getLogger().info("No Dragonfly account token stored!")
            else LogManager.getLogger().info("Successfully authenticated with Dragonfly")
        } catch (e: Exception) {
            LogManager.getLogger().warn("Failed to authenticate with Dragonfly:")
            e.printStackTrace()
        } finally {
            if (account == null && !StorageOptions.SKIP_LOGIN.get()) {
                AuthenticationBridge.showLoginModal()
            }
        }

        Runtime.getRuntime().addShutdownHook(Thread {
            // EVENTBUS - ClientShutdownEvent when the game is being closed
            val event = ClientShutdownEvent()
            eventBus.post(event)
        })

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
        DragonflyOptions.contentSave()
        fontManager.clearCache()
    }

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
     * The last amount of mod ticks per second.
     */
    @JvmStatic
    var lastTPS = 0

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
            ScreenOverlay.stage.update()
            Minecraft.getMinecraft().ingameGUI?.stage?.update()

            Minecraft.getMinecraft().currentScreen?.stage?.update()

            val tickEvent = ClientTickEvent()
            eventBus.post(tickEvent)
        }
    }
}

/**
 * Convenient access to the Minecraft instance.
 */
val Any.mc: Minecraft
    get() = Minecraft.getMinecraft()