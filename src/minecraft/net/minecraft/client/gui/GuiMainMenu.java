package net.minecraft.client.gui;

import net.inceptioncloud.minecraftmod.Dragonfly;
import net.inceptioncloud.minecraftmod.design.color.GreyToneColor;
import net.inceptioncloud.minecraftmod.design.color.RGB;
import net.inceptioncloud.minecraftmod.engine.EngineTestUI;
import net.inceptioncloud.minecraftmod.engine.font.renderer.IFontRenderer;
import net.inceptioncloud.minecraftmod.transition.Transition;
import net.inceptioncloud.minecraftmod.transition.number.DoubleTransition;
import net.inceptioncloud.minecraftmod.transition.number.SmoothDoubleTransition;
import net.inceptioncloud.minecraftmod.transition.supplier.ForwardBackward;
import net.inceptioncloud.minecraftmod.transition.supplier.ForwardNothing;
import net.inceptioncloud.minecraftmod.ui.components.button.TransparentButton;
import net.inceptioncloud.minecraftmod.ui.mainmenu.QuickAction;
import net.inceptioncloud.minecraftmod.ui.mainmenu.multiplayer.DirectConnectAction;
import net.inceptioncloud.minecraftmod.ui.mainmenu.multiplayer.LastServerAction;
import net.inceptioncloud.minecraftmod.ui.mainmenu.options.ModOptionsAction;
import net.inceptioncloud.minecraftmod.ui.mainmenu.options.ResourcePackAction;
import net.inceptioncloud.minecraftmod.ui.mainmenu.quit.ReloadAction;
import net.inceptioncloud.minecraftmod.ui.mainmenu.quit.RestartAction;
import net.inceptioncloud.minecraftmod.ui.mainmenu.singleplayer.CreateMapAction;
import net.inceptioncloud.minecraftmod.ui.mainmenu.singleplayer.LastMapAction;
import net.inceptioncloud.minecraftmod.ui.renderer.RenderUtils;
import net.inceptioncloud.minecraftmod.version.InceptionCloudVersion;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.ISaveFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GLContext;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class GuiMainMenu extends GuiScreen implements GuiYesNoCallback
{
    public static final String informationText = "Please click " + EnumChatFormatting.UNDERLINE + "here" + EnumChatFormatting.RESET + " for more information.";
    private static final Logger logger = LogManager.getLogger();

    /**
     * The time when the GUI was first drawn.
     *
     * @see #addButtons() Which Button IDs are used
     */
    private static long drawTime = -1;

    /**
     * Provides the value for the fading in of the main menu after the splash screen.
     */
    private static final SmoothDoubleTransition fadeInTransition = SmoothDoubleTransition.builder()
        .start(1)
        .end(0)
        .fadeIn(75)
        .stay(75)
        .fadeOut(0)
        .autoTransformator((ForwardNothing) () -> drawTime != -1 && System.currentTimeMillis() - drawTime > 1000)
        .build();

    /**
     * Provides all available quick actions.
     */
    private final List<QuickAction> AVAILABLE_ACTIONS = new ArrayList<>(Arrays.asList(
        new LastMapAction(), new CreateMapAction(),
        new LastServerAction(), new DirectConnectAction(),
        new ResourcePackAction(), new ModOptionsAction(),
        new RestartAction(), new ReloadAction()
    ));

    /**
     * The Object object utilized as a thread lock when performing non thread-safe operations
     */
    private final Object threadLock = new Object();

    /**
     * The transitions that are responsible for the different Quick Action Buttons.
     */
    private final Map<Integer, DoubleTransition> quickActionTransitions = new HashMap<>();
    private final String aboutString = "About ICM";

    /**
     * The transition that lets the navigation bar rise when it's hovered.
     */
    private SmoothDoubleTransition riseTransition;

    /**
     * Whether the navbar is currently hovered.
     */
    private boolean navbarHovered = false;

    private String openGLWarning1;

    /**
     * OpenGL graphics card warning.
     */
    private String openGLWarning2;

    /**
     * Link to the Mojang Support about minimum requirements
     */
    private String openGLWarningLink;
    private int field_92022_t;
    private int field_92021_u;
    private int field_92020_v;
    private int field_92019_w;

    /**
     * Button Information...
     */
    private int BUTTON_WIDTH;
    private int BUTTON_HEIGHT;
    private int BUTTON_SPACE;
    private int BUTTON_Y;
    private int QUICK_ACTION_LEFT;
    private int QUICK_ACTION_RIGHT;

    /**
     * The ID of the button which is selected in order to draw it's sub-modules.
     */
    private int selectedButton = 0;

    /**
     * Default Constructor
     */
    public GuiMainMenu ()
    {
        this.openGLWarning2 = informationText;
        this.openGLWarning1 = "";

        if (!GLContext.getCapabilities().OpenGL20 && !OpenGlHelper.areShadersSupported()) {
            this.openGLWarning1 = I18n.format("title.oldgl1");
            this.openGLWarning2 = I18n.format("title.oldgl2");
            this.openGLWarningLink = "https://help.mojang.com/customer/portal/articles/325948?ref=game";
        }
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the window resizes, the buttonList is cleared beforehand.
     */
    public void initGui ()
    {
        riseTransition = SmoothDoubleTransition.builder()
            .start(1)
            .end(2)
            .fadeIn(0)
            .stay(10)
            .fadeOut(20)
            .autoTransformator((ForwardBackward) () -> navbarHovered)
            .build();

        this.updateSize();
        this.addButtons();

        synchronized (this.threadLock) {
            final int field_92023_s = this.fontRendererObj.getStringWidth(this.openGLWarning1);
            final int field_92024_r = this.fontRendererObj.getStringWidth(this.openGLWarning2);
            int k = Math.max(field_92023_s, field_92024_r);
            this.field_92022_t = (this.width - k) / 2;
            this.field_92021_u = this.buttonList.get(0).yPosition - 24;
            this.field_92020_v = this.field_92022_t + k;
            this.field_92019_w = this.field_92021_u + 24;
        }

        this.mc.setConnectedToRealms(false);
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen (int mouseX, int mouseY, float partialTicks)
    {
        if (drawTime == -1) drawTime = System.currentTimeMillis();

        this.drawGradientBackground();

        final IFontRenderer finalFontRenderer = updateSize();

        navbarHovered = mouseY >= height - getNavbarHeight() && mouseY <= height;

        this.buttonList.stream()
            .filter(guiButton -> guiButton.id < 10)
            .filter(TransparentButton.class::isInstance)
            .map(TransparentButton.class::cast)
            .forEach(transparentButton ->
            {
                transparentButton.setHighlighted(!riseTransition.isAtStart() && selectedButton == transparentButton.id);
                transparentButton.setFontRenderer(finalFontRenderer);
                transparentButton.setPositionY(BUTTON_Y);

                if (transparentButton.isMouseOver())
                    selectedButton = transparentButton.id;
            });

        // All quick-action buttons
        this.buttonList.stream()
            .filter(guiButton -> guiButton.id >= 10)
            .filter(TransparentButton.class::isInstance)
            .map(TransparentButton.class::cast)
            .forEach(transparentButton ->
            {
                double percent = quickActionTransitions.get(transparentButton.id)
                    .get();
                transparentButton.setPositionY((int) (height - BUTTON_HEIGHT * 1.7 * percent));
                transparentButton.setFontRenderer(finalFontRenderer);
            });

        // Logo
        int imageSize = Math.min(height / 3, 300);
        final ResourceLocation image = new ResourceLocation("inceptioncloud/sqr_outline.png");
        RenderUtils.drawImage(image, width / 2 - imageSize / 2 + 2, height / 8 + 2, imageSize, imageSize, 0, 0, 0, 0.4F);
        RenderUtils.drawImage(image, width / 2 - imageSize / 2, height / 8, imageSize, imageSize);

        // Title
        double percent = imageSize / 280D;
        IFontRenderer fontRenderer = Dragonfly.getFontDesign()
            .retrieveOrBuild(" Medium", (int) (25 + (percent * 60)));
        fontRenderer.drawCenteredString(InceptionCloudVersion.FULL_VERSION, width / 2, height / 8 + imageSize + 10, 0xFFFFFF, true);

        // Subtitle
        int previousHeight = fontRenderer.getHeight();
        percent = imageSize / 280D;
        fontRenderer = Dragonfly.getFontDesign()
            .retrieveOrBuild("", (int) (15 + (percent * 40)));
        fontRenderer.drawCenteredString("Minecraft Mod 1.8.8", width / 2, height / 8 + imageSize + 12 + previousHeight, 0xFFFFFF, true);

        // About
        fontRenderer = Dragonfly.getFontDesign().getRegular();
        fontRenderer.drawString(aboutString, 5, 5, Color.WHITE.getRGB(), true);

        // Bottom Bar
        drawRect(0, height - getNavbarHeight(), width, height, new Color(0, 0, 0, 100).getRGB());

        // Buttons
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.buttonList.remove(this.buttonList.stream()
            .filter(guiButton -> guiButton.id == 5)
            .findFirst()
            .orElse(null));

        // Fade-In Overlay
        drawRect(0, 0, width, height, RGB.of(GreyToneColor.DARK_GREY)
            .alpha((float) fadeInTransition.get())
            .rgb());
    }

    /**
     * Changes the font and button size when the window size is updated (by rescaling or toggling fullscreen).
     */
    private IFontRenderer updateSize ()
    {
        double percent = Math.min(height / 540D, 1.0D);
        final int BUTTON_FONT_SIZE = (int) (18 + (percent * 15));
        IFontRenderer fontRenderer = Dragonfly.getFontDesign()
            .retrieveOrBuild("", BUTTON_FONT_SIZE);

        BUTTON_WIDTH = (int) (80 + (percent * 30));
        BUTTON_HEIGHT = fontRenderer.getHeight();
        BUTTON_SPACE = 10;
        BUTTON_Y = height - getNavbarHeight() + (BUTTON_HEIGHT / 2);

        QUICK_ACTION_LEFT = (int) (this.width / 2 - BUTTON_SPACE * 1.5 - BUTTON_WIDTH * 2 + 10);
        QUICK_ACTION_RIGHT = (int) (this.width / 2 + BUTTON_SPACE * 1.5 + BUTTON_WIDTH * 2 - 10);

        return fontRenderer;
    }

    /**
     * Adds Singleplayer and Multiplayer buttons on Main Menu for players who have bought the game.<br/>
     *
     * <b>The following button IDs are used:</b><br/>
     * <code>0</code> Singleplayer<br/>
     * <code>1</code> Multiplayer<br/>
     * <code>2</code> Options<br/>
     * <code>3</code> Quit Game
     */
    public void addButtons ()
    {
        this.buttonList.add(new TransparentButton(0, (int) (this.width / 2 - BUTTON_SPACE * 1.5 - BUTTON_WIDTH * 2), BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT, I18n.format("menu.singleplayer")));
        this.buttonList.add(new TransparentButton(1, this.width / 2 - BUTTON_SPACE / 2 - BUTTON_WIDTH, BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT, I18n.format("menu.multiplayer")));
        this.buttonList.add(new TransparentButton(2, this.width / 2 + BUTTON_SPACE / 2, BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT, I18n.format("menu.options")));
        this.buttonList.add(new TransparentButton(3, (int) (this.width / 2 + BUTTON_SPACE * 1.5 + BUTTON_WIDTH), BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT, I18n.format("menu.quit")));

        IFontRenderer fontRenderer = updateSize();
        boolean left = true;

        for (QuickAction quickAction : AVAILABLE_ACTIONS) {
            final int stringWidth = fontRenderer.getStringWidth(quickAction.getDisplay());
            final int xPosition = left ? QUICK_ACTION_LEFT + 50 : QUICK_ACTION_RIGHT - stringWidth - 50;
            final int buttonId = quickAction.getOwnButtonId();

            this.buttonList.add(new TransparentButton(buttonId, xPosition, height, stringWidth, 20, quickAction.getDisplay()).setOpacity(0.5F));
            quickActionTransitions.put(buttonId, DoubleTransition.builder()
                .start(0)
                .end(1)
                .amountOfSteps(20)
                .autoTransformator((ForwardBackward) () -> riseTransition.isAtEnd() && isQuickActionSelected(buttonId))
                .build());

            left = !left;
        }
    }

    /**
     * Checks whether the Quick Action represented by the Button is currently selected.
     */
    private boolean isQuickActionSelected (int quickActionButtonId)
    {
        final List<QuickAction> actions = AVAILABLE_ACTIONS.stream()
            .filter(action -> action.getHeadButtonId() == selectedButton)
            .collect(Collectors.toList());

        return quickActionButtonId == actions.get(0).getOwnButtonId()
               || quickActionButtonId == actions.get(1).getOwnButtonId();
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    public boolean doesGuiPauseGame ()
    {
        return false;
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed (GuiButton button) throws IOException
    {
        switch (button.id) {
            case 0:
                this.mc.displayGuiScreen(new GuiSelectWorld(this));
                break;

            case 1:
                this.mc.displayGuiScreen(new GuiMultiplayer(this));
                break;

            case 2:
                this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
                break;

            case 3:
                this.mc.shutdown();
                break;
        }

        if (button.id >= 10)
            getActionByButtonId(button.id).getHandleClick().run();
    }

    /**
     * Tries to find the quick action that belongs to the given button id.
     */
    private QuickAction getActionByButtonId (int buttonId)
    {
        return AVAILABLE_ACTIONS.stream()
            .filter(quickAction -> quickAction.getOwnButtonId() == buttonId)
            .findFirst()
            .orElse(null);
    }

    public void confirmClicked (boolean result, int id)
    {
        if (result && id == 12) {
            ISaveFormat isaveformat = this.mc.getSaveLoader();
            isaveformat.flushCache();
            isaveformat.deleteWorldDirectory("Demo_World");
            this.mc.displayGuiScreen(this);
        } else if (id == 13) {
            if (result) {
                try {
                    Class<?> oclass = Class.forName("java.awt.Desktop");
                    Object object = oclass.getMethod("getDesktop", new Class[0]).invoke(null);
                    oclass.getMethod("browse", new Class[] { URI.class }).invoke(object, new URI(this.openGLWarningLink));
                } catch (Throwable throwable) {
                    logger.error("Couldn't open link", throwable);
                }
            }

            this.mc.displayGuiScreen(this);
        }
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked (int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        synchronized (this.threadLock) {
            if (this.openGLWarning1.length() > 0 && mouseX >= this.field_92022_t && mouseX <= this.field_92020_v && mouseY >= this.field_92021_u && mouseY <= this.field_92019_w) {
                GuiConfirmOpenLink guiconfirmopenlink = new GuiConfirmOpenLink(this, this.openGLWarningLink, 13, true);
                guiconfirmopenlink.disableSecurityWarning();
                this.mc.displayGuiScreen(guiconfirmopenlink);
            }
        }

        final IFontRenderer fontRenderer = Dragonfly.getFontDesign().getRegular();
        if (mouseX >= 5 && mouseX <= 5 + fontRenderer.getStringWidth(aboutString)
            && mouseY >= 5 && mouseY <= 5 + fontRenderer.getHeight()) {
            // TODO: Restore this
            // this.mc.displayGuiScreen(new AboutUI(this));
            mc.displayGuiScreen(new EngineTestUI());
        }
    }

    @Override
    public void onGuiClosed ()
    {
        quickActionTransitions.values()
            .forEach(Transition::destroy);
        riseTransition.destroy();

        super.onGuiClosed();
    }

    /**
     * @return The height of the navigation bar.
     */
    public int getNavbarHeight ()
    {
        return (int) (BUTTON_HEIGHT * 2 * riseTransition.get());
    }
}
