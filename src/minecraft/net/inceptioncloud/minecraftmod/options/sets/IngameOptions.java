package net.inceptioncloud.minecraftmod.options.sets;

import net.inceptioncloud.minecraftmod.options.OptionKey;

/**
 * Contains all prepared {@link OptionKey}s for in-game options.
 */
public class IngameOptions
{
    /**
     * <h2>Zoom Animation</h2>
     * Whether to directly zoom in or show fluently zoom with an animation.
     * <hr/>
     *
     * <b>Key: </b>zoomAnimation<br/>
     * <b>Type: </b>Boolean<br/>
     * <b>Default: </b><code>true</code>
     */
    public static final OptionKey<Boolean> ZOOM_ANIMATION = OptionKey.newInstance(Boolean.class).key("zoomAnimation").defaultValue(true).validator(val -> true).build();

    /**
     * <h2>Scoreboard | Score Numbers</h2>
     * The red number that shows the score of the scoreboard entry.
     * <hr/>
     *
     * <b>Key: </b>scoreboardScores<br/>
     * <b>Type: </b>Boolean<br/>
     * <b>Default: </b><code>false</code>
     */
    public static final OptionKey<Boolean> SCOREBOARD_SCORES = OptionKey.newInstance(Boolean.class).key("scoreboardScores").defaultValue(false).validator(val -> true).build();

    /**
     * <h2>Scoreboard | Background</h2>
     * The background rectangle behind the title and the entries.
     * <hr/>
     *
     * <b>Key: </b>scoreboardBackground<br/>
     * <b>Type: </b>Boolean<br/>
     * <b>Default: </b><code>false</code>
     */
    public static final OptionKey<Boolean> SCOREBOARD_BACKGROUND = OptionKey.newInstance(Boolean.class).key("scoreboardBackground").defaultValue(false).validator(val -> true).build();

    /**
     * <h2>Scoreboard | Title</h2>
     * The title of the scoreboard that is displayed above the entries.
     * <hr/>
     *
     * <b>Key: </b>scoreboardTitle<br/>
     * <b>Type: </b>Boolean<br/>
     * <b>Default: </b><code>true</code>
     */
    public static final OptionKey<Boolean> SCOREBOARD_TITLE = OptionKey.newInstance(Boolean.class).key("scoreboardTitle").defaultValue(true).validator(val -> true).build();
}
