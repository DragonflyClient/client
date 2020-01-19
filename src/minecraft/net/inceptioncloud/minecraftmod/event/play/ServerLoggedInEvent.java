package net.inceptioncloud.minecraftmod.event.play;

import lombok.*;
import net.inceptioncloud.minecraftmod.event.Cancellable;
import net.minecraft.client.multiplayer.ServerData;

/**
 * When a player did successfully login to a server.
 */
@Getter
@RequiredArgsConstructor
public class ServerLoggedInEvent extends Cancellable
{
    /**
     * The {@link ServerData} object of the server he is connecting to.
     */
    private final ServerData serverData;
}
