package net.inceptioncloud.minecraftmod.ui.components;

import lombok.Getter;
import lombok.Setter;
import net.inceptioncloud.minecraftmod.InceptionMod;
import net.inceptioncloud.minecraftmod.design.color.*;
import net.inceptioncloud.minecraftmod.design.font.IFontRenderer;
import net.inceptioncloud.minecraftmod.transition.color.ColorTransition;
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
     * The transition for the left color of the gradient.
     */
    private ColorTransition transitionColorLeft = ColorTransition.builder().start(GreyToneColor.DARK_GREY).end(CloudColor.FUSION).amountOfSteps(50).autoTransformator(( ForwardBackward ) () -> hovered).build();

    /**
     * The transition for the right color of the gradient.
     */
    private ColorTransition transitionColorRight = ColorTransition.builder().start(GreyToneColor.DARK_GREY).end(CloudColor.ROYAL).amountOfSteps(50).autoTransformator(( ForwardBackward ) () -> hovered).build();

    /**
     * The opacity of the button.
     */
    @Getter
    @Setter
    private float opacity = 1.0F;

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
            final double left = this.xPosition;
            final double top = this.yPosition;
            final double right = this.xPosition + this.width;
            final double bottom = this.yPosition + this.height;
            final int colorLeft = ColorTransformator.of(transitionColorLeft.get()).transformAlpha(opacity).toRGB();
            final int colorRight = ColorTransformator.of(transitionColorRight.get()).transformAlpha(opacity).toRGB();

            this.hovered = mouseX >= left && mouseY >= top && mouseX < right && mouseY < bottom;
            this.mouseDragged(mc, mouseX, mouseY);

            drawRectD(left, top, right, bottom, ColorTransformator.of(GreyToneColor.LIGHT_WHITE).transformAlpha(opacity).toRGB());
            drawGradientHorizontalD(left + border, top + border, right - border, bottom - border, colorLeft, colorRight);
            if (opacity > 0.1)
                drawCenteredString(fontrenderer, this.displayString, (int) left + this.width / 2, (int) top + ( this.height - 8 ) / 2, new Color(1, 1, 1, opacity).getRGB());
        }
    }
}
