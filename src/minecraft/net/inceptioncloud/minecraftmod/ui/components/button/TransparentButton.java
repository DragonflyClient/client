package net.inceptioncloud.minecraftmod.ui.components.button;

import net.inceptioncloud.minecraftmod.Dragonfly;
import net.inceptioncloud.minecraftmod.design.color.RGB;
import net.inceptioncloud.minecraftmod.engine.font.IFontRenderer;
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
     * Transition increases the opacity up to 1.0F when the button is hovered.
     */
    private final DoubleTransition hoverIncreaseOpacity = DoubleTransition.builder().start(0).end(1).autoTransformator((ForwardBackward) () -> hovered).amountOfSteps(20).build();
    /**
     * The font renderer with which the button text is drawn.
     */
    private IFontRenderer fontRenderer = Dragonfly.getFontDesign().retrieveOrBuild(" Medium", 22);
    /**
     * Whether the clean button is highlighted by an underline.
     */
    private boolean highlighted = false;
    /**
     * The transition that animates the underline.
     */
    private final DoubleTransition underline = DoubleTransition.builder().start(0).end(1).autoTransformator((ForwardBackward) this::isHighlighted).amountOfSteps(40).build();
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

    public boolean isHighlighted ()
    {
        return highlighted;
    }

    public void setHighlighted (final boolean highlighted)
    {
        this.highlighted = highlighted;
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

            final float tempOpacity = (float) (this.opacity + (0.85F - opacity) * hoverIncreaseOpacity.get());
            final int i = Math.max(2, (this.height - fontRenderer.getHeight()) / 2);
            fontRenderer.drawCenteredString(this.displayString,
                this.xPosition + this.width / 2,
                this.yPosition + i,
                RGB.of(0xFFFFFF).alpha(highlighted ? opacity : tempOpacity).rgb(),
                true);

            int centerX = xPosition + (width / 2);
            int underlineWidth = (int) ((fontRenderer.getStringWidth(this.displayString) / 2) * underline.get());
            drawHorizontalLine(centerX - underlineWidth, centerX + underlineWidth, yPosition + fontRenderer.getHeight() + 1, Color.WHITE.getRGB());
        }
    }

    /**
     * Destroys the Button by removing all transitions.
     */
    @Override
    public void destroy ()
    {
        underline.destroy();
        hoverIncreaseOpacity.destroy();
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
