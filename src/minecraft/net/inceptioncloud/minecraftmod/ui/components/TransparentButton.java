package net.inceptioncloud.minecraftmod.ui.components;

import lombok.Getter;
import lombok.Setter;
import net.inceptioncloud.minecraftmod.InceptionMod;
import net.inceptioncloud.minecraftmod.design.color.ColorTransformator;
import net.inceptioncloud.minecraftmod.design.font.IFontRenderer;
import net.inceptioncloud.minecraftmod.transition.number.DoubleTransition;
import net.inceptioncloud.minecraftmod.transition.supplier.ForwardBackward;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;

/**
 * A {@link GuiButton} with custom font and transparent background!
 */
public class TransparentButton extends GuiButton
{
    /**
     * The font renderer with which the button text is drawn.
     */
    private IFontRenderer fontRenderer = InceptionMod.getInstance().getFontDesign().retrieveOrBuild("Product Sans Medium", Font.PLAIN, 22);

    /**
     * Whether the clean button is highlighted by an underline.
     */
    @Getter @Setter
    private boolean highlighted = false;

    /**
     * The transition that animates the underline.
     */
    private final DoubleTransition underline = DoubleTransition.builder().start(0).end(1).autoTransformator(( ForwardBackward ) this::isHighlighted).amountOfSteps(40).build();

    /**
     * The opacity of the text.
     */
    private float opacity = 1.0F;

    /**
     * Super-Constructor
     */
    public TransparentButton (final int buttonId, final int x, final int y, final String buttonText)
    {
        this(buttonId, x, y, 200, 20, buttonText);
    }

    /**
     * Super-Constructor
     */
    public TransparentButton (final int buttonId, final int x, final int y, final int widthIn, final int heightIn, final String buttonText)
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
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            this.mouseDragged(mc, mouseX, mouseY);

            final int i = Math.max(2, ( this.height - fontRenderer.getHeight() ) / 2);
            fontRenderer.drawCenteredString(this.displayString, this.xPosition + this.width / 2, this.yPosition + i, ColorTransformator.of(0xFFFFFF).changeAlpha(opacity).toRGB(), true);

            int centerX = xPosition + ( width / 2 );
            int underlineWidth = ( int ) ( ( fontRenderer.getStringWidth(this.displayString) / 2 ) * underline.get() );
            drawHorizontalLine(centerX - underlineWidth, centerX + underlineWidth, yPosition + fontRenderer.getHeight(), new Color(255, 255, 255, 255).getRGB());
        }
    }

    /**
     * Destroys the Button by removing all transitions.
     */
    @Override
    public void destroy ()
    {
        InceptionMod.getInstance().stopTransition(underline);
    }

    /**
     * Changes the font renderer of the button.
     */
    public void setFontRenderer (final IFontRenderer fontRenderer)
    {
        this.fontRenderer = fontRenderer;
    }

    /**
     * Changes the opacity of the button.
     *
     * @return The button instance
     */
    public TransparentButton setOpacity (final float opacity)
    {
        this.opacity = opacity;
        return this;
    }
}
