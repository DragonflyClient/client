package net.inceptioncloud.minecraftmod.subscriber;

import com.google.common.eventbus.Subscribe;
import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import lombok.SneakyThrows;
import net.inceptioncloud.minecraftmod.event.client.ClientStartupEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.FileReader;
import java.net.Proxy;
import java.util.Scanner;

/**
 * Awaits the {@link ClientStartupEvent} to log in to the Minecraft Account whose credentials
 * are saved in the <code>authentication.txt</code> file.
 */
public class AuthenticationSubscriber
{
    /**
     * {@link ClientStartupEvent} Subscriber
     */
    @Subscribe
    public void clientStartup (ClientStartupEvent event)
    {
        this.authenticateWithFile();
    }

    /**
     * Authenticate with the credentials of the Minecraft Account saved in the file.
     */
    @SneakyThrows
    private void authenticateWithFile ()
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
}
