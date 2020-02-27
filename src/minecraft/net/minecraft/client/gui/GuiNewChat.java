package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import net.inceptioncloud.minecraftmod.InceptionMod;
import net.inceptioncloud.minecraftmod.impl.Tickable;
import net.inceptioncloud.minecraftmod.design.font.IFontRenderer;
import net.inceptioncloud.minecraftmod.transition.number.DoubleTransition;
import net.inceptioncloud.minecraftmod.version.InceptionCloudVersion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;
import java.util.function.IntSupplier;

public class GuiNewChat extends Gui implements Tickable
{
    private static final Logger logger = LogManager.getLogger();
    private final Minecraft mc;
    private final List<String> sentMessages = Lists.newArrayList();
    private final List<ChatLine> chatMessages = Lists.newArrayList();
    private final List<ChatLine> seperateChatLines = Lists.newArrayList();
    private int scrollPos;
    private static int targetHeight = 0;
    private static double height = 0;
    private boolean isScrolled;

    /**
     * The transition that draws the title background.
     */
    private static final DoubleTransition titleBackground = DoubleTransition.builder().start(0).end(1).amountOfSteps(15).autoTransformator(new IntSupplier()
    {
        @Override
        public int getAsInt ()
        {
            if (!getChatOpen())
                return -1;

            if (GuiChat.getDirection() == 1 && Math.abs(targetHeight - height) < 10)
                return 1;

            if (GuiChat.getDirection() == -1 && titleText.isAtStart())
                return -1;

            return 0;
        }
    }).build();

    /**
     * The transition that moves the title text.
     */
    private static final DoubleTransition titleText = DoubleTransition.builder().start(0).end(1).amountOfSteps(15).autoTransformator(() ->
    {
        if (!getChatOpen())
            return -1;

        if (GuiChat.getDirection() == 1 && titleBackground.isAtEnd())
            return 1;

        if (GuiChat.getDirection() == -1)
            return -1;

        return 0;
    }).build();

    public GuiNewChat (Minecraft mcIn)
    {
        this.mc = mcIn;

        InceptionMod.getInstance().handleTickable(this);
    }

    public static int calculateChatboxWidth (float chatWidthSetting)
    {
        int i = 320;
        int j = 40;

        // 1F * 280 + 40
        return MathHelper.floor_float(chatWidthSetting * ( float ) ( i - j ) + ( float ) j);
    }

    public static int calculateChatboxHeight (float chatHeightSetting)
    {
        int i = 180;
        int j = 20;
        return MathHelper.floor_float(chatHeightSetting * ( float ) ( i - j ) + ( float ) j);
    }

    /**
     * Handle the mod tick.
     */
    @Override
    public void modTick ()
    {
        final double difference = Math.abs(targetHeight - height);
        final double factor = 0.05;

        if (difference > factor)
            height += targetHeight > height ? difference * factor : -( difference * factor );
        else
            height = targetHeight;
    }

