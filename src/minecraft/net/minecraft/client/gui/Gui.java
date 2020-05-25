package net.minecraft.client.gui;

import net.inceptioncloud.minecraftmod.engine.font.renderer.IFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

public class Gui
{
    public static final ResourceLocation optionsBackground = new ResourceLocation("textures/gui/options_background.png");
    public static final ResourceLocation statIcons = new ResourceLocation("textures/gui/container/stats_icons.png");
    public static final ResourceLocation icons = new ResourceLocation("textures/gui/icons.png");
    protected float zLevel;

    /**
     * Draw a 1 pixel wide horizontal line. Args: x1, x2, y, color
     */
    public static void drawHorizontalLine (int startX, int endX, int y, int color)
    {
        if (endX < startX) {
            int i = startX;
            startX = endX;
            endX = i;
        }

        drawRect(startX, y, endX, y + 1, color);
    }

    /**
     * Draw a 1 pixel wide vertical line. Args : x, y1, y2, color
     */
    public static void drawVerticalLine (int x, int startY, int endY, int color)
    {
        if (endY < startY) {
            int i = startY;
            startY = endY;
            endY = i;
        }

        drawRect(x, startY + 1, x + 1, endY, color);
    }

    /**
     * Draws a solid color rectangle with the specified coordinates and color (ARGB format). Args: x1, y1, x2, y2, color
     */
    public static void drawRect (int left, int top, int right, int bottom, int color)
    {
        if (left < right) {
            int i = left;
            left = right;
            right = i;
        }

        if (top < bottom) {
            int j = top;
            top = bottom;
            bottom = j;
        }

        float f3 = ( float ) ( color >> 24 & 255 ) / 255.0F;
        float f = ( float ) ( color >> 16 & 255 ) / 255.0F;
        float f1 = ( float ) ( color >> 8 & 255 ) / 255.0F;
        float f2 = ( float ) ( color & 255 ) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(f, f1, f2, f3);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(left, bottom, 0.0D).endVertex();
        worldrenderer.pos(right, bottom, 0.0D).endVertex();
        worldrenderer.pos(right, top, 0.0D).endVertex();
        worldrenderer.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    /**
     * Draws a solid color rectangle with the specified coordinates and color (ARGB format). Args: x1, y1, x2, y2, color
     */
    public static void drawRect (double left, double top, double right, double bottom, int color)
    {
        if (left < right) {
            double i = left;
            left = right;
            right = i;
        }

        if (top < bottom) {
            double j = top;
            top = bottom;
            bottom = j;
        }

        float f3 = ( float ) ( color >> 24 & 255 ) / 255.0F;
        float f = ( float ) ( color >> 16 & 255 ) / 255.0F;
        float f1 = ( float ) ( color >> 8 & 255 ) / 255.0F;
        float f2 = ( float ) ( color & 255 ) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(f, f1, f2, f3);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(left, bottom, 0.0D).endVertex();
        worldrenderer.pos(right, bottom, 0.0D).endVertex();
        worldrenderer.pos(right, top, 0.0D).endVertex();
        worldrenderer.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    /**
     * Draws a rectangle with a vertical gradient between the specified colors (ARGB format). Args : x1, y1, x2, y2,
     * topColor, bottomColor
     */
    public static void drawGradientHorizontal (int left, int top, int right, int bottom, int startColor, int endColor)
    {
        float start_a = ( float ) ( startColor >> 24 & 255 ) / 255.0F;
        float start_r = ( float ) ( startColor >> 16 & 255 ) / 255.0F;
        float start_g = ( float ) ( startColor >> 8 & 255 ) / 255.0F;
        float start_b = ( float ) ( startColor & 255 ) / 255.0F;
        float end_a = ( float ) ( endColor >> 24 & 255 ) / 255.0F;
        float end_r = ( float ) ( endColor >> 16 & 255 ) / 255.0F;
        float end_g = ( float ) ( endColor >> 8 & 255 ) / 255.0F;
        float end_b = ( float ) ( endColor & 255 ) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(left, top, 0).color(start_r, start_g, start_b, start_a).endVertex();
        worldrenderer.pos(left, bottom, 0).color(start_r, start_g, start_b, start_a).endVertex();
        worldrenderer.pos(right, bottom, 0).color(end_r, end_g, end_b, end_a).endVertex();
        worldrenderer.pos(right, top, 0).color(end_r, end_g, end_b, end_a).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    /**
     * Draws a rectangle with a vertical gradient between the specified colors (ARGB format). Args : x1, y1, x2, y2,
     * topColor, bottomColor
     */
    public static void drawGradientHorizontal (double left, double top, double right, double bottom, int startColor, int endColor)
    {
        float start_a = ( float ) ( startColor >> 24 & 255 ) / 255.0F;
        float start_r = ( float ) ( startColor >> 16 & 255 ) / 255.0F;
        float start_g = ( float ) ( startColor >> 8 & 255 ) / 255.0F;
        float start_b = ( float ) ( startColor & 255 ) / 255.0F;
        float end_a = ( float ) ( endColor >> 24 & 255 ) / 255.0F;
        float end_r = ( float ) ( endColor >> 16 & 255 ) / 255.0F;
        float end_g = ( float ) ( endColor >> 8 & 255 ) / 255.0F;
        float end_b = ( float ) ( endColor & 255 ) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(left, top, 0).color(start_r, start_g, start_b, start_a).endVertex();
        worldrenderer.pos(left, bottom, 0).color(start_r, start_g, start_b, start_a).endVertex();
        worldrenderer.pos(right, bottom, 0).color(end_r, end_g, end_b, end_a).endVertex();
        worldrenderer.pos(right, top, 0).color(end_r, end_g, end_b, end_a).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    /**
     * Draws a textured rectangle at z = 0. Args: x, y, u, v, width, height, textureWidth, textureHeight
     */
    public static void drawModalRectWithCustomSizedTexture (int x, int y, float u, float v, int width, int height, float textureWidth, float textureHeight)
    {
        float f = 1.0F / textureWidth;
        float f1 = 1.0F / textureHeight;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(x, y + height, 0.0D).tex(u * f, ( v + ( float ) height ) * f1).endVertex();
        worldrenderer.pos(x + width, y + height, 0.0D).tex(( u + ( float ) width ) * f, ( v + ( float ) height ) * f1).endVertex();
        worldrenderer.pos(x + width, y, 0.0D).tex(( u + ( float ) width ) * f, v * f1).endVertex();
        worldrenderer.pos(x, y, 0.0D).tex(u * f, v * f1).endVertex();
        tessellator.draw();
    }

    /**
     * Draws a scaled, textured, tiled modal rect at z = 0. This method isn't used anywhere in vanilla code.
     */
    public static void drawScaledCustomSizeModalRect (int x, int y, float u, float v, int uWidth, int vHeight, int width, int height, float tileWidth, float tileHeight)
    {
        float f = 1.0F / tileWidth;
        float f1 = 1.0F / tileHeight;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(x, y + height, 0.0D).tex(u * f, ( v + ( float ) vHeight ) * f1).endVertex();
        worldrenderer.pos(x + width, y + height, 0.0D).tex(( u + ( float ) uWidth ) * f, ( v + ( float ) vHeight ) * f1).endVertex();
        worldrenderer.pos(x + width, y, 0.0D).tex(( u + ( float ) uWidth ) * f, v * f1).endVertex();
        worldrenderer.pos(x, y, 0.0D).tex(u * f, v * f1).endVertex();
        tessellator.draw();
    }

    /**
     * Draws a rectangle with a vertical gradient between the specified colors (ARGB format). Args : x1, y1, x2, y2,
     * topColor, bottomColor
     */
    public void drawGradientVertical (int left, int top, int right, int bottom, int startColor, int endColor)
    {
        float f = ( float ) ( startColor >> 24 & 255 ) / 255.0F;
        float f1 = ( float ) ( startColor >> 16 & 255 ) / 255.0F;
        float f2 = ( float ) ( startColor >> 8 & 255 ) / 255.0F;
        float f3 = ( float ) ( startColor & 255 ) / 255.0F;
        float f4 = ( float ) ( endColor >> 24 & 255 ) / 255.0F;
        float f5 = ( float ) ( endColor >> 16 & 255 ) / 255.0F;
        float f6 = ( float ) ( endColor >> 8 & 255 ) / 255.0F;
        float f7 = ( float ) ( endColor & 255 ) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(right, top, this.zLevel).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos(left, top, this.zLevel).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos(left, bottom, this.zLevel).color(f5, f6, f7, f4).endVertex();
        worldrenderer.pos(right, bottom, this.zLevel).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    /**
     * Draws a gradient from the left top corner to the right bottom corner.
     */
    public void drawGradientLeftTopRightBottom (int left, int top, int right, int bottom, int startColor, int endColor)
    {
        float start_a = ( float ) ( startColor >> 24 & 255 ) / 255.0F;
        float start_r = ( float ) ( startColor >> 16 & 255 ) / 255.0F;
        float start_g = ( float ) ( startColor >> 8 & 255 ) / 255.0F;
        float start_b = ( float ) ( startColor & 255 ) / 255.0F;
        float end_a = ( float ) ( endColor >> 24 & 255 ) / 255.0F;
        float end_r = ( float ) ( endColor >> 16 & 255 ) / 255.0F;
        float end_g = ( float ) ( endColor >> 8 & 255 ) / 255.0F;
        float end_b = ( float ) ( endColor & 255 ) / 255.0F;
        float avg_a = ( start_a + end_a ) / 2F;
        float avg_r = ( start_r + end_r ) / 2F;
        float avg_g = ( start_g + end_g ) / 2F;
        float avg_b = ( start_b + end_b ) / 2F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(right, top, this.zLevel).color(avg_r, avg_g, avg_b, avg_a).endVertex();
        worldrenderer.pos(left, top, this.zLevel).color(start_r, start_g, start_b, start_a).endVertex();
        worldrenderer.pos(left, bottom, this.zLevel).color(avg_r, avg_g, avg_b, avg_a).endVertex();
        worldrenderer.pos(right, bottom, this.zLevel).color(end_r, end_g, end_b, end_a).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    /**
     * Renders the specified text to the screen, center-aligned. Args : renderer, string, x, y, color
     */
    public static void drawCenteredString (IFontRenderer fontRendererIn, String text, int x, int y, int color)
    {
        fontRendererIn.drawStringWithShadow(text, ( float ) ( x - fontRendererIn.getStringWidth(text) / 2 ), ( float ) y, color);
    }

    /**
     * Renders the specified text to the screen. Args : renderer, string, x, y, color
     */
    public static void drawString (FontRenderer fontRendererIn, String text, int x, int y, int color)
    {
        fontRendererIn.drawStringWithShadow(text, ( float ) x, ( float ) y, color);
    }

    /**
     * Draws a textured rectangle at the stored z-value. Args: x, y, u, v, width, height
     */
    public void drawTexturedModalRect (int x, int y, int textureX, int textureY, int width, int height)
    {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(x, y + height, this.zLevel).tex(( float ) ( textureX ) * f, ( float ) ( textureY + height ) * f1).endVertex();
        worldrenderer.pos(x + width, y + height, this.zLevel).tex(( float ) ( textureX + width ) * f, ( float ) ( textureY + height ) * f1).endVertex();
        worldrenderer.pos(x + width, y, this.zLevel).tex(( float ) ( textureX + width ) * f, ( float ) ( textureY ) * f1).endVertex();
        worldrenderer.pos(x, y, this.zLevel).tex(( float ) ( textureX ) * f, ( float ) ( textureY ) * f1).endVertex();
        tessellator.draw();
    }

    /**
     * Draws a textured rectangle using the texture currently bound to the TextureManager
     */
    public void drawTexturedModalRect (float xCoord, float yCoord, int minU, int minV, int maxU, int maxV)
    {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(xCoord + 0.0F, yCoord + ( float ) maxV, this.zLevel).tex(( float ) ( minU ) * f, ( float ) ( minV + maxV ) * f1).endVertex();
        worldrenderer.pos(xCoord + ( float ) maxU, yCoord + ( float ) maxV, this.zLevel).tex(( float ) ( minU + maxU ) * f, ( float ) ( minV + maxV ) * f1).endVertex();
        worldrenderer.pos(xCoord + ( float ) maxU, yCoord + 0.0F, this.zLevel).tex(( float ) ( minU + maxU ) * f, ( float ) ( minV ) * f1).endVertex();
        worldrenderer.pos(xCoord + 0.0F, yCoord + 0.0F, this.zLevel).tex(( float ) ( minU ) * f, ( float ) ( minV ) * f1).endVertex();
        tessellator.draw();
    }

    /**
     * Draws a texture rectangle using the texture currently bound to the TextureManager
     */
    public void drawTexturedModalRect (int xCoord, int yCoord, TextureAtlasSprite textureSprite, int widthIn, int heightIn)
    {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(xCoord, yCoord + heightIn, this.zLevel).tex(textureSprite.getMinU(), textureSprite.getMaxV()).endVertex();
        worldrenderer.pos(xCoord + widthIn, yCoord + heightIn, this.zLevel).tex(textureSprite.getMaxU(), textureSprite.getMaxV()).endVertex();
        worldrenderer.pos(xCoord + widthIn, yCoord, this.zLevel).tex(textureSprite.getMaxU(), textureSprite.getMinV()).endVertex();
        worldrenderer.pos(xCoord, yCoord, this.zLevel).tex(textureSprite.getMinU(), textureSprite.getMinV()).endVertex();
        tessellator.draw();
    }

    public static int getMouseX ()
    {
        if (Minecraft.getMinecraft().currentScreen == null)
            return 0;

        int scaleFactor = new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor();
        return Mouse.getX() / scaleFactor;
    }

    public static int getMouseY ()
    {
        if (Minecraft.getMinecraft().currentScreen == null)
            return 0;

        int scaleFactor = new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor();
        return Minecraft.getMinecraft().currentScreen.height - (Mouse.getY() / scaleFactor);
    }
}
