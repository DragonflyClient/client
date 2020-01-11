package net.minecraft.client.gui;

import net.inceptioncloud.minecraftmod.transition.number.DoubleTransition;
import net.inceptioncloud.minecraftmod.transition.supplier.ForwardNothing;
import net.minecraft.util.IChatComponent;

public class ChatLine
{
    /** GUI Update Counter value this Line was created at */
    private final int updateCounterCreated;
    private final IChatComponent lineString;

    /**
     * int value to refer to existing Chat Lines, can be 0 which means unreferrable
     */
    private final int chatLineID;

    /**
     * The transition for the location of the chat message.
     */
    private final DoubleTransition location;

    /**
     * The transition for the opacity.
     */
    private final DoubleTransition opacity;

    public ChatLine(int updateCounter, IChatComponent component, int lineID)
    {
        this.lineString = component;
        this.updateCounterCreated = updateCounter;
        this.chatLineID = lineID;

        this.location = DoubleTransition.builder().start(0.0D).end(1.0D).amountOfSteps(100).autoTransformator(( ForwardNothing ) () -> true).build();
        this.opacity = DoubleTransition.builder().start(50).end(255).amountOfSteps(200).autoTransformator((ForwardNothing) () -> location.get() > 0.5).build();
    }

    public IChatComponent getChatComponent()
    {
        return this.lineString;
    }

    /**
     * @return How many times the gui has been updated with this line shown.
     */
    public int getUpdatedCounter()
    {
        return this.updateCounterCreated;
    }

    public int getChatLineID()
    {
        return this.chatLineID;
    }

    public DoubleTransition getLocation ()
    {
        return location;
    }

    public DoubleTransition getOpacity ()
    {
        return opacity;
    }
}
