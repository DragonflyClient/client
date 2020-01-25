package net.minecraft.client.gui;

import net.inceptioncloud.minecraftmod.InceptionMod;
import net.inceptioncloud.minecraftmod.design.color.*;
import net.inceptioncloud.minecraftmod.design.font.IFontRenderer;
import net.inceptioncloud.minecraftmod.gui.components.CleanGuiButton;
import net.inceptioncloud.minecraftmod.gui.custom.mainmenu.quickactions.QuickAction;
import net.inceptioncloud.minecraftmod.gui.custom.mainmenu.quickactions.multiplayer.DirectConnectAction;
import net.inceptioncloud.minecraftmod.gui.custom.mainmenu.quickactions.multiplayer.LastServerAction;
import net.inceptioncloud.minecraftmod.gui.custom.mainmenu.quickactions.options.ModOptionsAction;
import net.inceptioncloud.minecraftmod.gui.custom.mainmenu.quickactions.options.ResourcePackAction;
import net.inceptioncloud.minecraftmod.gui.custom.mainmenu.quickactions.quit.ReloadAction;
import net.inceptioncloud.minecraftmod.gui.custom.mainmenu.quickactions.quit.RestartAction;
import net.inceptioncloud.minecraftmod.gui.custom.mainmenu.quickactions.singleplayer.CreateMapAction;
import net.inceptioncloud.minecraftmod.gui.custom.mainmenu.quickactions.singleplayer.LastMapAction;
import net.inceptioncloud.minecraftmod.transition.number.DoubleTransition;
import net.inceptioncloud.minecraftmod.transition.supplier.ForwardBackward;
import net.inceptioncloud.minecraftmod.transition.supplier.ForwardNothing;
import net.inceptioncloud.minecraftmod.utils.RenderUtils;
import net.inceptioncloud.minecraftmod.version.InceptionCloudVersion;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
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
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class GuiMainMenu extends GuiScreen implements GuiYesNoCallback
{
    public static final String informationText = "Please click " + EnumChatFormatting.UNDERLINE + "here" + EnumChatFormatting.RESET + " for more information.";

    /**
     * Provides all available quick actions.
     */
    public static final List<QuickAction> AVAILABLE_ACTIONS = Arrays.asList(
        new LastMapAction(), new CreateMapAction(),
        new LastServerAction(), new DirectConnectAction(),
        new ResourcePackAction(), new ModOptionsAction(),
        new ReloadAction(), new RestartAction()
    );

    private static final Logger logger = LogManager.getLogger();

    /**
     * The Object object utilized as a thread lock when performing non thread-safe operations
     */
    private final Object threadLock = new Object();
    private int mouseY = 0;

    /**
     * OpenGL graphics card warning.
     */
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
     * The time when the GUI was first drawn.
     *
     * @see #addButtons() Which Button IDs are used
     */
    private long drawTime = -1;

    /**
     * The transitions that are responsible for the different Quick Action Buttons.
     */
    private Map<Integer, DoubleTransition> quickActionTransitions = new HashMap<>();

    /**
     * The transition that lets the navigation bar rise when it's hovered.
     */
    private DoubleTransition riseTransition = DoubleTransition.builder().start(1).end(2).amountOfSteps(30).autoTransformator(( ForwardBackward ) () -> mouseY >= height - getNavbarHeight() && mouseY <= height).build();

    /**
     * Provides the value for the fading in of the main menu after the splash screen.
     */
    private DoubleTransition fadeInTransition = DoubleTransition.builder().start(1).end(0).amountOfSteps(500).autoTransformator(( ForwardNothing ) () -> drawTime != -1 && System.currentTimeMillis() - drawTime > 1000).build();

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
     * @return The height of the navigation bar.
     */
    public int getNavbarHeight ()
    {
        return ( int ) ( BUTTON_HEIGHT * 2 * riseTransition.get() );
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen (int mouseX, int mouseY, float partialTicks)
    {
        if (this.drawTime == -1) this.drawTime = System.currentTimeMillis();
        this.mouseY = mouseY;
        this.drawGradientBackground();
        final IFontRenderer finalFontRenderer = updateSize();

        this.buttonList.stream()
            .filter(guiButton -> guiButton.id < 10)
            .filter(CleanGuiButton.class::isInstance)
            .map(CleanGuiButton.class::cast)
            .forEach(cleanGuiButton ->
            {
                cleanGuiButton.setHighlighted(!riseTransition.isAtStart() && selectedButton == cleanGuiButton.id);
                cleanGuiButton.setFontRenderer(finalFontRenderer);
                cleanGuiButton.setyPosition(BUTTON_Y);

                if (cleanGuiButton.isMouseOver())
                    selectedButton = cleanGuiButton.id;
            });

        this.buttonList.stream()
            .filter(guiButton -> guiButton.id >= 10)
            .filter(CleanGuiButton.class::isInstance)
            .map(CleanGuiButton.class::cast)
            .forEach(cleanGuiButton ->
            {
                double percent = quickActionTransitions.get(cleanGuiButton.id).get();
                cleanGuiButton.setyPosition(( int ) ( height - BUTTON_HEIGHT * 1.7 * percent));
                cleanGuiButton.setFontRenderer(finalFontRenderer);
            });

        // ICMM - Logo
        int imageSize = Math.min(height / 3, 300);
        RenderUtils.drawImage(new ResourceLocation("inceptioncloud/sqr_outline.png"), width / 2 - imageSize / 2 + 1, height / 8 + 1, imageSize, imageSize, 0, 0, 0, 0.4F);
        RenderUtils.drawImage(new ResourceLocation("inceptioncloud/sqr_outline.png"), width / 2 - imageSize / 2, height / 8, imageSize, imageSize);

        // ICMM - Title
        double percent = imageSize / 280D;
        IFontRenderer fontRenderer = InceptionMod.getInstance().getFontDesign().retrieveOrBuild("Product Sans Medium", Font.PLAIN, ( int ) ( 25 + ( percent * 60 ) ));
        fontRenderer.drawCenteredString(InceptionCloudVersion.FULL_VERSION, width / 2, height / 8 + imageSize + 10, 0xFFFFFF, true);

        // ICMM - Subtitle
        int previousHeight = fontRenderer.getHeight();
        percent = imageSize / 280D;
        fontRenderer = InceptionMod.getInstance().getFontDesign().retrieveOrBuild("Product Sans", Font.PLAIN, ( int ) ( 15 + ( percent * 40 ) ));
        fontRenderer.drawCenteredString("Minecraft Mod 1.8.8", width / 2, height / 8 + imageSize + 12 + previousHeight, 0xFFFFFF, true);

        // ICMM - Bottom Bar
        drawRect(0, height - getNavbarHeight(), width, height, new Color(0, 0, 0, 100).getRGB());

        // Buttons
        super.drawScreen(mouseX, mouseY, partialTicks);

        this.buttonList.remove(this.buttonList.stream().filter(guiButton -> guiButton.id == 5).findFirst().orElse(null));

        // ICMM - Fade-In Overlay
        Gui.drawRect(0, 0, width, height, ColorTransformator.of(GreyToneColor.DARK_GREY).transformAlpha(( float ) fadeInTransition.get()).toRGB());
    }

    /**
     * Changes the font and button size when the window size is updated (by rescaling or toggling fullscreen).
     */
    private IFontRenderer updateSize ()
    {
        double percent = Math.min(height / 540D, 1.0D);
        final int BUTTON_FONT_SIZE = ( int ) ( 18 + ( percent * 15 ) );
        IFontRenderer fontRenderer = InceptionMod.getInstance().getFontDesign().retrieveOrBuild("Product Sans", Font.PLAIN, BUTTON_FONT_SIZE);

        BUTTON_WIDTH = ( int ) ( 80 + ( percent * 30 ) );
        BUTTON_HEIGHT = fontRenderer.getHeight();
        BUTTON_SPACE = 10;
        BUTTON_Y = height - getNavbarHeight() + ( BUTTON_HEIGHT / 2 );

        QUICK_ACTION_LEFT = ( int ) ( this.width / 2 - BUTTON_SPACE * 1.5 - BUTTON_WIDTH * 2 + 10);
        QUICK_ACTION_RIGHT = ( int ) ( this.width / 2 + BUTTON_SPACE * 1.5 + BUTTON_WIDTH * 2 - 10);

        return fontRenderer;
    }

    /**
     * Adds Singleplayer and Multiplayer buttons on Main Menu for players who have bought the game.<br/>
     *
     * <b>The following button ids are used:</b><br/>
     * <code>0</code> Singleplayer<br/>
     * <code>1</code> Multiplayer<br/>
     * <code>2</code> Options<br/>
     * <code>3</code> Quit Game
     */
    public void addButtons ()
    {
        this.buttonList.add(new CleanGuiButton(0, ( int ) ( this.width / 2 - BUTTON_SPACE * 1.5 - BUTTON_WIDTH * 2 ), BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT, I18n.format("menu.singleplayer")));
        this.buttonList.add(new CleanGuiButton(1, this.width / 2 - BUTTON_SPACE / 2 - BUTTON_WIDTH, BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT, I18n.format("menu.multiplayer")));
        this.buttonList.add(new CleanGuiButton(2, this.width / 2 + BUTTON_SPACE / 2, BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT, I18n.format("menu.options")));
        this.buttonList.add(new CleanGuiButton(3, ( int ) ( this.width / 2 + BUTTON_SPACE * 1.5 + BUTTON_WIDTH ), BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT, I18n.format("menu.quit")));

        IFontRenderer fontRenderer = updateSize();
        boolean left = true;

        for (QuickAction quickAction : AVAILABLE_ACTIONS) {
            final int stringWidth = fontRenderer.getStringWidth(quickAction.getDisplay());
            final int xPosition = left ? QUICK_ACTION_LEFT + 50 : QUICK_ACTION_RIGHT - stringWidth - 50;
            final int buttonId = quickAction.getOwnButtonId();

            this.buttonList.add(new CleanGuiButton(buttonId, xPosition, height, stringWidth, 20, quickAction.getDisplay()).setOpacity(0.5F));
            this.quickActionTransitions.put(buttonId, DoubleTransition.builder().start(0).end(1).amountOfSteps(20).autoTransformator(( ForwardBackward ) () -> riseTransition.isAtEnd() && isQuickActionSelected(buttonId)).build());

            left = !left;
        }
    }

    /**
     * Checks whether the Quick Action represented by the Button is currently selected.
     */
    private boolean isQuickActionSelected (int quickActionButtonId)
    {
        final List<QuickAction> actions = AVAILABLE_ACTIONS.stream().filter(action -> action.getHeadButtonId() == selectedButton).collect(Collectors.toList());
        return quickActionButtonId == actions.get(0).getOwnButtonId() || quickActionButtonId == actions.get(1).getOwnButtonId();
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    public boolean doesGuiPauseGame ()
    {
        return false;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui ()
    {
        this.updateSize();
        this.addButtons();

        synchronized (this.threadLock) {
            final int field_92023_s = this.fontRendererObj.getStringWidth(this.openGLWarning1);
            final int field_92024_r = this.fontRendererObj.getStringWidth(this.openGLWarning2);
            int k = Math.max(field_92023_s, field_92024_r);
            this.field_92022_t = ( this.width - k ) / 2;
            this.field_92021_u = this.buttonList.get(0).yPosition - 24;
            this.field_92020_v = this.field_92022_t + k;
            this.field_92019_w = this.field_92021_u + 24;
        }

        this.mc.setConnectedToRealms(false);
    }

    /**
     * Draws a rectangle with a vertical gradient between the specified colors (ARGB format). Args : x1, y1, x2, y2,
     * topColor, bottomColor
     */
    public void drawGradientBackground ()
    {
        int startColor = CloudColor.FUSION.getRGB();
        int endColor = CloudColor.ROYAL.getRGB();
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
        worldrenderer.pos(width, 0, this.zLevel).color(avg_r, avg_g, avg_b, avg_a).endVertex();
        worldrenderer.pos(0, 0, this.zLevel).color(start_r, start_g, start_b, start_a).endVertex();
        worldrenderer.pos(0, height, this.zLevel).color(avg_r, avg_g, avg_b, avg_a).endVertex();
        worldrenderer.pos(width, height, this.zLevel).color(end_r, end_g, end_b, end_a).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
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
        return AVAILABLE_ACTIONS.stream().filter(quickAction -> quickAction.getOwnButtonId() == buttonId).findFirst().orElse(null);
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
    }
}
