package net.inceptioncloud.dragonfly.options.sections;

import net.inceptioncloud.dragonfly.options.OptionKey;
import net.minecraft.client.multiplayer.ServerData;

/**
 * Contains all {@link OptionKey}s that are used by the client to store information but cannot be modified.
 */
public class StorageOptions {

    public static final OptionKey<ServerData> LAST_SERVER = OptionKey.newInstance(ServerData.class)
            .key("lastServer")
            .defaultValue((ServerData) null)
            .validator(str -> true)
            .build();

    public static final OptionKey<Boolean> SKIP_LOGIN = OptionKey.newInstance(Boolean.class)
            .key("skipLogin")
            .defaultValue(false)
            .validator(bool -> true)
            .build();
}