    public void drawChat (int updateTimes)
    {
        if (this.mc.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN) {
            int amountOfLines = this.getLineCount();
            boolean chatOpen = false;
            int visibleChatLines = 0;
            int amountOfSeperateLines = this.seperateChatLines.size();
            float f = this.mc.gameSettings.chatOpacity * 0.9F + 0.1F;
            final IFontRenderer fontRenderer = InceptionMod.getInstance().getFontDesign().getRegular();

            if (amountOfSeperateLines > 0) {

                if (getChatOpen()) {
                    chatOpen = true;
                }

                float f1 = this.getChatScale();
                int l = MathHelper.ceiling_float_int(( float ) this.getChatWidth() / f1);
                GlStateManager.pushMatrix();
                GlStateManager.translate(2.0F, 20.0F, 0.0F);
                GlStateManager.scale(f1, f1, 1.0F);

                int currentY = 0;
                int currentHeight = 0;

                List<Runnable> stringsToDraw = new ArrayList<>();

                for (int i1 = 0 ; i1 + this.scrollPos < this.seperateChatLines.size() && i1 < amountOfLines ; ++i1) {
                    ChatLine chatline = this.seperateChatLines.get(i1 + this.scrollPos);

                    if (chatline != null) {
                        int updatedCounter = updateTimes - chatline.getUpdatedCounter();

                        if (updatedCounter < 200 || chatOpen) /* Message times out if the chat isn't opened */ {
                            double d0 = ( double ) updatedCounter / 200.0D;
                            d0 = 1.0D - d0;
                            d0 = d0 * 10.0D;
                            d0 = MathHelper.clamp_double(d0, 0.0D, 1.0D); // Changes the value to be at least 0 and max 1
                            d0 = d0 * d0;
                            int l1 = ( int ) ( 255.0D * d0 );

                            if (chatOpen) {
                                l1 = 255;
                            }

                            l1 = ( int ) ( ( float ) l1 * f );
                            ++visibleChatLines;

                            if (l1 > 3) {

                                String s = chatline.getChatComponent().getFormattedText();
                                GlStateManager.enableBlend();

                                final int min = -fontRenderer.getStringWidth(s);
                                final int add = -min + getBorderAmount() - 2;
                                final int stringX = ( int ) ( min + ( ( add ) * chatline.getLocation().get() ) );
                                final int copyCurrentY = currentY;

                                Color fontColor = new Color(255, 255, 255, chatline.getOpacity().castToInt());
                                stringsToDraw.add(() ->
                                {
                                    fontRenderer.drawStringWithShadow(s, stringX, copyCurrentY - 8, fontColor.getRGB());
//                                    drawRect(
//                                        stringX,
//                                        copyCurrentY - 8,
//                                        stringX + fontRenderer.getStringWidth(s),
//                                        copyCurrentY - 8 + fontRenderer.getHeight(),
//                                        new Color(255, 0, 0, 100).getRGB()
//                                    );
                                });

                                currentY -= ( int ) ( fontRenderer.getHeight() * chatline.getLocation().get() );
                                currentHeight += fontRenderer.getHeight();
                            }
                        }
                    }
                }

                targetHeight = currentHeight;

                /* Draw the chat background rectangle */
                {
                    int border = ( int ) Math.min(getBorderAmount(), ( height / 10 ) * getBorderAmount());

                    Color rectColor = new Color(0, 0, 0, 80);
                    drawRect(-2, ( int ) -height - border, l + 4, border, rectColor.getRGB());
                }

                /* Open-Chat Title */
                {
                    int border = ( int ) Math.min(getBorderAmount(), ( height / 10 ) * getBorderAmount());

                    Color lineColor = new Color(0, 0, 0, 200);
                    drawRect(-2, ( int ) ( ( -height - border ) - ( titleBackground.get() * 15 ) ), l + 4, ( int ) ( -height - border ), lineColor.getRGB());

                    GlStateManager.enableBlend();
                    GlStateManager.enableAlpha();

                    String s1 = InceptionCloudVersion.TITLE_1;
                    String s2 = InceptionCloudVersion.TITLE_2 + " §f" + InceptionCloudVersion.VERSION;
                    String s3 = InceptionCloudVersion.IDENTIFIER;
                    String whole = s1 + s2 + " " + s3;
                    String date = "§7" + new SimpleDateFormat("HH:mm:ss").format(System.currentTimeMillis());

                    Color color1 = new Color(235, 59, 90, 10 + ( int ) ( titleText.get() * 245 ));
                    Color color2 = new Color(165, 94, 234, 10 + ( int ) ( titleText.get() * 245 ));
                    Color color3 = new Color(69, 170, 242, 10 + ( int ) ( titleText.get() * 245 ));
                    Color alpha = new Color(255, 255, 255, 10 + ( int ) ( titleText.get() * 245 ));

                    int base = -fontRenderer.getStringWidth(whole);
                    int addition = fontRenderer.getStringWidth(whole) + getBorderAmount() - 2;
                    int textX = base + ( int ) ( addition * titleText.get() );

                    // If the title box is wide enough to display the cloud name
                    if (l + 2 >= fontRenderer.getStringWidth(s1 + s2 + " " + s3)) {
                        fontRenderer.drawString(s1, textX, ( int ) ( -height - border - 10 ), color1.getRGB(), true);
                        fontRenderer.drawString(s2, textX + fontRenderer.getStringWidth(s1), ( int ) ( -height - border - 10 ), color2.getRGB(), true);
                        fontRenderer.drawString(s3, textX + fontRenderer.getStringWidth(s1 + s2 + " "), ( int ) ( -height - border - 10 ), color3.getRGB(), true);
                    }

                    // If the title box is wide enough to display the time
                    if (l + 2 >= fontRenderer.getStringWidth(s1 + s2 + " " + s3 + "     " + date)) {
                        base = -fontRenderer.getStringWidth("00:00:00");
                        addition = l + 4 - getBorderAmount();
                        textX = base + ( int ) ( addition * titleText.get() );
                        fontRenderer.drawString(date, textX, ( int ) ( -height - border - 10 ), alpha.getRGB(), true);
                    }
                }

                /* Draw the messages over the background */
                {
                    GlStateManager.color(1F, 1F, 1F, 1F);
                    stringsToDraw.forEach(Runnable::run);
                }

                if (chatOpen) {
                    int fontHeight = fontRenderer.getHeight();
                    GlStateManager.translate(-3.0F, 0.0F, 0.0F);
                    int totalHeight = amountOfSeperateLines * fontHeight + amountOfSeperateLines;
                    int visibleHeight = visibleChatLines * fontHeight + visibleChatLines;
                    int yPosition = ( int ) ( this.scrollPos * visibleHeight / amountOfSeperateLines * 0.9 );
                    int scrollBarHeight = visibleHeight * visibleHeight / totalHeight;

                    if (totalHeight != visibleHeight) {
                        drawRect(0, -yPosition, 2, -yPosition - scrollBarHeight, new Color(255, 255, 255, 100).getRGB());
                    }
                }

                GlStateManager.popMatrix();
            }
        }
    }

