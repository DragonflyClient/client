package net.minecraft.client.gui;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.mojang.authlib.GameProfile;
import net.inceptioncloud.dragonfly.Dragonfly;
import net.inceptioncloud.dragonfly.engine.font.renderer.IFontRenderer;
import net.inceptioncloud.dragonfly.transition.number.DoubleTransition;
import net.inceptioncloud.dragonfly.transition.number.SmoothDoubleTransition;
import net.inceptioncloud.dragonfly.transition.supplier.ForwardBackward;
import net.inceptioncloud.dragonfly.ui.playerlist.indicators.Indicator;
import net.inceptioncloud.dragonfly.ui.playerlist.indicators.IndicatorKt;
import net.inceptioncloud.dragonfly.ui.playerlist.indicators.SamePartyIndicator;
import net.inceptioncloud.dragonfly.ui.playerlist.indicators.ThePlayerIndicator;
import net.inceptioncloud.dragonfly.ui.renderer.HotkeyRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldSettings;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GuiPlayerTabOverlay extends Gui
{
    private static final Ordering<NetworkPlayerInfo> ENTRY_ORDERING = Ordering.from(new GuiPlayerTabOverlay.PlayerComparator());
    private final Minecraft mc;
    private final GuiIngame guiIngame;
    private IChatComponent footer;
    private IChatComponent header;

    /**
     * The last time the playerlist was opened (went from not being renderd, to being rendered)
     */
    private long lastTimeOpened;

    /**
     * Whether or not the playerlist is currently being rendered
     */
    private boolean isBeingRendered;

    private final DoubleTransition hotkeyFlyIn = DoubleTransition.builder().start(-10).end(130).amountOfSteps(30).autoTransformator(new ForwardBackward()
    {
        @Override
        public boolean getAsBoolean ()
        {
            return isBeingRendered && ( Minecraft.getSystemTime() - lastTimeOpened ) > 5000;
        }
    }).build();

    private final SmoothDoubleTransition tablistFlyIn = SmoothDoubleTransition.builder()
        .start(1).end(0)
        .fadeIn(5).stay(15).fadeOut(10)
        .autoTransformator(new ForwardBackward()
        {
            @Override
            public boolean getAsBoolean ()
            {
                return isBeingRendered;
            }
        }).build();

    public GuiPlayerTabOverlay (Minecraft mcIn, GuiIngame guiIngameIn)
    {
        this.mc = mcIn;
        this.guiIngame = guiIngameIn;
    }

    /**
     * Returns the name that should be rendered for the player supplied
     */
    public static String getPlayerName (NetworkPlayerInfo playerInfo)
    {
        String displayName = playerInfo.getDisplayName() != null ? playerInfo.getDisplayName().getFormattedText() : ScorePlayerTeam.formatPlayerName(playerInfo.getPlayerTeam(), playerInfo.getGameProfile().getName());

        playerInfo.updateIndicator(ThePlayerIndicator.class);
        if (playerInfo.updateIndicator(SamePartyIndicator.class)) {
            displayName = displayName.substring(0, displayName.length() - 12);
        }

        return displayName;
    }

    /**
     * Called by GuiIngame to update the information stored in the playerlist, does not actually render the list,
     * however.
     */
    public void updatePlayerList (boolean willBeRendered)
    {
        if (willBeRendered && !this.isBeingRendered) {
            this.lastTimeOpened = Minecraft.getSystemTime();
        }

        this.isBeingRendered = willBeRendered;

        // ICMM - Hotkey Display

        int x = mc.displayWidth / 2 - hotkeyFlyIn.castToInt();
        int y = mc.displayHeight / 2 - 50;
        drawRect(x - 3, y - 3, mc.displayWidth / 2, y + 20, new Color(0, 0, 0, 50).getRGB());
        HotkeyRenderer.render(Dragonfly.getFontManager().retrieveOrBuild("", 20), KeyEvent.VK_P, "Open Indicator Menu", x, y);
    }

    /**
     * Renders the playerlist, its background, headers and footers.
     */
    public void renderPlayerlist (int width, Scoreboard scoreboard, ScoreObjective scoreObjective)
    {
        final IFontRenderer fontRenderer = Dragonfly.getFontManager().getRegular();
        final double modifier = Math.max(1 - tablistFlyIn.get(), 0.1);
        final int offset = ( int ) ( tablistFlyIn.get() * estimatePlayerListHeight(fontRenderer, width) );
        final int backgroundColor = new Color(0, 0, 0, ( int ) ( 120 * modifier )).getRGB();

        if (tablistFlyIn.isAtStart())
            return;

        GlStateManager.translate(0, -offset, 0);

        NetHandlerPlayClient netHandler = this.mc.thePlayer.sendQueue;
        List<NetworkPlayerInfo> list = ENTRY_ORDERING.sortedCopy(netHandler.getPlayerInfoMap());
        int i = 0;
        int j = 0;

        for (NetworkPlayerInfo networkplayerinfo : list) {

            final String playerName = getPlayerName(networkplayerinfo);
            int k = fontRenderer.getStringWidth(playerName);
            i = Math.max(i, k);

            if (scoreObjective != null && scoreObjective.getRenderType() != IScoreObjectiveCriteria.EnumRenderType.HEARTS) {
                k = fontRenderer.getStringWidth(" " + scoreboard.getValueFromObjective(networkplayerinfo.getGameProfile().getName(), scoreObjective).getScorePoints());
                j = Math.max(j, k);
            }

        }

        list = list.subList(0, Math.min(list.size(), 80));
        int l3 = list.size();
        int i4 = l3;
        int amountOfColumns;

        for (amountOfColumns = 1; i4 > 20 ; i4 = ( l3 + amountOfColumns - 1 ) / amountOfColumns) {
            ++amountOfColumns;
        }

        // ICMM - The space that is being hold left of every player list entry in order to display indicators
        int indicatorAmount = IndicatorKt.calcMaxIndicatorCount(list);
        int horizontalColumnSpace = 8 + indicatorAmount * 8;

        boolean singleplayer = this.mc.isIntegratedServerRunning() || this.mc.getNetHandler().getNetworkManager().getIsencrypted();
        int l = scoreObjective != null ? scoreObjective.getRenderType() == IScoreObjectiveCriteria.EnumRenderType.HEARTS ? 90 : j : 0;
        int columnWidth = Math.min(amountOfColumns * ( ( singleplayer ? 9 : 0 ) + i + l + 13 ), width - 50) / amountOfColumns;
        int defaultX = width / 2 - ( columnWidth * amountOfColumns + ( amountOfColumns - 1 ) * horizontalColumnSpace ) / 2;
        int k1 = 10;
        int listWidth = columnWidth * amountOfColumns + amountOfColumns * ( horizontalColumnSpace );
        List<String> headerList = null;
        List<String> footerList = null;

        if (this.header != null) {
            headerList = fontRenderer.listFormattedStringToWidth(this.header.getFormattedText(), width - 50);

            for (String s : headerList) {
                listWidth = Math.max(listWidth, fontRenderer.getStringWidth(s));
            }
        }

        if (this.footer != null) {
            footerList = fontRenderer.listFormattedStringToWidth(this.footer.getFormattedText(), width - 50);

            for (String s2 : footerList) {
                listWidth = Math.max(listWidth, fontRenderer.getStringWidth(s2));
            }
        }

        listWidth += horizontalColumnSpace;
        if (headerList != null) {
            drawRect(width / 2 - listWidth / 2 - 1, k1 - 4, width / 2 + listWidth / 2 + 1, k1 + headerList.size() * fontRenderer.getHeight(), backgroundColor);

            for (String s3 : headerList) {
                int i2 = fontRenderer.getStringWidth(s3);
                fontRenderer.drawStringWithShadow(s3, ( float ) ( width / 2 - i2 / 2 ), ( float ) k1, new Color(255, 255, 255, ( int ) ( 255 * modifier )).getRGB());
                k1 += fontRenderer.getHeight();
            }

            ++k1;
        }

        drawRect(width / 2 - listWidth / 2 - 1, k1 - 1, width / 2 + listWidth / 2 + 1, k1 + i4 * 9, backgroundColor);

        for (int k4 = 0 ; k4 < l3 ; ++k4) {
            int columnIndex = k4 / i4;
            int i5 = k4 % i4;
            int x = defaultX + ( columnIndex * columnWidth ) + ( columnIndex * horizontalColumnSpace );

            int k2 = k1 + i5 * 9;
            drawRect(x, k2, x + columnWidth, k2 + 8, new Color(255, 255, 255, 30).getRGB());
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

            if (k4 < list.size()) {
                NetworkPlayerInfo playerInfo = list.get(k4);
                String s1 = getPlayerName(playerInfo);
                GameProfile gameprofile = playerInfo.getGameProfile();

                if (singleplayer) {
                    EntityPlayer entityplayer = this.mc.theWorld.getPlayerEntityByUUID(gameprofile.getId());
                    boolean flag1 = entityplayer != null && entityplayer.isWearing(EnumPlayerModelParts.CAPE) && ( gameprofile.getName().equals("Dinnerbone") || gameprofile.getName().equals("Grumm") );
                    this.mc.getTextureManager().bindTexture(playerInfo.getLocationSkin());
                    int l2 = 8 + ( flag1 ? 8 : 0 );
                    int i3 = 8 * ( flag1 ? -1 : 1 );
                    Gui.drawScaledCustomSizeModalRect(x, k2, 8.0F, ( float ) l2, 8, i3, 8, 8, 64.0F, 64.0F);

                    if (entityplayer != null && entityplayer.isWearing(EnumPlayerModelParts.HAT)) {
                        int j3 = 8 + ( flag1 ? 8 : 0 );
                        int k3 = 8 * ( flag1 ? -1 : 1 );
                        Gui.drawScaledCustomSizeModalRect(x, k2, 40.0F, ( float ) j3, 8, k3, 8, 8, 64.0F, 64.0F);
                    }

                    x += 9;
                }

                k2 += 1;

                if (playerInfo.getGameType() == WorldSettings.GameType.SPECTATOR) {
                    s1 = EnumChatFormatting.ITALIC + s1;
                    fontRenderer.drawStringWithShadow(s1, ( float ) x, ( float ) k2, new Color(0, 0, 0, 120).getRGB());
                } else {
                    fontRenderer.drawStringWithShadow(s1, ( float ) x, ( float ) k2, -1);
                }

                if (scoreObjective != null && playerInfo.getGameType() != WorldSettings.GameType.SPECTATOR) {
                    int k5 = x + i + 1;
                    int l5 = k5 + l;

                    if (l5 - k5 > 5) {
                        this.drawScoreboardValues(scoreObjective, k2, gameprofile.getName(), k5, l5, playerInfo);
                    }
                }

                this.drawPing(columnWidth, x - ( singleplayer ? 9 : 0 ) + 1, k2 - 1, playerInfo);

                int indicatorX = x - 13;

                for (Indicator indicator : playerInfo.getActiveIndicators().stream()
                    .sorted(Comparator.comparingInt(Indicator::getXIndex)).collect(Collectors.toList())) {

                    indicator.draw(indicatorX - 8, k2 - 1);
                    indicatorX -= 10;
                }
            }
        }

        if (footerList != null) {
            k1 = k1 + i4 * 9 + 1;
            drawRect(width / 2 - listWidth / 2 - 1, k1 - 1, width / 2 + listWidth / 2 + 1, k1 + footerList.size() * fontRenderer.getHeight() + 2, backgroundColor);

            for (String s4 : footerList) {
                int j5 = fontRenderer.getStringWidth(s4);
                fontRenderer.drawStringWithShadow(s4, ( float ) ( width / 2 - j5 / 2 ), ( float ) k1, new Color(255, 255, 255, ( int ) ( 255 * modifier )).getRGB());
                k1 += fontRenderer.getHeight();
            }
        }

        GlStateManager.translate(0, offset, 0);
    }

    public int estimatePlayerListHeight (IFontRenderer fontRenderer, int width)
    {
        NetHandlerPlayClient netHandler = this.mc.thePlayer.sendQueue;
        List<NetworkPlayerInfo> list = ENTRY_ORDERING.sortedCopy(netHandler.getPlayerInfoMap());

        int players = Math.min(18, list.size());
        int height = players * 12;

        if (this.header != null)
            height += fontRenderer.listFormattedStringToWidth(this.header.getFormattedText(), width - 50).size() * fontRenderer.getHeight();

        if (this.footer != null)
            height += fontRenderer.listFormattedStringToWidth(this.footer.getFormattedText(), width - 50).size() * fontRenderer.getHeight();

        return ( int ) ( height * 1.2 );
    }

    protected void drawPing (int param1, int param2, int param3, NetworkPlayerInfo networkPlayerInfoIn)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(icons);
        int pingLevel;

        if (networkPlayerInfoIn.getResponseTime() < 0) {
            pingLevel = 5;
        } else if (networkPlayerInfoIn.getResponseTime() < 150) {
            pingLevel = 0;
        } else if (networkPlayerInfoIn.getResponseTime() < 300) {
            pingLevel = 1;
        } else if (networkPlayerInfoIn.getResponseTime() < 600) {
            pingLevel = 2;
        } else if (networkPlayerInfoIn.getResponseTime() < 1000) {
            pingLevel = 3;
        } else {
            pingLevel = 4;
        }

        this.zLevel += 100.0F;
        this.drawTexturedModalRect(param2 + param1 - 11, param3, 0, 176 + pingLevel * 8, 10, 8);
        this.zLevel -= 100.0F;
    }

    private void drawScoreboardValues (ScoreObjective objective, int paramInt1, String name, int paramInt2, int paramInt3, NetworkPlayerInfo playerInfo)
    {
        int i = objective.getScoreboard().getValueFromObjective(name, objective).getScorePoints();

        if (objective.getRenderType() == IScoreObjectiveCriteria.EnumRenderType.HEARTS) {
            this.mc.getTextureManager().bindTexture(icons);

            if (this.lastTimeOpened == playerInfo.getRenderVisibilityId()) {
                if (i < playerInfo.getLastHealth()) {
                    playerInfo.setLastHealthTime(Minecraft.getSystemTime());
                    playerInfo.setHealthBlinkTime(this.guiIngame.getUpdateCounter() + 20);
                } else if (i > playerInfo.getLastHealth()) {
                    playerInfo.setLastHealthTime(Minecraft.getSystemTime());
                    playerInfo.setHealthBlinkTime(this.guiIngame.getUpdateCounter() + 10);
                }
            }

            if (Minecraft.getSystemTime() - playerInfo.getLastHealthTime() > 1000L || this.lastTimeOpened != playerInfo.getRenderVisibilityId()) {
                playerInfo.setLastHealth(i);
                playerInfo.setDisplayHealth(i);
                playerInfo.setLastHealthTime(Minecraft.getSystemTime());
            }

            playerInfo.setRenderVisibiltyId(this.lastTimeOpened);
            playerInfo.setLastHealth(i);
            int j = MathHelper.ceiling_float_int(( float ) Math.max(i, playerInfo.getDisplayHealth()) / 2.0F);
            int k = Math.max(MathHelper.ceiling_float_int(( float ) ( i / 2 )), Math.max(MathHelper.ceiling_float_int(( float ) ( playerInfo.getDisplayHealth() / 2 )), 10));
            boolean flag = playerInfo.getHealthBlinkTime() > ( long ) this.guiIngame.getUpdateCounter() && ( playerInfo.getHealthBlinkTime() - ( long ) this.guiIngame.getUpdateCounter() ) / 3L % 2L == 1L;

            if (j > 0) {
                float f = Math.min(( float ) ( paramInt3 - paramInt2 - 4 ) / ( float ) k, 9.0F);

                if (f > 3.0F) {
                    for (int l = j ; l < k ; ++l) {
                        this.drawTexturedModalRect(( float ) paramInt2 + ( float ) l * f, ( float ) paramInt1, flag ? 25 : 16, 0, 9, 9);
                    }

                    for (int j1 = 0 ; j1 < j ; ++j1) {
                        this.drawTexturedModalRect(( float ) paramInt2 + ( float ) j1 * f, ( float ) paramInt1, flag ? 25 : 16, 0, 9, 9);

                        if (flag) {
                            if (j1 * 2 + 1 < playerInfo.getDisplayHealth()) {
                                this.drawTexturedModalRect(( float ) paramInt2 + ( float ) j1 * f, ( float ) paramInt1, 70, 0, 9, 9);
                            }

                            if (j1 * 2 + 1 == playerInfo.getDisplayHealth()) {
                                this.drawTexturedModalRect(( float ) paramInt2 + ( float ) j1 * f, ( float ) paramInt1, 79, 0, 9, 9);
                            }
                        }

                        if (j1 * 2 + 1 < i) {
                            this.drawTexturedModalRect(( float ) paramInt2 + ( float ) j1 * f, ( float ) paramInt1, j1 >= 10 ? 160 : 52, 0, 9, 9);
                        }

                        if (j1 * 2 + 1 == i) {
                            this.drawTexturedModalRect(( float ) paramInt2 + ( float ) j1 * f, ( float ) paramInt1, j1 >= 10 ? 169 : 61, 0, 9, 9);
                        }
                    }
                } else {
                    float f1 = MathHelper.clamp_float(( float ) i / 20.0F, 0.0F, 1.0F);
                    int i1 = ( int ) ( ( 1.0F - f1 ) * 255.0F ) << 16 | ( int ) ( f1 * 255.0F ) << 8;
                    String s = "" + ( float ) i / 2.0F;

                    if (paramInt3 - this.mc.fontRendererObj.getStringWidth(s + "hp") >= paramInt2) {
                        s = s + "hp";
                    }

                    this.mc.fontRendererObj.drawStringWithShadow(s, ( float ) ( ( paramInt3 + paramInt2 ) / 2 - this.mc.fontRendererObj.getStringWidth(s) / 2 ), ( float ) paramInt1, i1);
                }
            }
        } else {
            String s1 = EnumChatFormatting.YELLOW + "" + i;
            this.mc.fontRendererObj.drawStringWithShadow(s1, ( float ) ( paramInt3 - this.mc.fontRendererObj.getStringWidth(s1) ), ( float ) paramInt1, 16777215);
        }
    }

    public void setFooter (IChatComponent footerIn)
    {
        this.footer = footerIn;
    }

    public void setHeader (IChatComponent headerIn)
    {
        this.header = headerIn;
    }

    public void func_181030_a ()
    {
        this.header = null;
        this.footer = null;
    }

    static class PlayerComparator implements Comparator<NetworkPlayerInfo>
    {
        private PlayerComparator ()
        {
        }

        public int compare (NetworkPlayerInfo p_compare_1_, NetworkPlayerInfo p_compare_2_)
        {
            ScorePlayerTeam scoreplayerteam = p_compare_1_.getPlayerTeam();
            ScorePlayerTeam scoreplayerteam1 = p_compare_2_.getPlayerTeam();
            return ComparisonChain.start().compareTrueFirst(p_compare_1_.getGameType() != WorldSettings.GameType.SPECTATOR, p_compare_2_.getGameType() != WorldSettings.GameType.SPECTATOR).compare(scoreplayerteam != null ? scoreplayerteam.getRegisteredName() : "", scoreplayerteam1 != null ? scoreplayerteam1.getRegisteredName() : "").compare(p_compare_1_.getGameProfile().getName(), p_compare_2_.getGameProfile().getName()).result();
        }
    }
}
