package net.inceptioncloud.minecraftmod.event.play;

import lombok.*;
import net.inceptioncloud.minecraftmod.event.Cancellable;
import net.minecraft.client.Minecraft;
import net.minecraft.world.WorldSettings;

/**
 * When the player joins a singleplayer world and the client launches an integrated server via {@link Minecraft#launchIntegratedServer(String, String, WorldSettings)}.
 */
@Getter
@RequiredArgsConstructor
public class IntegratedServerStartingEvent extends Cancellable
{
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
}
