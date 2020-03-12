package net.inceptioncloud.minecraftmod.ui.mainmenu.quit;

import net.inceptioncloud.minecraftmod.ui.mainmenu.QuickAction;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.List;

/**
 * Restarts the client when clicking the action.
 */
public class RestartAction extends QuickAction
{
    /**
     * Default Constructor
     */
    public RestartAction ()
    {
        super(3, 18, "Restart", () ->
        {
            try {
                restartApplication(() -> LogManager.getLogger().info("Restating Minecraft Application..."));
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    /**
     * Restart the current Java application
     *
     * @param runBeforeRestart some custom code to be run before restarting
     *
     * @throws IOException Any exception
     */
    public static void restartApplication (Runnable runBeforeRestart) throws IOException
    {
        try {
            String java = System.getProperty("java.home") + "/bin/java";
            List<String> vmArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
            StringBuilder vmArgsOneLine = new StringBuilder();

            for (String arg : vmArguments) {
                if (!arg.contains("-agentlib")) {
                    vmArgsOneLine.append(arg);
                    vmArgsOneLine.append(" ");
                }
            }

            final StringBuffer cmd = new StringBuffer("\"" + java + "\" " + vmArgsOneLine);
            final String[] mainCommand = System.getProperty("sun.java.command").split(" ");

            if (mainCommand[0].endsWith(".jar"))
                cmd.append("-jar ").append(new File(mainCommand[0]).getPath());
            else
                cmd.append("-cp \"").append(System.getProperty("java.class.path")).append("\" ").append(mainCommand[0]);

            for (int i = 1 ; i < mainCommand.length ; i++) {
                cmd.append(" ");
                cmd.append(mainCommand[i]);
            }

            Runtime.getRuntime().addShutdownHook(new Thread(() ->
            {
                try {
                    Runtime.getRuntime().exec(cmd.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }));

            if (runBeforeRestart != null)
                runBeforeRestart.run();

            Minecraft.getMinecraft().shutdown();
        } catch (Exception e) {
            throw new IOException("Error while trying to restart the application", e);
        }
    }
}
