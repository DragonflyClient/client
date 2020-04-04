package net.inceptioncloud.minecraftmod.options.sets;

import net.inceptioncloud.minecraftmod.options.OptionKey;

import java.awt.event.KeyEvent;

/**
 * Contains all prepared {@link OptionKey}s for user interface options.
 */
public class UIOptions
{
    /**
     * <h2>Player List Indicator Menu</h2>
     * Hotkey to be pressed to open the indicator menu.
     * <hr/>
     *
     * <b>Key: </b>playerListIndicatorMenu<br>
     * <b>Type: </b>Integer<br>
     * <b>Default: </b><code>-1</code>
     */
    public static final OptionKey<Integer> PLAYER_LIST_INDICATOR_MENU = OptionKey.newInstance(Integer.class)
        .key("playerListIndicatorMenu")
        .defaultValue(KeyEvent.VK_I)
        .validator(val -> !KeyEvent.getKeyText(val).contains("unknown"))
        .build();

    /**
     * <h2>Font Quality Scale</h2>
     * The scale-factor in which the glyph page is rendered in order to achieve a higher font
     * quality on higher gui scales. Takes performance!
     * <hr/>
     *
     * <b>Key: </b>fontQualityScale<br/>
     * <b>Type: </b>Integer<br/>
     * <b>Default: </b><code>2</code>
     */
    public static final OptionKey<Double> FONT_QUALITY_SCALE = OptionKey.newInstance(Double.class)
        .key("fontQualityScale")
        .defaultValue(2.0)
        .validator(val -> val >= 1 && val <= 8)
        .build();
}
