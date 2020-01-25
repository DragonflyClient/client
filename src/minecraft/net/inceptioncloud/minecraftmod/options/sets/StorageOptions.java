package net.inceptioncloud.minecraftmod.options.sets;

import net.inceptioncloud.minecraftmod.options.OptionKey;

/**
 * Contains all {@link OptionKey}s that are used by the client to store information but cannot be modified.
 */
public class StorageOptions
{
    public static final OptionKey<String> LAST_SERVER = OptionKey.<String>newInstance().key("lastServer").defaultValue(( String ) null).validator(str -> true).build();
}
