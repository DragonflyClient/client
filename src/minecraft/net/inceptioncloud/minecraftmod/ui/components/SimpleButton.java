package net.inceptioncloud.minecraftmod.ui.components;

import lombok.Getter;
import lombok.Setter;
import net.inceptioncloud.minecraftmod.InceptionMod;
import net.inceptioncloud.minecraftmod.design.color.*;
import net.inceptioncloud.minecraftmod.design.font.IFontRenderer;
import net.inceptioncloud.minecraftmod.transition.color.ColorTransition;
import net.inceptioncloud.minecraftmod.transition.number.DoubleTransition;
import net.inceptioncloud.minecraftmod.transition.supplier.ForwardBackward;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import java.awt.*;

/**
 * A Simple UI Button that has a {@link GreyToneColor#LIGHT_WHITE white} outline and a {@link GreyToneColor#DARK_GREY grey} background.
 */
public class SimpleButton extends GuiButton
{
    /**
     * Transition manages the Hover Effect
     */
    protected DoubleTransition hoverTransition = DoubleTransition.builder().start(0.0).end(1.0).amountOfSteps(width / 4).autoTransformator(( ForwardBackward ) () -> hovered).build();

    /**
     * Transition changes the color when hovering.
     */
    protected ColorTransition colorTransition = ColorTransition.builder().start(CloudColor.FUSION).end(CloudColor.DESIRE).amountOfSteps(width / 2).autoTransformator(( ForwardBackward ) () -> hovered).build();

    /**
     * The opacity of the button.
     */
    @Setter @Getter
    protected float opacity = 1.0F;

    /**
     * Inherit Constructor
     *
     * @see GuiButton#GuiButton(int, int, int, String) Original Constructor
     */
    public SimpleButton (final int buttonId, final int x, final int y, final String buttonText)
    {
        super(buttonId, x, y, buttonText);
    }

    /**
     * Inherit Constructor
     *
     * @see GuiButton#GuiButton(int, int, int, String) Original Constructor
     */
    public SimpleButton (final int buttonId, final int x, final int y, final int widthIn, final int heightIn, final String buttonText)
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
            IFontRenderer fontrenderer = InceptionMod.getInstance().getFontDesign().getRegular();
            final double border = 0.5;
            final double left = this.xPosition + border;
            final double top = this.yPosition + border;
            final double right = this.xPosition + this.width - border;
            final double bottom = this.yPosition + this.height - border;
            final Color color = ColorTransformator.of(colorTransition.get()).changeAlpha(opacity).toColor();

            this.hovered = mouseX >= left && mouseY >= top && mouseX < right && mouseY < bottom;
            this.mouseDragged(mc, mouseX, mouseY);

            drawRect(left - border, top - border, right + border, bottom + border, ColorTransformator.of(GreyToneColor.LIGHT_WHITE).changeAlpha(opacity).toRGB());
            drawRect(left, top, right, bottom, ColorTransformator.of(GreyToneColor.DARK_GREY).changeAlpha(opacity).toRGB());

            drawRect(left, top, left + 4 + ( hoverTransition.get() * ( right - left - 4)), bottom, color.getRGB());

            if (opacity > 0.1)
                drawCenteredString(fontrenderer, this.displayString, (int) left + this.width / 2, (int) top + ( this.height - 8 ) / 2, new Color(1, 1, 1, opacity).getRGB());
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
