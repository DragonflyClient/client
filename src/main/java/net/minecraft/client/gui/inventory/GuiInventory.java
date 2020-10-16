package net.minecraft.client.gui.inventory;

import net.inceptioncloud.dragonfly.options.sections.OptionsSectionUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class GuiInventory extends InventoryEffectRenderer
{
    /**
     * The scale factor for the inventory which can be customized using the custom inventory
     * scale setting.
     */
    private Double scaleFactor = null;

    /**
     * The old x position of the mouse pointer
     */
    private float oldMouseX;

    /**
     * The old y position of the mouse pointer
     */
    private float oldMouseY;

    public GuiInventory (EntityPlayer p_i1094_1_)
    {
        super(p_i1094_1_.inventoryContainer);
        this.allowUserInput = true;
    }

    /**
     * Draws the entity to the screen. Args: xPos, yPos, scale, mouseX, mouseY, entityLiving
     */
    public static void drawEntityOnScreen (int posX, int posY, int scale, float mouseX, float mouseY, EntityLivingBase ent)
    {
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate(( float ) posX, ( float ) posY, 50.0F);
        GlStateManager.scale(( float ) ( -scale ), ( float ) scale, ( float ) scale);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        float f = ent.renderYawOffset;
        float f1 = ent.rotationYaw;
        float f2 = ent.rotationPitch;
        float f3 = ent.prevRotationYawHead;
        float f4 = ent.rotationYawHead;
        GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-( ( float ) Math.atan(mouseY / 40.0F) ) * 20.0F, 1.0F, 0.0F, 0.0F);
        ent.renderYawOffset = ( float ) Math.atan(mouseX / 40.0F) * 20.0F;
        ent.rotationYaw = ( float ) Math.atan(mouseX / 40.0F) * 40.0F;
        ent.rotationPitch = -( ( float ) Math.atan(mouseY / 40.0F) ) * 20.0F;
        ent.rotationYawHead = ent.rotationYaw;
        ent.prevRotationYawHead = ent.rotationYaw;
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        rendermanager.setPlayerViewY(180.0F);
        rendermanager.setRenderShadow(false);
        rendermanager.renderEntityWithPosYaw(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
        rendermanager.setRenderShadow(true);
        ent.renderYawOffset = f;
        ent.rotationYaw = f1;
        ent.rotationPitch = f2;
        ent.prevRotationYawHead = f3;
        ent.rotationYawHead = f4;
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen ()
    {
        if (this.mc.playerController.isInCreativeMode()) {
            LogManager.getLogger().info("Switched from survival to creative inventory.");
            this.mc.displayGuiScreen(new GuiContainerCreative(this.mc.thePlayer));
        }

        this.updateActivePotionEffects();
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui ()
    {
        this.buttonList.clear();

        if (this.mc.playerController.isInCreativeMode()) {
            this.mc.displayGuiScreen(new GuiContainerCreative(this.mc.thePlayer));
        } else {
            super.initGui();
        }
    }

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height) {
        this.updateScaleFactor();
        super.setWorldAndResolution(mc, width, height);
    }

    /**
     * Updates the {@link #scaleFactor} property based on the custom inventory scale setting.
     */
    private void updateScaleFactor() {
        final Integer customInventoryScale = OptionsSectionUI.getCustomInventoryScale().invoke();
        if (customInventoryScale != null && customInventoryScale != -1) {
            if (customInventoryScale == 0) {
                this.scaleFactor = (double) calculateAutoGuiScale();
            } else {
                this.scaleFactor = (double) customInventoryScale;
            }
        } else {
            this.scaleFactor = null;
        }
    }

    /**
     * Calculates the scale factor if "auto" is selected as the custom inventory scale.
     */
    private static Integer calculateAutoGuiScale() {
        Minecraft mc = Minecraft.getMinecraft();
        int scaledWidth = mc.displayWidth;
        int scaledHeight = mc.displayHeight;
        int scaleFactor = 1;
        boolean flag = mc.isUnicode();
        int i = 1000;

        while (scaleFactor < i && scaledWidth / ( scaleFactor + 1 ) >= 320 && scaledHeight / ( scaleFactor + 1 ) >= 240) {
            ++scaleFactor;
        }

        if (flag && scaleFactor % 2 != 0 && scaleFactor != 1) {
            --scaleFactor;
        }

        return scaleFactor;
    }

    @Nullable
    @Override
    public Double getCustomScaleFactor() {
        return scaleFactor;
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items). Args : mouseX, mouseY
     */
    protected void drawGuiContainerForegroundLayer (int mouseX, int mouseY)
    {
        this.fontRendererObj.drawString(I18n.format("container.crafting"), 86, 16, 4210752);
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    @Override
    public void drawScreen (int mouseX, int mouseY, float partialTicks)
    {

        super.drawScreen(mouseX, mouseY, partialTicks);
        this.oldMouseX = ( float ) mouseX;
        this.oldMouseY = ( float ) mouseY;
    }

    /**
     * Args : renderPartialTicks, mouseX, mouseY
     */
    protected void drawGuiContainerBackgroundLayer (float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(inventoryBackground);
        int i = this.guiLeft;
        int j = this.guiTop;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
        drawEntityOnScreen(i + 51, j + 75, 30, ( float ) ( i + 51 ) - this.oldMouseX, ( float ) ( j + 75 - 50 ) - this.oldMouseY, this.mc.thePlayer);
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed (GuiButton button) throws IOException
    {

        if (button.id == 0) {
            this.mc.displayGuiScreen(new GuiAchievements(this, this.mc.thePlayer.getStatFileWriter()));
        }

        if (button.id == 1) {
            this.mc.displayGuiScreen(new GuiStats(this, this.mc.thePlayer.getStatFileWriter()));
        }
    }
}
