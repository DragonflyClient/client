package net.inceptioncloud.minecraftmod.ui

import net.inceptioncloud.minecraftmod.InceptionMod
import net.inceptioncloud.minecraftmod.design.color.BluePalette
import net.inceptioncloud.minecraftmod.ui.components.button.ImageButton
import net.inceptioncloud.minecraftmod.utils.RenderUtils
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation

/**
 * A modern UI that displays information about the Minecraft Mod.
 *
 * The screen contains a window that is split into 4 sections, the title, information about the network,
 * developer (me :D) and credits to other sources like icons and fonts.
 *
 * @property parentScreen the screen from which the UI was opened
 */
class AboutUI(val parentScreen: GuiScreen) : GuiScreen()
{
    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    override fun initGui()
    {
        val iconSize = width.coerceAtMost(height) / 15
        buttonList.add(ImageButton(1, 5, height - 5 - iconSize, iconSize, iconSize, ResourceLocation("inceptioncloud/icons/about/back.png")))
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float)
    {
        if (mc.displayWidth >= 400 && mc.displayHeight >= 400)
        {
            Gui.drawRect(0, 0, width, height, BluePalette.BACKGROUND.rgb)

            val wHeight = (height / 1.3).toInt()
            val wWidth = (wHeight / 1.2).toInt()
            val wX = width / 2 - wWidth / 2
            val wY = height / 2 - wHeight / 2

            val header = wHeight / 11
            val body = wHeight - header
            val fontRenderer = InceptionMod.getInstance().fontDesign.retrieveOrBuild(" Medium", (header * 1.1).toInt())

            // Header
            RenderUtils.drawRoundRect(wX, wY, wWidth, header, 100, BluePalette.PRIMARY)
            Gui.drawRect(wX, wY + header / 2, wX + wWidth, wY + header, BluePalette.PRIMARY.rgb)
            fontRenderer.drawCenteredString("About", wX + wWidth / 2, wY + header / 2 - fontRenderer.height / 4,
                    BluePalette.FOREGROUND.rgb, false)

            // Body
            RenderUtils.drawRoundRect(wX, wY + body, wWidth, header, 100, BluePalette.FOREGROUND)
            Gui.drawRect(wX, wY + header, wX + wWidth, wY + wHeight - header / 2, BluePalette.FOREGROUND.rgb)

            //region Content
            val titleFont = InceptionMod.getInstance().fontDesign.retrieveOrBuild("", (header * 1.15).toInt())
            val largeFont = InceptionMod.getInstance().fontDesign.retrieveOrBuild("", header / 1)
            val smallFont = InceptionMod.getInstance().fontDesign.retrieveOrBuild("", (header / 1.5).toInt())
            val largeColor = 0x545454
            val smallColor = 0x6D6D6D
            val imageSize = (smallFont.height * 1.2).toInt()
            var textY = wY + header + 10

            //region Title
            titleFont.drawCenteredString("Inception Cloud Minecraft Mod", wX + wWidth / 2, textY, largeColor, false)
            textY += titleFont.height + 2
            smallFont.drawCenteredString(InceptionMod.getVersion(), wX + wWidth / 2, textY, smallColor, false)
            textY += smallFont.height + 12
            //endregion

            //region Network
            largeFont.drawString("part of the Inception Cloud Network", wX + 10F, textY.toFloat(), largeColor, false)
            textY += largeFont.height + 2

            smallFont.drawString("visit http://inceptioncloud.net for more information", wX + 10F, textY.toFloat(), smallColor, false)
            textY += smallFont.height + 1

            drawIcon("twitter", wX + 20, textY, imageSize)
            smallFont.drawString("@inceptioncloud", wX + 22F + imageSize, textY + 3F, smallColor, false)
            textY += imageSize + 2

            drawIcon("instagram", wX + 20, textY, imageSize)
            smallFont.drawString("@inceptioncloud", wX + 22F + imageSize, textY + 3F, smallColor, false)
            textY += imageSize + 2

            drawIcon("discord", wX + 20, textY, imageSize)
            smallFont.drawString("https://discord.gg/DJRb4fF", wX + 22F + imageSize, textY + 3F, smallColor, false)
            textY += 20
            //endregion

            //region Developer
            drawIcon("heart", wX + 10 + largeFont.getStringWidth("developed with"), textY - (imageSize / 4), (imageSize * 1.5).toInt())
            largeFont.drawString("developed with", wX + 10F, textY.toFloat(), largeColor, false)
            largeFont.drawString("by inception", (wX + 11F + (imageSize * 1.5) + largeFont.getStringWidth("developed with"))
                .toFloat(), textY.toFloat(), largeColor, false)
            textY += largeFont.height + 2

            drawIcon("twitter", wX + 20, textY, imageSize)
            smallFont.drawString("@theincxption", wX + 22F + imageSize, textY + 3F, smallColor, false)
            textY += imageSize + 2

            drawIcon("instagram", wX + 20, textY, imageSize)
            smallFont.drawString("@theincxption", wX + 22F + imageSize, textY + 3F, smallColor, false)
            textY += imageSize + 2

            drawIcon("github", wX + 20, textY, imageSize)
            smallFont.drawString("@theincxption", wX + 22F + imageSize, textY + 3F, smallColor, false)
            textY += 20
            //endregion

            //region Credits
            largeFont.drawString("credits", wX + 10F, textY.toFloat(), largeColor, false)
            textY += largeFont.height + 2

            smallFont.drawString("icons by https://icons8.com", wX + 20F, textY + 3F, smallColor, false)
            textY += smallFont.height + 2

            smallFont.drawString("fonts by https://fonts.google.com", wX + 20F, textY + 3F, smallColor, false)
            //endregion

            //endregion

            super.drawScreen(mouseX, mouseY, partialTicks)
        } else
        {
            drawSizeNotSupported()
        }
    }

    /**
     * A quick method that draws a icon from the "inceptioncloud/icon/about/" folder into the gui.
     */
    fun drawIcon(name: String, x: Int, y: Int, size: Int)
    {
        mc.textureManager.bindTexture(ResourceLocation("inceptioncloud/icons/about/$name.png"))
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F)
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0F, 0F, size, size, size.toFloat(), size.toFloat())
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    override fun actionPerformed(button: GuiButton?)
    {
        if (button?.id == 1)
        {
            mc.displayGuiScreen(parentScreen)
        }
    }
}