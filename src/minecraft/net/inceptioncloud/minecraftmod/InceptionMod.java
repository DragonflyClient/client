package net.inceptioncloud.minecraftmod;

import com.google.common.collect.Lists;
import lombok.Getter;
import net.inceptioncloud.minecraftmod.design.font.FontManager;
import net.inceptioncloud.minecraftmod.design.splash.ModSplashScreen;
import net.inceptioncloud.minecraftmod.discord.RichPresenceManager;
import net.inceptioncloud.minecraftmod.event.ModEventBus;
import net.inceptioncloud.minecraftmod.event.client.ClientShutdownEvent;
import net.inceptioncloud.minecraftmod.impl.Tickable;
import net.inceptioncloud.minecraftmod.options.Options;
import net.inceptioncloud.minecraftmod.state.GameStateManager;
import net.inceptioncloud.minecraftmod.transition.Transition;
import net.inceptioncloud.minecraftmod.version.InceptionCloudVersion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

import java.util.*;

/**
 * The main class of the Inception Cloud Minecraft Mod.
 */
public class InceptionMod
{
    /**
     * The Logger used to log InceptionMod messages.
     */
    @Getter
    private static final Logger logger = LogManager.getLogger();

    /**
     * The Minecraft Mod instance.
     */
    @Getter
    private static InceptionMod instance;

    @Getter
    private final GameStateManager gameStateManager;

    @Getter
    private final RichPresenceManager richPresenceManager;

    @Getter
    private final ModEventBus eventBus;

    @Getter
    private final FontManager fontDesign;

    @Getter
    private final ModSplashScreen splashScreen;

    @Getter
    private final Options options;

    /**
     * All transitions handled by the mod.
     */
    @Getter
    private final List<Transition> transitions = Lists.newArrayList();

    /**
     * All classes that implement the tickable interface.
     */
    @Getter
    private final List<Tickable> tickables = Lists.newArrayList();

    /**
     * If a tickable has an associated class, it is stored in here so it can be replaced.
     */
    private final Map<Class<?>, Tickable> associatedTickables = new HashMap<>();

    /**
     * The {@link Timer} that performs the mod ticks.
     */
    private final Timer tickTimer;

    /**
     * The last amount of mod ticks per second.
     */
    @Getter
    private int lastTPS = 0;

    /**
     * The amount of ticks that have been executed in the current second.
     */
    private int ticks = 0;

    /**
     * When the first tick of the second was recorded. Used for calculating reasons.
     */
    private long firstTick = 0;

    /**
     * Minecraft Mod Constructor.
     * <p>
     * Called when loading the Minecraft client.
     */
    public InceptionMod ()
    {
        Display.setTitle(InceptionCloudVersion.FULL_VERSION + " | Minecraft Mod 1.8.8");

        instance = this;
        eventBus = new ModEventBus();
        options = new Options();
        fontDesign = new FontManager();
        splashScreen = new ModSplashScreen();
        richPresenceManager = new RichPresenceManager();
        gameStateManager = new GameStateManager();

        tickTimer = new Timer();
        tickTimer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run ()
            {
                try {
                    tick();
                    recordTick();
                } catch (Exception exception) {
                    LogManager.getLogger().error("Inception Cloud Mod Tick failed!");
                    exception.printStackTrace();
                }
            }
        }, 0, 5);

        Runtime.getRuntime().addShutdownHook(new Thread(() ->
        {
            // EVENTBUS - ClientShutdownEvent when the game is being closed
            ClientShutdownEvent event = new ClientShutdownEvent();
            InceptionMod.getInstance().getEventBus().post(event);
        }));
    }

    /**
     * Creates the {@link InceptionMod} instance.
     */
    public static void create ()
    {
        if (instance != null)
            instance.shutdownInstance();

        new InceptionMod();
    }

    /**
     * Used to shut down the current InceptionCloud Minecraft Mod Instance.
     */
    public void shutdownInstance ()
    {
        LogManager.getLogger().info("Shutting down InceptionCloud Minecraft Mod Instance...");
        tickTimer.cancel();
    }

    /**
     * Perform the mod tick.
     */
    private void tick ()
    {
        new ArrayList<>(transitions).forEach(Transition::tick);
        new ArrayList<>(tickables).forEach(Tickable::modTick);
    }

    /**
     * Record the procedure of the tick for the debug screen.
     */
    private void recordTick ()
    {
        ticks++;

        if (firstTick == 0)
            firstTick = System.currentTimeMillis();
        else if (System.currentTimeMillis() - firstTick >= 1000) {
            lastTPS = ticks;
            firstTick = 0;
            ticks = 0;
        }
    }

    /**
     * Add a client transition to handle.
     *
     * @param target The target transition
     */
    public void handleTransition (Transition target)
    {
        transitions.add(target);
    }

    /**
     * Removes the target transition from the handler list.
     *
     * @param target The target transition
     */
    public void stopTransition (Transition target)
    {
        if (!transitions.remove(target))
            LogManager.getLogger().error("Could not stop " + target.getClass().getSimpleName() + " from " + target.getOriginClass() + "! (not running)");
    }

    /**
     * Add a tickable interface to handle.
     *
     * @param tickable        The implementing class
     * @param associatedClass The class that this tickable is connected with if it should be replaced
     */
    public void handleTickable (Tickable tickable, Class<?> associatedClass)
    {
        if (associatedClass != null && associatedTickables.containsKey(associatedClass)) {
            Tickable previous = associatedTickables.get(associatedClass);
            tickables.remove(previous);
            LogManager.getLogger().info("Replaced previous Tickable " + previous.getClass().getSimpleName());
        }

        LogManager.getLogger().info("Mod is now handling Tickable " + tickable.getClass().getSimpleName());
        tickables.add(tickable);

        if (associatedClass != null)
            associatedTickables.put(associatedClass, tickable);
    }

    /**
     * Convenient Method
     *
     * @see #handleTickable(Tickable, Class) Original Method
     */
    public void handleTickable (Tickable tickable)
    {
        this.handleTickable(tickable, null);
    }
}
