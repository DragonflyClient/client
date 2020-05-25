package net.inceptioncloud.minecraftmod.ui.components.button;

import net.inceptioncloud.minecraftmod.Dragonfly;
import net.inceptioncloud.minecraftmod.design.color.BluePalette;
import net.inceptioncloud.minecraftmod.engine.font.GlyphFontRenderer;
import net.inceptioncloud.minecraftmod.transition.color.ColorTransition;
import net.inceptioncloud.minecraftmod.transition.color.ColorTransitionBuilder;
import net.inceptioncloud.minecraftmod.transition.supplier.ForwardBackward;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import java.awt.*;

/**
 * A button in the {@link BluePalette} style.
 */
public class BluePaletteButton extends GuiButton
{
    /**
     * Transition changes the fill color when hovering.
     */
    protected ColorTransition fillColor = new ColorTransitionBuilder()
        .start(BluePalette.getPRIMARY_LIGHT()).end(BluePalette.getFOREGROUND())
        .amountOfSteps(30).autoTransformator((ForwardBackward) () -> hovered).build();

    /**
     * Transition changes the text color when hovering.
     */
    protected ColorTransition textColor = new ColorTransitionBuilder()
        .start(BluePalette.getFOREGROUND()).end(BluePalette.getPRIMARY())
        .amountOfSteps(30).autoTransformator((ForwardBackward) () -> hovered).build();

    /**
     * The opacity of the button.
     */
    protected float opacity = 1.0F;

    /**
     * Inherit Constructor
     *
     * @see GuiButton#GuiButton(int, int, int, String) Original Constructor
     */
    public BluePaletteButton (final int buttonId, final int x, final int y, final String buttonText)
    {
        super(buttonId, x, y, buttonText);
    }

    /**
     * Inherit Constructor
     *
     * @see GuiButton#GuiButton(int, int, int, String) Original Constructor
     */
    public BluePaletteButton (final int buttonId, final int x, final int y, final int widthIn, final int heightIn, final String buttonText)
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
            GlyphFontRenderer fontrenderer = (GlyphFontRenderer) Dragonfly.getFontDesign().retrieveOrBuild(" Medium", 19);
            final double border = 1;
            final double left = this.xPosition + border;
            final double top = this.yPosition + border;
            final double right = this.xPosition + this.width - border;
            final double bottom = this.yPosition + this.height - border;

            this.hovered = mouseX >= left && mouseY >= top && mouseX < right && mouseY < bottom;
            this.mouseDragged(mc, mouseX, mouseY);

            drawRect(left - border, top - border, right + border, bottom + border, BluePalette.getPRIMARY().getRGB());
            drawRect(left, top, right, bottom, fillColor.get().getRGB());

            int stringWith = fontrenderer.getStringWidth(this.displayString) + 4;

            fontrenderer.drawStringWithCustomShadow(
                this.displayString,
                (int) (left + this.width / 2 - stringWith / 2),
                (int) top + this.height / 2 - 4,
                textColor.get().getRGB(),
                new Color(0, 0, 0, hovered ? 40 : 70).getRGB(),
                0.6F
            );
        }
    }

    public float getOpacity ()
    {
        return opacity;
    }

    public void setOpacity (final float opacity)
    {
        this.opacity = opacity;
    }

    /**
     * Destroys the Button by removing all transitions.
     */
    @Override
    public void destroy ()
    {
        fillColor.destroy();
        textColor.destroy();
    }
}
