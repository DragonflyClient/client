package net.inceptioncloud.minecraftmod.subscriber;

import com.google.common.eventbus.Subscribe;
import net.inceptioncloud.minecraftmod.event.gui.GuiScreenDisplayEvent;

public class TestSubscriber
{
    @Subscribe
    public void guiScreenDisplay (GuiScreenDisplayEvent event)
    {
    }
}
