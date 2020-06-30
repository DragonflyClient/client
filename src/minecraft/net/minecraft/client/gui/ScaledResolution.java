package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;

public class ScaledResolution
{
    private final double scaledWidthD;
    private final double scaledHeightD;
    private int scaledWidth;
    private int scaledHeight;
    private int scaleFactor;

    public ScaledResolution (Minecraft mc)
    {
        this.scaledWidth = mc.displayWidth;
        this.scaledHeight = mc.displayHeight;
        this.scaleFactor = 1;
        boolean flag = mc.isUnicode();
        int i = mc.gameSettings.guiScale;

        if (i == 0) // If "Auto" is selected
        {
            i = 1000;
        }

//        if (Minecraft.getMinecraft().currentScreen != null && Minecraft.getMinecraft().currentScreen.getClass().getName().startsWith("net.minecraft.client.gui.inventory")) {
//            StackTraceElement stackTraceElement = RuntimeUtils.getStackTrace(ScaledResolution.class);
//
//            if (!( stackTraceElement != null && stackTraceElement.getClassName().contains("GuiIngame") ))
//                i = 3;
//        }

        while (this.scaleFactor < i && this.scaledWidth / ( this.scaleFactor + 1 ) >= 320 && this.scaledHeight / ( this.scaleFactor + 1 ) >= 240) {
            ++this.scaleFactor;
        }

        if (flag && this.scaleFactor % 2 != 0 && this.scaleFactor != 1) {
            --this.scaleFactor;
        }

        this.scaledWidthD = ( double ) this.scaledWidth / ( double ) this.scaleFactor; // 12 / 2 = 6 | 12 / 3 = 4 | 6 * 2/3 = 4
        this.scaledHeightD = ( double ) this.scaledHeight / ( double ) this.scaleFactor;
        this.scaledWidth = MathHelper.ceiling_double_int(this.scaledWidthD);
        this.scaledHeight = MathHelper.ceiling_double_int(this.scaledHeightD);
    }

    public int getScaledWidth ()
    {
        return this.scaledWidth;
    }

    public int getScaledHeight ()
    {
        return this.scaledHeight;
    }

    public double getScaledWidth_double ()
    {
        return this.scaledWidthD;
    }


    public double getScaledHeight_double ()
    {
        return this.scaledHeightD;
    }

    public int getScaleFactor ()
    {
        return this.scaleFactor;
    }
}
