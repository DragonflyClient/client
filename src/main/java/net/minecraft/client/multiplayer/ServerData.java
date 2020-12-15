package net.minecraft.client.multiplayer;

import net.inceptioncloud.dragonfly.utils.Keep;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

@Keep
public class ServerData
{
    public String serverName;
    public String serverIP;
    public boolean lan;

    /**
     * the string indicating number of players on and capacity of the server that is shown on the server browser (i.e.
     * "5/20" meaning 5 slots used out of 20 slots total)
     */
    public transient String populationInfo;

    /**
     * (better variable name would be 'hostname') server name as displayed in the server browser's second line (grey
     * text)
     */
    public transient String serverMOTD;

    /**
     * last server ping that showed up in the server browser
     */
    public transient long pingToServer;
    public transient int version = 47;

    /**
     * Game version for this server.
     */
    public transient String gameVersion = "1.8.8";
    public transient boolean field_78841_f;
    public transient String playerList;
    private transient ServerData.ServerResourceMode resourceMode = ServerData.ServerResourceMode.PROMPT;
    private transient String serverIcon;

    public ServerData (String name, String ip, boolean lan)
    {
        this.serverName = name;
        this.serverIP = ip;
        this.lan = lan;
    }

    /**
     * Takes an NBTTagCompound with 'name' and 'ip' keys, returns a ServerData instance.
     */
    public static ServerData getServerDataFromNBTCompound (NBTTagCompound nbtCompound)
    {
        ServerData serverdata = new ServerData(nbtCompound.getString("name"), nbtCompound.getString("ip"), false);

        if (nbtCompound.hasKey("icon", 8)) {
            serverdata.setBase64EncodedIconData(nbtCompound.getString("icon"));
        }

        if (nbtCompound.hasKey("acceptTextures", 1)) {
            if (nbtCompound.getBoolean("acceptTextures")) {
                serverdata.setResourceMode(ServerData.ServerResourceMode.ENABLED);
            } else {
                serverdata.setResourceMode(ServerData.ServerResourceMode.DISABLED);
            }
        } else {
            serverdata.setResourceMode(ServerData.ServerResourceMode.PROMPT);
        }

        return serverdata;
    }

    /**
     * Returns an NBTTagCompound with the server's name, IP and maybe acceptTextures.
     */
    public NBTTagCompound getNBTCompound ()
    {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        nbttagcompound.setString("name", this.serverName);
        nbttagcompound.setString("ip", this.serverIP);

        if (this.serverIcon != null) {
            nbttagcompound.setString("icon", this.serverIcon);
        }

        if (this.resourceMode == ServerData.ServerResourceMode.ENABLED) {
            nbttagcompound.setBoolean("acceptTextures", true);
        } else if (this.resourceMode == ServerData.ServerResourceMode.DISABLED) {
            nbttagcompound.setBoolean("acceptTextures", false);
        }

        return nbttagcompound;
    }

    public ServerData.ServerResourceMode getResourceMode ()
    {
        return this.resourceMode;
    }

    public void setResourceMode (ServerData.ServerResourceMode mode)
    {
        this.resourceMode = mode;
    }

    /**
     * Returns the base-64 encoded representation of the server's icon, or null if not available
     */
    public String getBase64EncodedIconData ()
    {
        return this.serverIcon;
    }

    public void setBase64EncodedIconData (String icon)
    {
        this.serverIcon = icon;
    }

    public boolean isLan ()
    {
        return this.lan;
    }

    public void copyFrom (ServerData serverDataIn)
    {
        this.serverIP = serverDataIn.serverIP;
        this.serverName = serverDataIn.serverName;
        this.setResourceMode(serverDataIn.getResourceMode());
        this.serverIcon = serverDataIn.serverIcon;
        this.lan = serverDataIn.lan;
    }

    public enum ServerResourceMode
    {
        ENABLED("enabled"),
        DISABLED("disabled"),
        PROMPT("prompt");

        private final IChatComponent motd;

        ServerResourceMode(String name) {
            this.motd = new ChatComponentTranslation("addServer.resourcePack." + name);
        }

        public IChatComponent getMotd() {
            return this.motd;
        }
    }

    @Override
    public String toString() {
        return "ServerData{" +
                "serverName='" + serverName + '\'' +
                ", serverIP='" + serverIP + '\'' +
                ", lan=" + lan +
                '}';
    }
}
