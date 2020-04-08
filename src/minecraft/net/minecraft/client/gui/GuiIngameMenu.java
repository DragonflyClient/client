package net.minecraft.client.gui;

import net.inceptioncloud.minecraftmod.InceptionMod;
import net.inceptioncloud.minecraftmod.design.color.CloudColor;
import net.inceptioncloud.minecraftmod.design.color.GreyToneColor;
import net.inceptioncloud.minecraftmod.design.font.IFontRenderer;
import net.inceptioncloud.minecraftmod.transition.number.DoubleTransition;
import net.inceptioncloud.minecraftmod.transition.number.SmoothDoubleTransition;
import net.inceptioncloud.minecraftmod.transition.supplier.ForwardBackward;
import net.inceptioncloud.minecraftmod.transition.supplier.ForwardNothing;
import net.inceptioncloud.minecraftmod.ui.components.*;
import net.inceptioncloud.minecraftmod.utils.RenderUtils;
import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.realms.RealmsBridge;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class GuiIngameMenu extends GuiScreen
{
    /**
     * True when the user requested to close the gui.
     */
    private boolean closeRequested = false;

    /**
     * Manages the fade-in of the background gradient.
     */
    private final SmoothDoubleTransition transitionBackground = SmoothDoubleTransition.builder().start(0.0F).end(0.7F).fadeIn(0).stay(20).fadeOut(20).autoTransformator(( ForwardBackward ) () -> mc != null && mc.currentScreen instanceof GuiIngameMenu && !closeRequested).build();

    /**
     * Builds the box of the Game Menu.
     */
    private final SmoothDoubleTransition transitionBox = SmoothDoubleTransition.builder().start(0).end(1).fadeIn(30).stay(20).fadeOut(0).autoTransformator(( ForwardBackward ) () -> mc != null && mc.currentScreen instanceof GuiIngameMenu).build();

    /**
     * Builds the header of the Game Menu.
     */
    private final SmoothDoubleTransition transitionHeader = SmoothDoubleTransition.builder().start(0).end(1).fadeIn(0).stay(30).fadeOut(30).autoTransformator(( ForwardBackward ) transitionBox::isAtEnd).build();

    /**
     * The transition that closes the gui when the user requested.
     */
    private final SmoothDoubleTransition pushOffset =
        SmoothDoubleTransition.builder()
            .start(0).end(1)
            .fadeIn(30).stay(30).fadeOut(0)
            .autoTransformator(( ForwardNothing ) () -> closeRequested)
            .reachEnd(() -> {
                mc.displayGuiScreen(null);

                if (mc.currentScreen == null) {
                    mc.setIngameFocus();
                }
            })
            .build();

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui ()
    {
        /* Game Menu Box Dimensions */
        int cursorY = this.height / 4 + 25;
        Mouse.setGrabbed(false);
        Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() - cursorY * 2);

        this.buttonList.forEach(GuiButton::destroy);
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
        this.buttonList.add(this.mc.isSingleplayer() && !this.mc.getIntegratedServer().getPublic()
            ? new SimpleButton(7, this.width / 2 - 100, this.height / 4 + 108, "Open to LAN")
            : new ConfirmationButton(this, 7, this.width / 2 - 100, this.height / 4 + 108, "Back to Hub"));
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
                requestClose();
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
                    requestClose();
                } else this.mc.displayGuiScreen(new GuiShareToLan(this));
        }
    }

    private void requestClose ()
    {
        Mouse.setCursorPosition(0, 0);
        Mouse.setGrabbed(true);
        this.closeRequested = true;
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen (int mouseX, int mouseY, float partialTicks)
    {
        int pushOffset = ( int ) ( this.pushOffset.get() * this.height);

        /* Background */
        drawRect(0, 0, this.width, this.height, new Color(0F, 0F, 0F, ( float ) transitionBackground.get()).getRGB());

        /* Game Menu Box Dimensions */
        int padding = 10;
        int top = this.height / 4 + 12 - padding + pushOffset;
        int bottom = this.height / 4 + 152 + padding + pushOffset;
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
        this.buttonList.stream().filter(SimpleButton.class::isInstance).map(SimpleButton.class::cast).forEach(guiButton -> guiButton.setOpacity(( float ) transitionHeader.get()));

        /* Header */
        int headerWidth = ( int ) ( transitionHeader.get() * width );
        int headerCenter = left + ( width / 2 );
        int headerLeft = headerCenter - ( headerWidth / 2 );
        drawGradientHorizontal(headerLeft, top - 1, headerLeft + headerWidth, top, CloudColor.DESIRE.getRGB(), CloudColor.ROYAL.getRGB());

        /* Game Menu Box */
        drawRect(left, top, right, bottom, GreyToneColor.DARK_GREY.getRGB());

        /* Title */
        final float opacity = ( float ) transitionHeader.get();
        final Color color = new Color(1, 1, 1, Math.max(0.05f, opacity));
        final IFontRenderer fontRenderer = InceptionMod.getInstance().getFontDesign().retrieveOrBuild(" Medium", 22);

        drawCenteredString(fontRenderer, "Ingame Menu", left + ( width / 2 ), top + 9, color.getRGB());
        GlStateManager.color(1f, 1f, 1f, color.getAlpha() / 255f);
        RenderUtils.drawLine(left + ( width / 2D ) - 12, top + 23, left + ( width / 2D ) + 12, top + 23, 1);

        for (GuiButton guiButton : new ArrayList<>(this.buttonList)) {
            final int realY = guiButton.yPosition;
            guiButton.setPositionY(guiButton.yPosition + pushOffset);
            guiButton.drawButton(this.mc, mouseX, mouseY);
            guiButton.setPositionY(realY);
        }

        for (GuiLabel guiLabel : new ArrayList<>(this.labelList)) {
            guiLabel.drawLabel(this.mc, mouseX, mouseY);
        }
    }

    @Override
    protected void keyTyped (final char typedChar, final int keyCode) throws IOException
    {
        if (keyCode == 1) {
            requestClose();
        }
    }

    @Override
    public void onGuiClosed ()
    {
        transitionBackground.destroy();
        transitionBox.destroy();
        transitionHeader.destroy();
        pushOffset.destroy();

        super.onGuiClosed();
    }
}
