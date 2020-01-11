package net.inceptioncloud.minecraftmod;

import com.google.common.collect.Lists;
import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import lombok.Getter;
import lombok.SneakyThrows;
import net.inceptioncloud.minecraftmod.impl.Tickable;
import net.inceptioncloud.minecraftmod.render.font.CustomFontRenderer;
import net.inceptioncloud.minecraftmod.transition.Transition;
import net.inceptioncloud.minecraftmod.version.InceptionCloudVersion;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import org.apache.logging.log4j.LogManager;
import org.lwjgl.opengl.Display;

import java.awt.*;
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
     * The custom Inception Cloud Mod font renderer.
     */
    @Getter
    private CustomFontRenderer fontRenderer;

    /**
     * All transitions handled by the mod.
     */
    private List<Transition> modTransitions = Lists.newArrayList();

    /**
     * All classes that implement the tickable interface.
     */
    private List<Tickable> tickables = Lists.newArrayList();

    /**
     * Minecraft Mod Constructor.
     * <p>
     * Called when loading the Minecraft client.
     */
    public MinecraftMod ()
    {
        instance = this;

        Display.setTitle(InceptionCloudVersion.FULL_VERSION + " | Minecraft Mod 1.8.8");

        auth();

        new Timer().schedule(new TimerTask()
        {
            @Override
            public void run ()
            {
                try {
                    Thread.sleep(1);
                    tick();
                } catch (Exception exception) {
                    LogManager.getLogger().error("Inception Cloud Mod Tick failed!", exception);
                }
            }
        }, 0, 1);
    }

    /**
     * Called when the Minecraft client's graphics are initialized.
     */
    public void initializeGraphics ()
    {
        fontRenderer = new CustomFontRenderer("Product Sans Medium", Font.PLAIN, 45);
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
