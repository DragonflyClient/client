package net.inceptioncloud.minecraftmod.event.gui;

import lombok.*;
import net.inceptioncloud.minecraftmod.event.Cancellable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

/**
 * When a new {@link GuiScreen} is displayed via {@link Minecraft#displayGuiScreen(GuiScreen)}.
 */
@Getter
@RequiredArgsConstructor
public class GuiScreenDisplayEvent extends Cancellable
{
    /**
     * The screen that is currently displayed and that will be replaced by the new screen.
     * Can be <code>null</code> if no screen is displayed.
     */
    private final GuiScreen previousScreen;

    /**
     * The screen that will replace the current screen if the event won't be cancelled.
     */
    private final GuiScreen newScreen;
}
