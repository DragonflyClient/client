package net.minecraft.client.gui;

import net.minecraft.client.AnvilConverterException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

public class GuiSelectWorld extends GuiScreen implements GuiYesNoCallback
{
    private static final Logger logger = LogManager.getLogger();
    private final DateFormat field_146633_h = new SimpleDateFormat();
    protected GuiScreen parentScreen;
    protected String field_146628_f = "Select world";
    private boolean joiningWorld;
    private int field_146640_r;
    private java.util.List<SaveFormatComparator> worldList;
    private GuiSelectWorld.List selectWorldList;
    private String field_146637_u;
    private String field_146636_v;
    private final String[] field_146635_w = new String[4];
    private boolean field_146643_x;
    private GuiButton deleteButton;
    private GuiButton selectButton;
    private GuiButton renameButton;
    private GuiButton recreateButton;

    public GuiSelectWorld (GuiScreen parentScreenIn)
    {
        this.parentScreen = parentScreenIn;
    }

    public static GuiYesNo func_152129_a (GuiYesNoCallback p_152129_0_, String p_152129_1_, int p_152129_2_)
    {
        String s = I18n.format("selectWorld.deleteQuestion");
        String s1 = "'" + p_152129_1_ + "' " + I18n.format("selectWorld.deleteWarning");
        String s2 = I18n.format("selectWorld.deleteButton");
        String s3 = I18n.format("gui.cancel");
        return new GuiYesNo(p_152129_0_, s, s1, s2, s3, p_152129_2_);
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui ()
    {
        this.field_146628_f = I18n.format("selectWorld.title");

        try {
            this.func_146627_h();
        } catch (AnvilConverterException anvilconverterexception) {
            logger.error("Couldn't load level list", anvilconverterexception);
            this.mc.displayGuiScreen(new GuiErrorScreen("Unable to load worlds", anvilconverterexception.getMessage()));
            return;
        }

        this.field_146637_u = I18n.format("selectWorld.world");
        this.field_146636_v = I18n.format("selectWorld.conversion");
        this.field_146635_w[WorldSettings.GameType.SURVIVAL.getID()] = I18n.format("gameMode.survival");
        this.field_146635_w[WorldSettings.GameType.CREATIVE.getID()] = I18n.format("gameMode.creative");
        this.field_146635_w[WorldSettings.GameType.ADVENTURE.getID()] = I18n.format("gameMode.adventure");
        this.field_146635_w[WorldSettings.GameType.SPECTATOR.getID()] = I18n.format("gameMode.spectator");
        this.selectWorldList = new GuiSelectWorld.List(this.mc);
        this.selectWorldList.registerScrollButtons(4, 5);
        this.addButtons();
    }

    /**
     * Handles mouse input.
     */
    public void handleMouseInput () throws IOException
    {
        super.handleMouseInput();
        this.selectWorldList.handleMouseInput();
    }

    private void func_146627_h () throws AnvilConverterException
    {
        ISaveFormat isaveformat = this.mc.getSaveLoader();
        this.worldList = isaveformat.getSaveList();
        Collections.sort(this.worldList);
        this.field_146640_r = -1;
    }

    protected String getFolderNameForWorldIndex (int index)
    {
        return this.worldList.get(index).getFileName();
    }

    protected String getWorldNameForWorldIndex (int index)
    {
        String s = this.worldList.get(index).getDisplayName();

        if (StringUtils.isEmpty(s)) {
            s = I18n.format("selectWorld.world") + " " + ( index + 1 );
        }

        return s;
    }

    public void addButtons ()
    {
        this.buttonList.add(this.selectButton = new GuiButton(1, this.width / 2 - 154, this.height - 52, 150, 20, I18n.format("selectWorld.select")));
        this.buttonList.add(new GuiButton(3, this.width / 2 + 4, this.height - 52, 150, 20, I18n.format("selectWorld.create")));
        this.buttonList.add(this.renameButton = new GuiButton(6, this.width / 2 - 154, this.height - 28, 72, 20, I18n.format("selectWorld.rename")));
        this.buttonList.add(this.deleteButton = new GuiButton(2, this.width / 2 - 76, this.height - 28, 72, 20, I18n.format("selectWorld.delete")));
        this.buttonList.add(this.recreateButton = new GuiButton(7, this.width / 2 + 4, this.height - 28, 72, 20, I18n.format("selectWorld.recreate")));
        this.buttonList.add(new GuiButton(0, this.width / 2 + 82, this.height - 28, 72, 20, I18n.format("gui.cancel")));
        this.selectButton.enabled = false;
        this.deleteButton.enabled = false;
        this.renameButton.enabled = false;
        this.recreateButton.enabled = false;
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed (GuiButton button) throws IOException
    {
        if (button.enabled) {
            if (button.id == 2) {
                String s = this.getWorldNameForWorldIndex(this.field_146640_r);

                if (s != null) {
                    this.field_146643_x = true;
                    GuiYesNo guiyesno = func_152129_a(this, s, this.field_146640_r);
                    this.mc.displayGuiScreen(guiyesno);
                }
            } else if (button.id == 1) {
                this.joinSelectedWorld(this.field_146640_r);
            } else if (button.id == 3) {
                this.mc.displayGuiScreen(new GuiCreateWorld(this));
            } else if (button.id == 6) {
                this.mc.displayGuiScreen(new GuiRenameWorld(this, this.getFolderNameForWorldIndex(this.field_146640_r)));
            } else if (button.id == 0) {
                this.mc.displayGuiScreen(this.parentScreen);
            } else if (button.id == 7) {
                GuiCreateWorld guicreateworld = new GuiCreateWorld(this);
                ISaveHandler isavehandler = this.mc.getSaveLoader().getSaveLoader(this.getFolderNameForWorldIndex(this.field_146640_r), false);
                WorldInfo worldinfo = isavehandler.loadWorldInfo();
                isavehandler.flush();
                guicreateworld.func_146318_a(worldinfo);
                this.mc.displayGuiScreen(guicreateworld);
            } else {
                this.selectWorldList.actionPerformed(button);
            }
        }
    }

    public void joinSelectedWorld (int index)
    {
        this.mc.displayGuiScreen(null);

        if (!this.joiningWorld) {
            this.joiningWorld = true;
            String folderName = this.getFolderNameForWorldIndex(index);

            if (folderName == null) {
                folderName = "World" + index;
            }

            String worldName = this.getWorldNameForWorldIndex(index);

            if (worldName == null) {
                worldName = "World" + index;
            }

            if (this.mc.getSaveLoader().canLoadWorld(folderName)) {
                this.mc.launchIntegratedServer(folderName, worldName, null);
            }
        }
    }

    public void confirmClicked (boolean result, int id)
    {
        if (this.field_146643_x) {
            this.field_146643_x = false;

            if (result) {
                ISaveFormat isaveformat = this.mc.getSaveLoader();
                isaveformat.flushCache();
                isaveformat.deleteWorldDirectory(this.getFolderNameForWorldIndex(id));

                try {
                    this.func_146627_h();
                } catch (AnvilConverterException anvilconverterexception) {
                    logger.error("Couldn't load level list", anvilconverterexception);
                }
            }

            this.mc.displayGuiScreen(this);
        }
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen (int mouseX, int mouseY, float partialTicks)
    {
        this.selectWorldList.drawScreen(mouseX, mouseY, partialTicks);
        drawCenteredString(this.fontRendererObj, this.field_146628_f, this.width / 2, 20, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    class List extends GuiSlot
    {
        public List (Minecraft mcIn)
        {
            super(mcIn, GuiSelectWorld.this.width, GuiSelectWorld.this.height, 32, GuiSelectWorld.this.height - 64, 36);
        }

        protected int getSize ()
        {
            return GuiSelectWorld.this.worldList.size();
        }

        protected void elementClicked (int slotIndex, boolean isDoubleClick, int mouseX, int mouseY)
        {
            GuiSelectWorld.this.field_146640_r = slotIndex;
            boolean flag = GuiSelectWorld.this.field_146640_r >= 0 && GuiSelectWorld.this.field_146640_r < this.getSize();
            GuiSelectWorld.this.selectButton.enabled = flag;
            GuiSelectWorld.this.deleteButton.enabled = flag;
            GuiSelectWorld.this.renameButton.enabled = flag;
            GuiSelectWorld.this.recreateButton.enabled = flag;

            if (isDoubleClick && flag) {
                GuiSelectWorld.this.joinSelectedWorld(slotIndex);
            }
        }

        protected boolean isSelected (int slotIndex)
        {
            return slotIndex == GuiSelectWorld.this.field_146640_r;
        }

        protected int getContentHeight ()
        {
            return GuiSelectWorld.this.worldList.size() * 36;
        }

        protected void drawBackground ()
        {
            GuiSelectWorld.this.drawDefaultBackground();
        }

        protected void drawSlot (int entryID, int x, int y, int height, int mouseXIn, int mouseYIn)
        {
            SaveFormatComparator saveformatcomparator = GuiSelectWorld.this.worldList.get(entryID);
            String s = saveformatcomparator.getDisplayName();

            if (StringUtils.isEmpty(s)) {
                s = GuiSelectWorld.this.field_146637_u + " " + ( entryID + 1 );
            }

            String s1 = saveformatcomparator.getFileName();
            s1 = s1 + " (" + GuiSelectWorld.this.field_146633_h.format(new Date(saveformatcomparator.getLastTimePlayed()));
            s1 = s1 + ")";
            String s2 = "";

            if (saveformatcomparator.requiresConversion()) {
                s2 = GuiSelectWorld.this.field_146636_v + " " + s2;
            } else {
                s2 = GuiSelectWorld.this.field_146635_w[saveformatcomparator.getEnumGameType().getID()];

                if (saveformatcomparator.isHardcoreModeEnabled()) {
                    s2 = EnumChatFormatting.DARK_RED + I18n.format("gameMode.hardcore", new Object[0]) + EnumChatFormatting.RESET;
                }

                if (saveformatcomparator.getCheatsEnabled()) {
                    s2 = s2 + ", " + I18n.format("selectWorld.cheats");
                }
            }

            drawString(GuiSelectWorld.this.fontRendererObj, s, x + 2, y + 1, 16777215);
            drawString(GuiSelectWorld.this.fontRendererObj, s1, x + 2, y + 12, 8421504);
            drawString(GuiSelectWorld.this.fontRendererObj, s2, x + 2, y + 12 + 10, 8421504);
        }
    }
}
