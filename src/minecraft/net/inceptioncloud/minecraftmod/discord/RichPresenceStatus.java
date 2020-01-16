package net.inceptioncloud.minecraftmod.discord;

import lombok.*;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.inceptioncloud.minecraftmod.version.InceptionCloudVersion;

/**
 * The superclass of any game status that is displayed in the Discord Rich Presence.
 */
@Setter
public class RichPresenceStatus
{
    /**
     * The status string that is being displayed.
     */
    private String title;

    /**
     * The details string that is displayed below the status.
     */
    private String extra;

    /**
     * The start time for the timer.
     */
    private long startMillis;

    /**
     * The end time for the timer.
     */
    private long endMillis;

    /**
     * The asset key for the big image.
     */
    private String bigImageKey = "border-thick";

    /**
     * The hover text displayed when hovering the big image.
     */
    private String bigImageText = InceptionCloudVersion.FULL_VERSION;

    /**
     * The asset key for the small image.
     */
    private String smallImageKey;

    /**
     * The hover text displayed when hovering the big image.
     */
    private String smallImageText;

    /**
     * Builds the Rich Presence that will display this status.
     * @return The built instance
     */
    public DiscordRichPresence buildRichPresence ()
    {
        return new DiscordRichPresence.Builder(extra)
            .setDetails(title)
            .setStartTimestamps(startMillis)
            .setEndTimestamp(endMillis)
            .setBigImage(bigImageKey, bigImageText)
            .setSmallImage(smallImageKey, smallImageText)
            .build();
    }
}
