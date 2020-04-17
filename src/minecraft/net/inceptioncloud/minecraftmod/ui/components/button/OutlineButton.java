package net.inceptioncloud.minecraftmod.ui.components.button;

import net.inceptioncloud.minecraftmod.InceptionMod;
import net.inceptioncloud.minecraftmod.design.color.GreyToneColor;
import net.inceptioncloud.minecraftmod.design.color.RGB;
import net.inceptioncloud.minecraftmod.design.font.IFontRenderer;
import net.inceptioncloud.minecraftmod.transition.color.ColorTransition;
import net.inceptioncloud.minecraftmod.transition.color.ColorTransitionBuilder;
import net.inceptioncloud.minecraftmod.transition.number.DoubleTransition;
import net.inceptioncloud.minecraftmod.transition.supplier.ForwardBackward;
import net.inceptioncloud.minecraftmod.ui.renderer.RectangleRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import java.awt.*;

/**
 * A Simple UI Button that has a {@link GreyToneColor#LIGHT_WHITE white} outline and a {@link GreyToneColor#DARK_GREY grey} background.
 */
public class OutlineButton extends GuiButton
{
    /**
     * Transition manages the Hover Effect
     */
    protected DoubleTransition hoverTransition = DoubleTransition.builder().start(0.0).end(1.0).amountOfSteps(35).autoTransformator(( ForwardBackward ) () -> hovered).build();

    /**
     * Transition manages the Button Fill Color
     */
    protected ColorTransition fillTransition = new ColorTransitionBuilder().start(GreyToneColor.DARK_GREY).end(new Color(0x25ccf7)).amountOfSteps(25).autoTransformator((ForwardBackward) () -> hovered).build();

    /**
     * Inherit Constructor
     *
     * @see GuiButton#GuiButton(int, int, int, String) Original Constructor
     */
    public OutlineButton (final int buttonId, final int x, final int y, final String buttonText)
    {
        super(buttonId, x, y, buttonText);
    }

    /**
     * Inherit Constructor
     *
     * @see GuiButton#GuiButton(int, int, int, String) Original Constructor
     */
    public OutlineButton (final int buttonId, final int x, final int y, final int widthIn, final int heightIn, final String buttonText)
    {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
    }

    /**
     * Draws this button on the screen.
     */
    @Override
    public void drawButton (final Minecraft mc, final int mouseX, final int mouseY)
    {
        if (this.visible) {
            IFontRenderer fontRenderer = InceptionMod.getInstance().getFontDesign().getMedium();

            double transition = 1 - hoverTransition.get();
            int offset = ( int ) ( 7 * transition );
            int opacity = ( int ) ( 255 * hoverTransition.get());

            double left = this.xPosition;
            double top = this.yPosition;
            double right = this.xPosition + this.width;
            double bottom = this.yPosition + this.height;

            this.hovered = mouseX >= left && mouseY >= top && mouseX < right && mouseY < bottom;
            this.mouseDragged(mc, mouseX, mouseY);

            drawRect(left, top, right, bottom, fillTransition.get().getRGB());

            final int textX = (int) left + this.width / 2;
            final int textY = (int) top + (this.height - 6) / 2;
            fontRenderer.drawCenteredString(this.displayString, textX + 1, textY + 1, new Color(0F, 0F, 0F, (float) (0.05F + hoverTransition.get() / 2F)).getRGB(), false);
            fontRenderer.drawCenteredString(this.displayString, textX, textY, -1, false);

            RectangleRenderer.drawOutline(left, top, right, bottom, GreyToneColor.DARK_WHITE);
            RectangleRenderer.renderInline(left - offset, top - offset, right + offset, bottom + offset,
                RGB.of(GreyToneColor.LIGHT_WHITE).alpha(opacity).toColor(), 1);
        }
    }

    /**
     * Destroys the Button by removing all transitions.
     */
    @Override
    public void destroy ()
    {
        InceptionMod.getInstance().stopTransition(hoverTransition);
    }
}
