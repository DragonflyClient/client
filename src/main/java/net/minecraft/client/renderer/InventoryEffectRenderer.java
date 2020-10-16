package net.minecraft.client.renderer;

import java.util.Collection;

import net.inceptioncloud.dragonfly.options.sections.OptionsSectionUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.jetbrains.annotations.Nullable;

public abstract class InventoryEffectRenderer extends GuiContainer
{
    /**
     * The scale factor for the inventory which can be customized using the custom inventory
     * scale setting.
     */
    private Double scaleFactor = null;

    /** True if there is some potion effect to display */
    private boolean hasActivePotionEffects;

    public InventoryEffectRenderer(Container inventorySlotsIn)
    {
        super(inventorySlotsIn);
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        super.initGui();
        this.updateActivePotionEffects();
    }

    protected void updateActivePotionEffects()
    {
        if (!this.mc.thePlayer.getActivePotionEffects().isEmpty())
        {
            this.guiLeft = 160 + (this.width - this.xSize - 200) / 2;
            this.hasActivePotionEffects = true;
        }
        else
        {
            this.guiLeft = (this.width - this.xSize) / 2;
            this.hasActivePotionEffects = false;
        }
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (this.hasActivePotionEffects)
        {
            this.drawActivePotionEffects();
        }
    }

    /**
     * Display the potion effects list
     */
    private void drawActivePotionEffects()
    {
        int i = this.guiLeft - 124;
        int j = this.guiTop;
        int k = 166;
        Collection<PotionEffect> collection = this.mc.thePlayer.getActivePotionEffects();

        if (!collection.isEmpty())
        {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableLighting();
            int l = 33;

            if (collection.size() > 5)
            {
                l = 132 / (collection.size() - 1);
            }

            for (PotionEffect potioneffect : this.mc.thePlayer.getActivePotionEffects())
            {
                Potion potion = Potion.potionTypes[potioneffect.getPotionID()];
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                this.mc.getTextureManager().bindTexture(inventoryBackground);
                this.drawTexturedModalRect(i, j, 0, 166, 140, 32);

                if (potion.hasStatusIcon())
                {
                    int i1 = potion.getStatusIconIndex();
                    this.drawTexturedModalRect(i + 6, j + 7, 0 + i1 % 8 * 18, 198 + i1 / 8 * 18, 18, 18);
                }

                String s1 = I18n.format(potion.getName(), new Object[0]);

                if (potioneffect.getAmplifier() == 1)
                {
                    s1 = s1 + " " + I18n.format("enchantment.level.2", new Object[0]);
                }
                else if (potioneffect.getAmplifier() == 2)
                {
                    s1 = s1 + " " + I18n.format("enchantment.level.3", new Object[0]);
                }
                else if (potioneffect.getAmplifier() == 3)
                {
                    s1 = s1 + " " + I18n.format("enchantment.level.4", new Object[0]);
                }

                this.fontRendererObj.drawStringWithShadow(s1, (float)(i + 10 + 18), (float)(j + 6), 16777215);
                String s = Potion.getDurationString(potioneffect);
                this.fontRendererObj.drawStringWithShadow(s, (float)(i + 10 + 18), (float)(j + 6 + 10), 8355711);
                j += l;
            }
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
}
