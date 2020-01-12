package net.inceptioncloud.minecraftmod;

import com.google.common.collect.Lists;
import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import lombok.Getter;
import lombok.SneakyThrows;
import net.inceptioncloud.minecraftmod.impl.Tickable;
import net.inceptioncloud.minecraftmod.render.font.FontRendererMaster;
import net.inceptioncloud.minecraftmod.transition.Transition;
import net.inceptioncloud.minecraftmod.version.InceptionCloudVersion;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import org.apache.logging.log4j.LogManager;
import org.lwjgl.opengl.Display;

import java.io.File;
import java.io.FileReader;
import java.net.Proxy;
import java.util.*;
import java.util.List;

/**
 * The main class of the Inception Cloud Minecraft Mod.
 */
public class MinecraftMod
{
    /**
     * The Minecraft Mod instance.
     */
    @Getter
    private static MinecraftMod instance;

    /**
     * The Font Renderer Mapper.
     */
    @Getter
    private final FontRendererMaster fontRendererMaster;

    /**
     * All transitions handled by the mod.
     */
    private List<Transition> modTransitions = Lists.newArrayList();

    /**
     * All classes that implement the tickable interface.
     */
    private List<Tickable> tickables = Lists.newArrayList();

    /**
     * The amount of ticks that have been executed in the current second.
     */
    private int ticks = 0;

    /**
     * When the first tick of the second was recorded. Used for calculating reasons.
     */
    private long firstTick = 0;

    /**
     * The last amount of mod ticks per second.
     */
    @Getter
    private int lastTPS = 0;

    /**
     * Minecraft Mod Constructor.
     * <p>
     * Called when loading the Minecraft client.
     */
    public MinecraftMod ()
    {
        instance = this;
        fontRendererMaster = new FontRendererMaster();

        Display.setTitle(InceptionCloudVersion.FULL_VERSION + " | Minecraft Mod 1.8.8");

        auth();

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
        }, 0, 1);
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
     * Called when the Minecraft client's graphics are initialized.
     */
    public void initializeGraphics ()
    {
    }

    /**
     * Authenticate with the InceptionCloud Minecraft account.
     */
    @SneakyThrows
    private void auth ()
    {
        final File credentials = new File("authetication.txt");
        final Scanner scanner = new Scanner(new FileReader(credentials));

        final String email = scanner.nextLine();
        final String password = scanner.nextLine();

        final YggdrasilUserAuthentication auth = ( YggdrasilUserAuthentication ) new YggdrasilAuthenticationService(Proxy.NO_PROXY, "").createUserAuthentication(Agent.MINECRAFT);

        auth.setUsername(email);
        auth.setPassword(password);

        try {
            auth.logIn();

            Minecraft.getMinecraft().setSession(new Session(auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(), auth.getAuthenticatedToken(), "mojang"));
            LogManager.getLogger().error("Logged in with account " + Minecraft.getMinecraft().getSession().getUsername());
        } catch (AuthenticationException exception) {
            LogManager.getLogger().error("Invalid credentials in authentication file!");
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
        tickables.add(tickable);
    }
}
