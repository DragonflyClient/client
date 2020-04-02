package net.minecraft.client.network;

import com.google.common.base.Objects;
import com.mojang.authlib.GameProfile;
import lombok.Getter;
import lombok.Setter;
import net.inceptioncloud.minecraftmod.ui.playerlist.indicators.Indicator;
import net.inceptioncloud.minecraftmod.ui.playerlist.indicators.IndicatorKt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldSettings;
import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.List;

public class NetworkPlayerInfo
{
    /**
     * The GameProfile for the player represented by this NetworkPlayerInfo instance
     */
    private final GameProfile gameProfile;
    private final List<Indicator> activeIndicators = new ArrayList<>();
    private WorldSettings.GameType gameType;
    /**
     * Player response time to server in milliseconds
     */
    private int responseTime;
    private boolean playerTexturesLoaded = false;
    private ResourceLocation locationSkin;
    private ResourceLocation locationCape;
    private String skinType;

    /**
     * When this is non-null, it is displayed instead of the player's real name
     */
    private IChatComponent displayName;
    private int lastHealth = 0;
    private int displayHealth = 0;
    private long lastHealthTime = 0L;
    private long healthBlinkTime = 0L;
    private long renderVisibilityId = 0L;

    public NetworkPlayerInfo (GameProfile gameProfile)
    {
        this.gameProfile = gameProfile;
    }

    public NetworkPlayerInfo (S38PacketPlayerListItem.AddPlayerData addPlayerData)
    {
        this.gameProfile = addPlayerData.getProfile();
        this.gameType = addPlayerData.getGameMode();
        this.responseTime = addPlayerData.getPing();
        this.displayName = addPlayerData.getDisplayName();
    }

    /**
     * Updates whether a specific indicator should be active or not.
     *
     * @param indicatorClass The class of the indicator
     *
     * @return The result of the check
     */
    public boolean updateIndicator (Class<? extends Indicator> indicatorClass)
    {
        Indicator indicator = IndicatorKt.find(indicatorClass);
        Validate.notNull(indicator, "No indicator found for the given class!");

        boolean active = indicator.check(this);
        updateIndicator(indicator, active);

        return active;
    }

    /**
     * De(activates) the indicator depending on the parsed argument.
     */
    public void updateIndicator (Indicator indicator, boolean active)
    {
        if (!active)
            activeIndicators.remove(indicator);
        else if (!activeIndicators.contains(indicator))
            activeIndicators.add(indicator);
    }

    /**
     * Returns whether the given indicator is active or not.
     */
    public boolean isIndicatorActive (Class<? extends Indicator> indicatorClass) {
        return activeIndicators.contains(IndicatorKt.find(indicatorClass));
    }

    /**
     * Returns the GameProfile for the player represented by this NetworkPlayerInfo instance
     */
    public GameProfile getGameProfile ()
    {
        return this.gameProfile;
    }

    public WorldSettings.GameType getGameType ()
    {
        return this.gameType;
    }

    protected void setGameType (WorldSettings.GameType gameType)
    {
        this.gameType = gameType;
    }

    public int getResponseTime ()
    {
        return this.responseTime;
    }

    protected void setResponseTime (int responseTime)
    {
        this.responseTime = responseTime;
    }

    public boolean hasLocationSkin ()
    {
        return this.locationSkin != null;
    }

    public String getSkinType ()
    {
        return this.skinType == null ? DefaultPlayerSkin.getSkinType(this.gameProfile.getId()) : this.skinType;
    }

    public ResourceLocation getLocationSkin ()
    {
        if (this.locationSkin == null) {
            this.loadPlayerTextures();
        }

        return Objects.firstNonNull(this.locationSkin, DefaultPlayerSkin.getDefaultSkin(this.gameProfile.getId()));
    }

    public ResourceLocation getLocationCape ()
    {
        if (this.locationCape == null) {
            this.loadPlayerTextures();
        }

        return this.locationCape;
    }

    public ScorePlayerTeam getPlayerTeam ()
    {
        return Minecraft.getMinecraft().theWorld.getScoreboard().getPlayersTeam(this.getGameProfile().getName());
    }

    protected void loadPlayerTextures ()
    {
        synchronized (this) {
            if (!this.playerTexturesLoaded) {
                this.playerTexturesLoaded = true;
                Minecraft.getMinecraft().getSkinManager().loadProfileTextures(this.gameProfile, (type, location, profileTexture) ->
                {
                    switch (type) {
                        case SKIN:
                            NetworkPlayerInfo.this.locationSkin = location;
                            NetworkPlayerInfo.this.skinType = profileTexture.getMetadata("model");

                            if (NetworkPlayerInfo.this.skinType == null) {
                                NetworkPlayerInfo.this.skinType = "default";
                            }

                            break;

                        case CAPE:
                            NetworkPlayerInfo.this.locationCape = location;
                    }
                }, true);
            }
        }
    }

    public IChatComponent getDisplayName ()
    {
        return this.displayName;
    }

    public void setDisplayName (IChatComponent displayNameIn)
    {
        this.displayName = displayNameIn;
    }

    public int getLastHealth ()
    {
        return this.lastHealth;
    }

    public void setLastHealth (int lastHealth)
    {
        this.lastHealth = lastHealth;
    }

    public int getDisplayHealth ()
    {
        return this.displayHealth;
    }

    public void setDisplayHealth (int displayHealth)
    {
        this.displayHealth = displayHealth;
    }

    public long getLastHealthTime ()
    {
        return this.lastHealthTime;
    }

    public void setLastHealthTime (long lastHealthTime)
    {
        this.lastHealthTime = lastHealthTime;
    }

    public long getHealthBlinkTime ()
    {
        return this.healthBlinkTime;
    }

    public void setHealthBlinkTime (long healthBlinkTime)
    {
        this.healthBlinkTime = healthBlinkTime;
    }

    public long getRenderVisibilityId ()
    {
        return this.renderVisibilityId;
    }

    public void setRenderVisibiltyId (long renderVisibilityId)
    {
        this.renderVisibilityId = renderVisibilityId;
    }

    public List<Indicator> getActiveIndicators ()
    {
        return activeIndicators;
    }
}
