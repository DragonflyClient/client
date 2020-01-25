package net.inceptioncloud.minecraftmod.gui.components;

import net.inceptioncloud.minecraftmod.InceptionMod;
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
public class CleanGuiButton extends GuiButton
{
    /**
     * The font renderer with which the button text is drawn.
     */
    private IFontRenderer fontRenderer = InceptionMod.getInstance().getFontDesign().retrieveOrBuild("Product Sans Medium", Font.PLAIN, 22);

    /**
     * Super-Constructor
     */
    public CleanGuiButton (final int buttonId, final int x, final int y, final String buttonText)
    {
        this(buttonId, x, y, 200, 20, buttonText);
    }

    /**
     * Super-Constructor
     */
    public CleanGuiButton (final int buttonId, final int x, final int y, final int widthIn, final int heightIn, final String buttonText)
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
            fontRenderer.drawCenteredString(this.displayString, this.xPosition + this.width / 2, this.yPosition + i, 0xFFFFFF, true);
        }
    }

    public void setFontRenderer (final IFontRenderer fontRenderer)
    {
        this.fontRenderer = fontRenderer;
    }
}