    /**
     * Clears the chat.
     */
    public void clearChatMessages ()
    {
        this.seperateChatLines.forEach(ChatLine::destroy);
        this.chatMessages.forEach(ChatLine::destroy);

        this.seperateChatLines.clear();
        this.chatMessages.clear();
        this.sentMessages.clear();
    }

    public void printChatMessage (IChatComponent p_146227_1_)
    {
        this.printChatMessageWithOptionalDeletion(p_146227_1_, 0);
    }

    public int getBorderAmount ()
    {
        return 5;
    }

    /**
     * prints the ChatComponent to Chat. If the ID is not 0, deletes an existing Chat Line of that ID from the GUI
     */
    public void printChatMessageWithOptionalDeletion (IChatComponent component, int id)
    {
        this.setChatLine(component, id, this.mc.ingameGUI.getUpdateCounter(), false);
        logger.info("[CHAT] " + component.getUnformattedText());
    }

    private void setChatLine (IChatComponent component, int id, int updateCounter, boolean displayOnly)
    {
        if (id != 0) {
            this.deleteChatLine(id);
        }

        int i = MathHelper.floor_float(( float ) this.getChatWidth() / this.getChatScale());
        List<IChatComponent> list = GuiUtilRenderComponents.splitText(component, i, InceptionMod.getInstance().getFontDesign().getRegular(), false, false);
        boolean chatOpen = this.getChatOpen();

        for (IChatComponent ichatcomponent : list) {
            if (chatOpen && this.scrollPos > 0) {
                this.isScrolled = true;
                this.scroll(1);
            }

            this.seperateChatLines.add(0, new ChatLine(updateCounter, ichatcomponent, id));
        }

        while (this.seperateChatLines.size() > 100) {
            this.seperateChatLines.remove(this.seperateChatLines.size() - 1);
        }

        if (!displayOnly) {
            this.chatMessages.add(0, new ChatLine(updateCounter, component, id));

            while (this.chatMessages.size() > 100) {
                ChatLine target = chatMessages.get(chatMessages.size() - 1);
                target.destroy();
                chatMessages.remove(target);
            }
        }
    }

    public void refreshChat ()
    {
        this.seperateChatLines.clear();
        this.resetScroll();

        for (int i = this.chatMessages.size() - 1 ; i >= 0 ; --i) {
            ChatLine chatline = this.chatMessages.get(i);
            this.setChatLine(chatline.getChatComponent(), chatline.getChatLineID(), chatline.getUpdatedCounter(), true);
        }
    }

