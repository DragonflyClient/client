package net.inceptioncloud.dragonfly.discord;

import net.arikia.dev.drpc.DiscordRichPresence;
import net.inceptioncloud.dragonfly.versioning.DragonflyVersion;

/**
 * The superclass of any game status that is displayed in the Discord Rich Presence.
 */
public class RichPresenceAdapter
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
    private String bigImageKey = "512x";

    /**
     * The hover text displayed when hovering the big image.
     */
    private String bigImageText = "Dragonfly " + DragonflyVersion.getString();

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
            .setParty("myPartyId", 1, 6)
            .setSecrets("secret-abababa", null)
            .build();
    }

    public void setTitle (final String title)
    {
        this.title = title;
    }

    public void setExtra (final String extra)
    {
        this.extra = extra;
    }

    public void setStartMillis (final long startMillis)
    {
        this.startMillis = startMillis;
    }

    public void setEndMillis (final long endMillis)
    {
        this.endMillis = endMillis;
    }

    public void setBigImageKey (final String bigImageKey)
    {
        this.bigImageKey = bigImageKey;
    }

    public void setBigImageText (final String bigImageText)
    {
        this.bigImageText = bigImageText;
    }

    public void setSmallImageKey (final String smallImageKey)
    {
        this.smallImageKey = smallImageKey;
    }

    public void setSmallImageText (final String smallImageText)
    {
        this.smallImageText = smallImageText;
    }
}
