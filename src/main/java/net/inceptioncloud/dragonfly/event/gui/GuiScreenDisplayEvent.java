package net.inceptioncloud.dragonfly.event.gui;

import net.inceptioncloud.dragonfly.event.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

/**
 * When a new {@link GuiScreen} is displayed via {@link Minecraft#displayGuiScreen(GuiScreen)}.
 */
public class GuiScreenDisplayEvent extends Cancellable implements Event {
    /**
     * The screen that is currently displayed and that will be replaced by the new screen.
     * Can be <code>null</code> if no screen is displayed.
     */
    private final GuiScreen previousScreen;

    /**
     * The screen that will replace the current screen if the event won't be cancelled.
     */
    private final GuiScreen newScreen;

    public GuiScreenDisplayEvent(final GuiScreen previousScreen, final GuiScreen newScreen) {
        this.previousScreen = previousScreen;
        this.newScreen = newScreen;
    }

    public GuiScreen getPreviousScreen() {
        return previousScreen;
    }

    public GuiScreen getNewScreen() {
        return newScreen;
    }
}
