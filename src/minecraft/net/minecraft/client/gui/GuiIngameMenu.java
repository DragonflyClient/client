package net.minecraft.client.gui;

import net.inceptioncloud.minecraftmod.InceptionMod;
import net.inceptioncloud.minecraftmod.design.color.CloudColor;
import net.inceptioncloud.minecraftmod.design.color.GreyToneColor;
import net.inceptioncloud.minecraftmod.design.font.IFontRenderer;
import net.inceptioncloud.minecraftmod.transition.number.DoubleTransition;
import net.inceptioncloud.minecraftmod.transition.supplier.ForwardBackward;
import net.inceptioncloud.minecraftmod.ui.components.ConfirmationButton;
import net.inceptioncloud.minecraftmod.ui.components.SimpleButton;
import net.inceptioncloud.minecraftmod.utils.RenderUtils;
import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.realms.RealmsBridge;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import java.awt.*;
import java.io.IOException;

public class GuiIngameMenu extends GuiScreen
{
    /**
     * Manages the fade-in of the background gradient.
     */
    private final DoubleTransition transitionBackground = DoubleTransition.builder().start(0.0F).end(0.6F).amountOfSteps(80).autoTransformator(( ForwardBackward ) () -> mc.currentScreen instanceof GuiIngameMenu).build();

    /**
     * Builds the box of the Game Menu.
     */
    private final DoubleTransition transitionBox = DoubleTransition.builder().start(0).end(1).amountOfSteps(30).autoTransformator(( ForwardBackward ) () -> mc.currentScreen instanceof GuiIngameMenu).build();

    /**
     * Builds the header of the Game Menu.
     */
    private final DoubleTransition transitionHeader = DoubleTransition.builder().start(0).end(1).amountOfSteps(50).autoTransformator(( ForwardBackward ) transitionBox::isAtEnd).build();

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui ()
    {
        /* Game Menu Box Dimensions */
        int cursorY = this.height / 4 + 25;
        Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() - cursorY * 2);

        this.buttonList.clear();

        /* Back to Game */
        this.buttonList.add(new SimpleButton(4, this.width / 2 - 100, this.height / 4 + 36, "Back to Game"));
        /* Achievements */
        this.buttonList.add(new SimpleButton(5, this.width / 2 - 100, this.height / 4 + 60, 98, 20, "Achievements"));
        /* Statistics */
        this.buttonList.add(new SimpleButton(6, this.width / 2 + 2, this.height / 4 + 60, 98, 20, "Statistics"));
        /* Options */
        this.buttonList.add(new SimpleButton(0, this.width / 2 - 100, this.height / 4 + 84, "Options"));
        /* Open to LAN / Lobby */
        this.buttonList.add(new ConfirmationButton(this, 7, this.width / 2 - 100, this.height / 4 + 108, this.mc.isSingleplayer() && !this.mc.getIntegratedServer().getPublic() ? "Open to LAN" : "Back to Hub"));
        /* Quit World */
        this.buttonList.add(new ConfirmationButton(this, 1, this.width / 2 - 100, this.height / 4 + 132, this.mc.isIntegratedServerRunning() ? "Save and Quit to Title" : "Disconnect"));
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
                if (button.displayString.equals("Back to Hub")) {
                    GuiChat.sendChatMessage("/hub", false);
                    mc.displayGuiScreen(null);
                } else this.mc.displayGuiScreen(new GuiShareToLan(this));
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
        /* Background */
        drawRect(0, 0, this.width, this.height, new Color(0F, 0F, 0F, ( float ) transitionBackground.get()).getRGB());

        /* Game Menu Box Dimensions */
        int padding = 10;
        int top = this.height / 4 + 12 - padding;
        int bottom = this.height / 4 + 152 + padding;
        int left = this.width / 2 - 100 - padding;
        int right = this.width / 2 + 100 + padding;
        int width = right - left;

        /* Apply transition dimensions to the location */
        final double factor = 1 - transitionBox.get();
        int subWidth = ( int ) ( ( right - left ) * factor );
        int subHeight = ( int ) ( ( bottom - top ) * factor );
        top += subHeight / 2;
        bottom -= subHeight / 2;
        left += subWidth / 2;
        right -= subWidth / 2;
        this.buttonList.stream().map(SimpleButton.class::cast).forEach(guiButton -> guiButton.setOpacity(( float ) transitionHeader.get()));

        /* Header */
        int headerWidth = ( int ) ( transitionHeader.get() * width );
        int headerCenter = left + ( width / 2 );
        int headerLeft = headerCenter - ( headerWidth / 2 );
        drawGradientHorizontal(headerLeft, top - 1, headerLeft + headerWidth, top, CloudColor.FUSION.getRGB(), CloudColor.ROYAL.getRGB());

        /* Game Menu Box */
        drawRect(left, top, right, bottom, GreyToneColor.DARK_GREY.getRGB());

        /* Title */
        final float opacity = ( float ) transitionHeader.get();
        final Color color = new Color(1, 1, 1, Math.max(0.05f, opacity));
        final IFontRenderer fontRenderer = InceptionMod.getInstance().getFontDesign().retrieveOrBuild("Product Sans Medium", Font.PLAIN, 22);

        drawCenteredString(fontRenderer, "Ingame Menu", left + ( width / 2 ), top + 9, color.getRGB());
        GlStateManager.color(1f, 1f, 1f, color.getAlpha() / 255f);
        RenderUtils.drawLine(left + ( width / 2D ) - 12, top + 23, left + ( width / 2D ) + 12, top + 23, 1);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void onGuiClosed ()
    {
        super.onGuiClosed();
    }
}
