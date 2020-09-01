package net.inceptioncloud.dragonfly.event.play;

import net.inceptioncloud.dragonfly.event.*;
import net.minecraft.client.Minecraft;
import net.minecraft.world.WorldSettings;

/**
 * When the player joins a singleplayer world and the client launches an integrated server via {@link Minecraft#launchIntegratedServer(String, String, WorldSettings)}.
 */
public class IntegratedServerStartingEvent extends Cancellable implements Event {
    /**
     * The current name of the world.
     */
    private final String worldName;

    /**
     * The original world name (also the name of the folder)
     */
    private final String folderName;

    /**
     * The settings of the world.
     * Could be null!
     */
    private final WorldSettings settings;

    public IntegratedServerStartingEvent(final String worldName, final String folderName, final WorldSettings settings) {
        this.worldName = worldName;
        this.folderName = folderName;
        this.settings = settings;
    }

    public String getWorldName() {
        return worldName;
    }

    public String getFolderName() {
        return folderName;
    }

    public WorldSettings getSettings() {
        return settings;
    }
}
