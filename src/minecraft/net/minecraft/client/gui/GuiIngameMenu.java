package net.minecraft.client.gui;

import net.inceptioncloud.minecraftmod.InceptionMod;
import net.inceptioncloud.minecraftmod.design.color.CloudColor;
import net.inceptioncloud.minecraftmod.design.color.GreyToneColor;
import net.inceptioncloud.minecraftmod.transition.number.DoubleTransition;
import net.inceptioncloud.minecraftmod.transition.number.OverflowDoubleTransition;
import net.inceptioncloud.minecraftmod.transition.supplier.ForwardBackward;
import net.inceptioncloud.minecraftmod.ui.components.SimpleButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.realms.RealmsBridge;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;

import java.awt.*;
import java.io.IOException;
import java.util.function.Consumer;

public class GuiIngameMenu extends GuiScreen
{
    /**
     * Manages the fade-in of the background gradient.
     */
    private DoubleTransition transitionBackground = DoubleTransition.builder().start(0.0F).end(0.8F).amountOfSteps(70).autoTransformator(( ForwardBackward ) () -> mc.currentScreen instanceof GuiIngameMenu).build();

    /**
     * Builds the box of the Game Menu.
     */
    private DoubleTransition transitionBox = DoubleTransition.builder().start(0).end(1).amountOfSteps(40).autoTransformator(( ForwardBackward ) () -> mc.currentScreen instanceof GuiIngameMenu).build();

    /**
     * Builds the header of the Game Menu.
     */
    private DoubleTransition transitionHeader = DoubleTransition.builder().start(0).end(1).amountOfSteps(20).autoTransformator(( ForwardBackward ) () -> transitionBox.isAtEnd()).build();

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui ()
    {
        this.buttonList.clear();

        /* Back to Game */
        this.buttonList.add(new SimpleButton(4, this.width / 2 - 100, this.height / 4 + 12, "Back to Game"));
        /* Achievements */
        this.buttonList.add(new SimpleButton(5, this.width / 2 - 100, this.height / 4 + 36, "Achievements"));
        /* Statistics */
        this.buttonList.add(new SimpleButton(6, this.width / 2 - 100, this.height / 4 + 60, "Statistics"));
        /* Options */
        this.buttonList.add(new SimpleButton(0, this.width / 2 - 100, this.height / 4 + 84, "Options"));
        /* Open to LAN / Lobby */
        this.buttonList.add(new SimpleButton(7, this.width / 2 - 100, this.height / 4 + 108, this.mc.isSingleplayer() && !this.mc.getIntegratedServer().getPublic() ? "Open to LAN" : "Back to Hub"));
        /* Quit World */
        this.buttonList.add(new SimpleButton(1, this.width / 2 - 100, this.height / 4 + 132, this.mc.isIntegratedServerRunning() ? "Save and Quit to Title" : "Disconnect"));
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed (GuiButton button) throws IOException
    {
        switch (button.id) {
            case 0:
                this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
                break;

            case 1:
                boolean flag = this.mc.isIntegratedServerRunning();
                boolean flag1 = this.mc.isConnectedToRealms();
                button.enabled = false;
                this.mc.theWorld.sendQuittingDisconnectingPacket();
                this.mc.loadWorld(null);

                if (flag) {
                    this.mc.displayGuiScreen(new GuiMainMenu());
                } else if (flag1) {
                    RealmsBridge realmsbridge = new RealmsBridge();
                    realmsbridge.switchToRealms(new GuiMainMenu());
                } else {
                    this.mc.displayGuiScreen(new GuiMultiplayer(new GuiMainMenu()));
                }

            case 2:
            case 3:
            default:
                break;

            case 4:
                this.mc.displayGuiScreen(null);
                this.mc.setIngameFocus();
                break;

            case 5:
                this.mc.displayGuiScreen(new GuiAchievements(this, this.mc.thePlayer.getStatFileWriter()));
                break;

            case 6:
                this.mc.displayGuiScreen(new GuiStats(this, this.mc.thePlayer.getStatFileWriter()));
                break;

            case 7:
                if (button.displayString.contains("Hub"))
                    GuiChat.sendChatMessage("/hub", false);
                else
                    this.mc.displayGuiScreen(new GuiShareToLan(this));
        }
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen ()
    {
        super.updateScreen();
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen (int mouseX, int mouseY, float partialTicks)
    {
        drawGradientVertical(0, 0, width, height,
            new Color(0F, 0F, 0F, ( float ) transitionBackground.get()).getRGB(),
            new Color(0F, 0F, 0F, ( float ) ( transitionBackground.get() / 2 )).getRGB());

        /* Game Menu Box Dimensions */
        int padding = 10;
        int top = this.height / 4 + 12 - padding;
        int bottom = this.height / 4 + 152 + padding;
        int left = this.width / 2 - 100 - padding;
        int right = this.width / 2 + 100 + padding;

        /* Apply transition dimensions to the location */
        final double factor = 1 - transitionBox.get();
        int height = ( int ) ( ( bottom - top ) * factor );
        int width = ( int ) ( ( right - left ) * factor );
        top += height / 2;
        bottom -= height / 2;
        left += width / 2;
        right -= width / 2;
        this.buttonList.stream().map(SimpleButton.class::cast).forEach(guiButton -> guiButton.setOpacity(( float ) transitionHeader.get()));

        /* Header */
        int headerHeight = 25;
        int headerTop = ( int ) ( top - ( ( 1 + headerHeight ) * transitionHeader.get() ) );
        int headerBottom = headerTop + headerHeight;
        final int alpha = ( int ) ( Math.max(0.1, transitionHeader.get()) * 255 );
        drawGradientHorizontal(left, headerTop, right, headerBottom, CloudColor.FUSION.getRGB(), CloudColor.ROYAL.getRGB());
        drawCenteredString(InceptionMod.getInstance().getFontDesign().getMedium(), "Game Menu", left + ( right - left ) / 2, top - 16,
            new Color(255, 255, 255, alpha).getRGB());

        /* Game Menu Box */
        drawGradientVertical(left, top, right, bottom, GreyToneColor.GREY.getRGB(), GreyToneColor.DARK_GREY.getRGB());

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
