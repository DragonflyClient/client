package net.inceptioncloud.minecraftmod;

import net.inceptioncloud.minecraftmod.design.font.FontManager;
import net.inceptioncloud.minecraftmod.design.splash.ModSplashScreen;
import net.inceptioncloud.minecraftmod.discord.RichPresenceManager;
import net.inceptioncloud.minecraftmod.engine.internal.Dynamic;
import net.inceptioncloud.minecraftmod.event.ModEventBus;
import net.inceptioncloud.minecraftmod.event.client.ClientShutdownEvent;
import net.inceptioncloud.minecraftmod.impl.Tickable;
import net.inceptioncloud.minecraftmod.options.Options;
import net.inceptioncloud.minecraftmod.options.sections.*;
import net.inceptioncloud.minecraftmod.state.GameStateManager;
import net.inceptioncloud.minecraftmod.transition.Transition;
import net.inceptioncloud.minecraftmod.version.InceptionCloudVersion;
import net.minecraft.client.Minecraft;
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
    private static final Logger logger = LogManager.getLogger();

    /**
     * The Minecraft Mod instance.
     */
    private static InceptionMod instance;

    @Dynamic
    private final GameStateManager gameStateManager;

    @Dynamic
    private final RichPresenceManager richPresenceManager;

    @Dynamic
    private final ModEventBus eventBus;

    @Dynamic
    private final FontManager fontDesign;

    @Dynamic
    private final ModSplashScreen splashScreen;

    @Dynamic
    private final Options options;

    /**
     * All transitions handled by the mod.
     */
    private final List<Transition> transitions = Collections.synchronizedList(new ArrayList<>());

    /**
     * All classes that implement the tickable interface.
     */
    private final List<Tickable> tickables = Collections.synchronizedList(new ArrayList<>());

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

        OptionsSectionClient.init();
        OptionsSectionUI.init();
        OptionsSectionScoreboard.init();
        OptionsSectionZoom.init();

        tickTimer = new Timer("Minecraft Mod Tick Timer");
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

    public static Logger getLogger ()
    {
        return logger;
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

    /**
     * Reloads the Minecraft Mod.
     */
    public void reload ()
    {
        InceptionMod.getInstance().getOptions().contentSave();
        InceptionMod.getInstance().getFontDesign().clearCache();
    }

    /**
     * @return the current Minecraft Mod version
     */
    public static String getVersion ()
    {
        return "1.0.1.0-alpha";
    }

    /*====          Getters          ====*/

    public static InceptionMod getInstance ()
    {
        return instance;
    }

    public GameStateManager getGameStateManager ()
    {
        return gameStateManager;
    }

    public RichPresenceManager getRichPresenceManager ()
    {
        return richPresenceManager;
    }

    public ModEventBus getEventBus ()
    {
        return eventBus;
    }

    public FontManager getFontDesign ()
    {
        return fontDesign;
    }

    public ModSplashScreen getSplashScreen ()
    {
        return splashScreen;
    }

    public Options getOptions ()
    {
        return options;
    }

    public List<Transition> getTransitions ()
    {
        return transitions;
    }

    public List<Tickable> getTickables ()
    {
        return tickables;
    }

    public int getLastTPS ()
    {
        return lastTPS;
    }

    /**
     * Perform the mod tick.
     */
    private void tick ()
    {
        synchronized (this) {
            new ArrayList<>(transitions).forEach(Transition::tick);
            new ArrayList<>(tickables).forEach(Tickable::modTick);

            if (Minecraft.getMinecraft().currentScreen != null)
                Minecraft.getMinecraft().currentScreen.buffer.updateBuffer();
        }
    }
}
