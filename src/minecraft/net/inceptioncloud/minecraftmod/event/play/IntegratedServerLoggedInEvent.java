package net.inceptioncloud.minecraftmod.event.play;

import lombok.*;
import net.minecraft.client.Minecraft;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.world.WorldSettings;

/**
 * When an integrated server was successfully started by {@link Minecraft#launchIntegratedServer(String, String, WorldSettings)}.
 */
@Getter
@RequiredArgsConstructor
public class IntegratedServerLoggedInEvent
{
    /**
     * The integrated server that has been launched.
     */
    private final IntegratedServer integratedServer;
}
