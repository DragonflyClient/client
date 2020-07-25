package net.inceptioncloud.dragonfly.ui.components.button;

import net.inceptioncloud.dragonfly.Dragonfly;
import net.inceptioncloud.dragonfly.design.color.CloudColor;
import net.inceptioncloud.dragonfly.design.color.GreyToneColor;
import net.inceptioncloud.dragonfly.design.color.RGB;
import net.inceptioncloud.dragonfly.engine.font.renderer.IFontRenderer;
import net.inceptioncloud.dragonfly.transition.color.ColorTransition;
import net.inceptioncloud.dragonfly.transition.color.ColorTransitionBuilder;
import net.inceptioncloud.dragonfly.transition.number.DoubleTransition;
import net.inceptioncloud.dragonfly.transition.supplier.ForwardBackward;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

import java.awt.*;

/**
 * An extension of the {@link SimpleButton} that forces the user to confirm the action by holding down the button.
 */
public class ConfirmationButton extends SimpleButton
{
    /**
     * Handles the effect when holding down the button.
     */
    protected DoubleTransition holdTransition;

    /**
     * Transition changes the color when hovering.
     */
    protected ColorTransition colorTransition;

    /**
     * Transition manages the Hover Effect
     */
    protected DoubleTransition hoverTransition;

    /**
     * The parent screen in which this button is being displayed.
     */
    private final GuiScreen parentScreen;

    /**
     * Counts the ticks during the end-state of the {@link #holdTransition}.
     */
    private int endTicks = 0;

    /**
     * Inherit Constructor
     *
     * @see GuiButton#GuiButton(int, int, int, String) Original Constructor
     */
    public ConfirmationButton (final GuiScreen parentScreen, final int buttonId, final int x, final int y, final String buttonText)
    {
        super(buttonId, x, y, buttonText);
        this.parentScreen = parentScreen;

        initTransitions();
    }

    /**
     * Inherit Constructor
     *
     * @see GuiButton#GuiButton(int, int, int, String) Original Constructor
     */
    public ConfirmationButton (final GuiScreen parentScreen, final int buttonId, final int x, final int y, final int widthIn, final int heightIn, final String buttonText)
    {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
        this.parentScreen = parentScreen;

        initTransitions();
    }

    /**
     * Draws this button on the screen.
     */
    @Override
    public void drawButton (final Minecraft mc, final int mouseX, final int mouseY)
    {
        if (this.visible) {
            IFontRenderer fontrenderer = Dragonfly.getFontDesign().getMedium();
            final double border = 0.5;
            final double left = this.xPosition + border;
            final double top = this.yPosition + border;
            final double right = this.xPosition + this.width - border;
            final double bottom = this.yPosition + this.height - border;
            final double width = right - left;
            final Color color = RGB.of(colorTransition.get()).alpha(opacity).toColor();

            this.hovered = mouseX >= left && mouseY >= top && mouseX < right && mouseY < bottom;
            this.mouseDragged(mc, mouseX, mouseY);

            drawRect(left - border, top - border, right + border, bottom + border, RGB.of(GreyToneColor.LIGHT_WHITE).alpha(opacity).rgb());
            drawRect(left, top, right, bottom, RGB.of(GreyToneColor.DARK_GREY).alpha(opacity).rgb());

            drawRect(left + ( holdTransition.get() * ( width - 4 ) ), top, left + 4 + ( hoverTransition.get() * ( width - 4 ) ), bottom, color.getRGB());

            if (opacity > 0.05)
                drawCenteredString(fontrenderer, this.displayString, ( int ) left + this.width / 2, ( int ) top + ( this.height - 6 ) / 2, new Color(1, 1, 1, opacity).getRGB());

            if (holdTransition.isAtEnd()) {
                endTicks++;
            } else endTicks = 0;

            if (endTicks >= 15) {
                endTicks = -500;
                parentScreen.buttonClick(this);
            }
        }
    }

    /**
     * Called in the constructor to initialize all transitions.
     */
    private void initTransitions ()
    {
        holdTransition = DoubleTransition.builder()
            .start(0.0).end(1.0)
            .amountOfSteps(( int ) ( width / 2.8))
            .autoTransformator(( ForwardBackward ) () -> hoverTransition != null && hoverTransition.isAtEnd() && Mouse.isCreated() && Mouse.isButtonDown(0))
            .build();

        colorTransition  = new ColorTransitionBuilder()
            .start(CloudColor.ROYAL.brighter()).end(CloudColor.NTSC)
            .amountOfSteps(width / 2)
            .autoTransformator(( ForwardBackward ) () -> hovered).build();

        hoverTransition = DoubleTransition.builder()
            .start(0.0).end(1.0)
            .amountOfSteps(width / 4)
            .autoTransformator(( ForwardBackward ) () -> hovered || !holdTransition.isAtStart()).build();
    }

    @Override
    public void destroy ()
    {
        holdTransition.destroy();
        colorTransition.destroy();
        hoverTransition.destroy();

        super.destroy();
    }
}