    public List<String> getSentMessages ()
    {
        return this.sentMessages;
    }

    /**
     * Adds this string to the list of sent messages, for recall using the up/down arrow keys
     */
    public void addToSentMessages (String p_146239_1_)
    {
        if (this.sentMessages.isEmpty() || !this.sentMessages.get(this.sentMessages.size() - 1).equals(p_146239_1_)) {
            this.sentMessages.add(p_146239_1_);
        }
    }

    /**
     * Resets the chat scroll (executed when the GUI is closed, among others)
     */
    public void resetScroll ()
    {
        this.scrollPos = 0;
        this.isScrolled = false;
    }

    /**
     * Scrolls the chat by the given number of lines.
     */
    public void scroll (int p_146229_1_)
    {
        this.scrollPos += p_146229_1_;
        int i = this.seperateChatLines.size();

        if (this.scrollPos > i - this.getLineCount()) {
            this.scrollPos = i - this.getLineCount();
        }

        if (this.scrollPos <= 0) {
            this.scrollPos = 0;
            this.isScrolled = false;
        }
    }

    /**
     * Gets the chat component under the mouse
     */
    public IChatComponent getChatComponent (int p1, int p2)
    {
        if (getChatOpen()) {
            ScaledResolution scaledresolution = new ScaledResolution(this.mc);
            int i = scaledresolution.getScaleFactor();
            float f = this.getChatScale();
            int j = p1 / i - 3;
            int k = p2 / i - 27;
            j = MathHelper.floor_float(( float ) j / f);
            k = MathHelper.floor_float(( float ) k / f);

            if (j >= 0 && k >= 0) {
                int l = Math.min(this.getLineCount(), this.seperateChatLines.size());

                if (j <= MathHelper.floor_float(( float ) this.getChatWidth() / this.getChatScale()) && k < InceptionMod.getInstance().getFontDesign().getRegular().getHeight() * l + l) {
                    int i1 = k / InceptionMod.getInstance().getFontDesign().getRegular().getHeight() + this.scrollPos;

                    if (i1 >= 0 && i1 < this.seperateChatLines.size()) {
                        ChatLine chatline = this.seperateChatLines.get(i1);
                        int j1 = 0;

                        for (IChatComponent ichatcomponent : chatline.getChatComponent()) {
                            if (ichatcomponent instanceof ChatComponentText) {
                                j1 += InceptionMod.getInstance().getFontDesign().getRegular().getStringWidth(GuiUtilRenderComponents.removeTextColorsIfConfigured(( ( ChatComponentText ) ichatcomponent ).getChatComponentText_TextValue(), false));

                                if (j1 > j) {
                                    return ichatcomponent;
                                }
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    /**
     * Returns true if the chat GUI is open
     */
    public static boolean getChatOpen ()
    {
        return Minecraft.getMinecraft().currentScreen != null && Minecraft.getMinecraft().currentScreen instanceof GuiChat;
    }

    /**
     * finds and deletes a Chat line by ID
     */
    public void deleteChatLine (int chatLineID)
    {
        Iterator<ChatLine> iterator = this.seperateChatLines.iterator();

        while (iterator.hasNext()) {
            ChatLine target = iterator.next();

            if (target.getChatLineID() == chatLineID) {
                target.destroy();
                iterator.remove();
            }
        }

        iterator = this.chatMessages.iterator();

        while (iterator.hasNext()) {
            ChatLine target = iterator.next();

            if (target.getChatLineID() == chatLineID) {
                target.destroy();
                iterator.remove();
                break;
            }
        }
    }

    public int getChatWidth ()
    {
        return calculateChatboxWidth(this.mc.gameSettings.chatWidth);
    }

    public int getChatHeight ()
    {
        return calculateChatboxHeight(getChatOpen() ? this.mc.gameSettings.chatHeightFocused : this.mc.gameSettings.chatHeightUnfocused);
    }

    /**
     * Returns the chatscale from mc.gameSettings.chatScale
     */
    public float getChatScale ()
    {
        return this.mc.gameSettings.chatScale;
    }

    public int getLineCount ()
    {
        return this.getChatHeight() / 9;
    }
}
