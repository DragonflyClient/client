package net.inceptioncloud.dragonfly.design.splash;

import net.inceptioncloud.dragonfly.Dragonfly;
import net.inceptioncloud.dragonfly.design.color.CloudColor;
import net.inceptioncloud.dragonfly.design.color.GreyToneColor;
import net.inceptioncloud.dragonfly.engine.font.renderer.IFontRenderer;
import net.inceptioncloud.dragonfly.engine.font.renderer.UnicodeFontRenderer;
import net.inceptioncloud.dragonfly.utils.RuntimeUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * The customized InceptionCloud Mod Splash Screen.
 */
public class ModSplashScreen
{
    /**
     * The default-sized custom Font Renderer.
     */
    private IFontRenderer defaultFR;

    /**
     * A larger Font Renderer for drawing the title.
     */
    private IFontRenderer titleFR;

    /**
     * A smaller Font Renderer for drawing the current action;
     */
    private IFontRenderer actionFR;

    /**
     * The Resource Location of the InceptionCloud Logo.
     */
    private ResourceLocation logo;

    /**
     * The percentage of the loading in decimal format.
     */
    private double percentage = 0.0D;

    /**
     * The action that is currently being performed.
     */
    private String action;

    /**
     * Whether the splash screen is active due to the loading of the client.
     */
    private boolean active = true;

    /**
     * Updates the splash screen if the Minecraft instance is able to do this.
     */
    public void update ()
    {
        if (Minecraft.getMinecraft() == null || Minecraft.getMinecraft().getLanguageManager() == null || !active)
            return;

        final StackTraceElement element = RuntimeUtils.getStackTrace(ModSplashScreen.class);
        this.percentage = Math.min(1.0D, this.percentage + (1.0D / 21));
        this.action = element == null ? "..." : element.getClassName() + " - " + element.getMethodName();

        performRender(Minecraft.getMinecraft().getTextureManager());
    }

    /**
     * The draw method for the splash screen.
     *
     * @param textureManager The Minecraft Texture Manager
     */
    public void performRender (TextureManager textureManager)
    {
        if (titleFR == null)
            titleFR = UnicodeFontRenderer
                    .newInstance(Dragonfly.getFontManager().getFontFamily() + " Medium", 132, Font.PLAIN);

        if (defaultFR == null)
            defaultFR = UnicodeFontRenderer
                    .newInstance(Dragonfly.getFontManager().getFontFamily() + " Medium", 75, Font.PLAIN);

        if (actionFR == null)
            actionFR = UnicodeFontRenderer
                    .newInstance(Dragonfly.getFontManager().getFontFamily() + " Light", 65, Font.PLAIN);

        if (logo == null)
            logo = new ResourceLocation("dragonflyres/logos/splash.png");

        ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
        int factor = resolution.getScaleFactor();

        Framebuffer framebuffer = new Framebuffer(resolution.getScaledWidth() * factor, resolution.getScaledHeight() * factor, true);
        framebuffer.bindFramebuffer(false);

        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0D, resolution.getScaledWidth(), resolution.getScaledHeight(), 0.0D, 1000.0D, 3000.0D);
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.loadIdentity();
        GlStateManager.translate(0, 0, -2000F);
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        GlStateManager.disableDepth();
        GlStateManager.enableTexture2D();

        GlStateManager.resetColor();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableAlpha();

        drawScreen(textureManager, resolution);

        framebuffer.unbindFramebuffer();
        framebuffer.framebufferRender(resolution.getScaledWidth() * factor, resolution.getScaledHeight() * factor);

        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.1F);

        Minecraft.getMinecraft().updateDisplay();
    }

    /**
     * Draw the content of the Splash Screen.
     */
    private void drawScreen (TextureManager textureManager, ScaledResolution resolution)
    {
        Gui.drawRect(0, 0, resolution.getScaledWidth(), resolution.getScaledHeight(), GreyToneColor.DARK_GREY.getRGB());

        resetState();
        textureManager.bindTexture(logo);
        Gui.drawModalRectWithCustomSizedTexture(resolution.getScaledWidth() / 2 - 35, 20, 0, 0, 70, 70, 70, 70);

        titleFR.drawCenteredString("InceptionCloud", resolution.getScaledWidth() / 2, 100, Color.WHITE.getRGB(), false);
        defaultFR.drawCenteredString("Dragonfly", resolution.getScaledWidth() / 2, 126, new Color(255, 255, 255, 170).getRGB(), false);
        actionFR.drawCenteredString(action, resolution.getScaledWidth() / 2 - 15, 210, new Color(255, 255, 255, 100).getRGB(), false);

        final int rectWidth = 250, rectHeight = 20;
        final int rectLeft = resolution.getScaledWidth() / 2 - rectWidth / 2;
        final int rectRight = resolution.getScaledWidth() / 2 + rectWidth / 2;
        final int loadingWidth = ( int ) ( ( rectRight - rectLeft ) * percentage );

        Gui.drawRect(rectLeft, 180, rectRight, 180 + rectHeight, new Color(255, 255, 255, 200).getRGB());
        Gui.drawRect(rectLeft + 1, 180 + 1, rectRight - 1, 180 + rectHeight - 1, GreyToneColor.DARK_GREY.getRGB());
        Gui.drawGradientHorizontal(rectLeft + 2, 180 + 2, rectLeft + loadingWidth - 2, 180 + rectHeight - 2, CloudColor.DESIRE.getRGB(), CloudColor.ROYAL.getRGB());
    }

    /**
     * Reset the GL color and texture state.
     */
    private void resetState ()
    {
        GlStateManager.resetColor();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.textureState[GlStateManager.activeTextureUnit].textureName = -1;
    }

    public boolean isActive ()
    {
        return active;
    }

    public void setActive (final boolean active)
    {
        this.active = active;
    }
}
