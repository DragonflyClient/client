package net.inceptioncloud.dragonfly.subscriber;

import com.google.common.eventbus.Subscribe;
import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.*;
import net.inceptioncloud.dragonfly.event.client.ClientStartupEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import org.apache.logging.log4j.LogManager;

import java.io.*;
import java.net.Proxy;
import java.util.Scanner;

/**
 * Awaits the {@link ClientStartupEvent} to log in to the Minecraft Account whose credentials
 * are saved in the <code>authentication.txt</code> file.
 */
public class AuthenticationSubscriber {
    /**
     * {@link ClientStartupEvent} Subscriber
     */
    @Subscribe
    public void clientStartup(ClientStartupEvent event) {
        this.authenticateWithFile();
    }

    /**
     * Authenticate with the credentials of the Minecraft Account saved in the file.
     */
    private void authenticateWithFile() {
        try {
            final File credentials = new File("authentication.txt");

            if (!credentials.exists())
                return;

            final Scanner scanner = new Scanner(new FileReader(credentials));

            final String email = scanner.nextLine();
            final String password = scanner.nextLine();

            final YggdrasilUserAuthentication auth =
                    (YggdrasilUserAuthentication) new YggdrasilAuthenticationService(Proxy.NO_PROXY, "").createUserAuthentication(Agent.MINECRAFT);

            auth.setUsername(email);
            auth.setPassword(password);

            try {
                auth.logIn();

                Minecraft.getMinecraft().setSession(new Session(
                        auth.getSelectedProfile().getName(),
                        auth.getSelectedProfile().getId().toString(),
                        auth.getAuthenticatedToken(),
                        "mojang"
                ));
                LogManager.getLogger().info("Logged in with account " + Minecraft.getMinecraft().getSession().getUsername());
            } catch (AuthenticationException exception) {
                LogManager.getLogger().error("Invalid credentials in authentication file!");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
