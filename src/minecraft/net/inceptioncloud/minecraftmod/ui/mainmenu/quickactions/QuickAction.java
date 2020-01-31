package net.inceptioncloud.minecraftmod.ui.mainmenu.quickactions;

import lombok.Getter;
import net.minecraft.client.gui.GuiMainMenu;

/**
 * A quick action is a button that appears when the navigation bar in the main menu rises.
 * Every category (Singleplayer, Multiplayer, Options, Quit Game) has two quick actions.
 * The Button ID that is used for the category can be found {@link GuiMainMenu#addButtons() here}.
 */
@Getter
public class QuickAction
{
    /**
     * The ID of the button (or category) to which this quick action belongs to.
     */
    private final int headButtonId;

    /**
     * The id of the button that can be clicked to execute this action.
     */
    private final int ownButtonId;

    /**
     * The text that is displayed to represent the quick action.
     */
    private final String display;

    /**
     * Called when the user clicks on the quick action.
     */
    private final Runnable handleClick;

    /**
     * Required Arguments Constructor
     */
    public QuickAction (final int headButtonId, final int ownButtonId, final String display, final Runnable handleClick)
    {
        this.headButtonId = headButtonId;
        this.ownButtonId = ownButtonId;
        this.display = display;
        this.handleClick = handleClick;
    }
}