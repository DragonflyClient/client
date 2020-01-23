package net.inceptioncloud.minecraftmod;

import com.google.common.collect.Lists;
import lombok.Getter;
import net.inceptioncloud.minecraftmod.design.font.FontManager;
import net.inceptioncloud.minecraftmod.design.splash.ModSplashScreen;
import net.inceptioncloud.minecraftmod.discord.RichPresenceManager;
import net.inceptioncloud.minecraftmod.event.ModEventBus;
import net.inceptioncloud.minecraftmod.impl.Tickable;
import net.inceptioncloud.minecraftmod.options.OptionKey;
import net.inceptioncloud.minecraftmod.options.Options;
import net.inceptioncloud.minecraftmod.state.GameStateManager;
import net.inceptioncloud.minecraftmod.transition.Transition;
import net.inceptioncloud.minecraftmod.version.InceptionCloudVersion;
import org.apache.logging.log4j.LogManager;
import org.lwjgl.opengl.Display;

import java.util.*;

/**
 * The main class of the Inception Cloud Minecraft Mod.
 */
public class InceptionMod
{
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
    private List<Transition> modTransitions = Lists.newArrayList();

    /**
     * All classes that implement the tickable interface.
     */
    private List<Tickable> tickables = Lists.newArrayList();

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

        new Timer().scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run ()
            {
                try {
                    tick();
                    recordTick();
                } catch (Exception exception) {
                    LogManager.getLogger().error("Inception Cloud Mod Tick failed!", exception);
                }
            }
        }, 0, 5);
    }

    /**
     * Creates the {@link InceptionMod} instance.
     */
    public static void create ()
    {
        if (instance != null)
            throw new UnsupportedOperationException("The Mod has already been created!");

        new InceptionMod();
    }

    /**
     * Perform the mod tick.
     */
    private void tick ()
    {
        new ArrayList<>(modTransitions).forEach(Transition::tick);
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
     * @param transition The transition
     */
    public void handleTransition (Transition transition)
    {
        modTransitions.add(transition);
    }

    /**
     * Add a tickable interface to handle.
     *
     * @param tickable The implementing class
     */
    public void handleTickable (Tickable tickable)
    {
        LogManager.getLogger().info("Mod is now handling Tickable " + tickable.getClass().getSimpleName());
        tickables.add(tickable);
    }
}
