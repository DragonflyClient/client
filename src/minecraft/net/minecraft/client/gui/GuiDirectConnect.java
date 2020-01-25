package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;

public class GuiDirectConnect extends GuiScreen
{
    private final GuiScreen previousScreen;
    private final ServerData serverData;
    private GuiTextField textField;

    public GuiDirectConnect (GuiScreen previousScreen, ServerData serverData)
    {
        this.previousScreen = previousScreen;
        this.serverData = serverData;
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        this.textField.updateCursorCounter();
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + 12, I18n.format("selectServer.select")));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.format("gui.cancel")));
        this.textField = new GuiTextField(2, this.fontRendererObj, this.width / 2 - 100, 116, 200, 20);
        this.textField.setMaxStringLength(128);
        this.textField.setFocused(true);
        this.textField.setText(this.mc.gameSettings.lastServer);
        this.buttonList.get(0).enabled = this.textField.getText().length() > 0 && this.textField.getText().split(":").length > 0;
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
        this.mc.gameSettings.lastServer = this.textField.getText();
        this.mc.gameSettings.saveOptions();
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.enabled)
        {
            if (button.id == 1)
            {
                this.previousScreen.confirmClicked(false, 0);
            }
            else if (button.id == 0)
            {
                this.serverData.serverIP = this.textField.getText();
                this.previousScreen.confirmClicked(true, 0);
            }
        }
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (this.textField.textboxKeyTyped(typedChar, keyCode))
        {
            this.buttonList.get(0).enabled = this.textField.getText().length() > 0 && this.textField.getText().split(":").length > 0;
        }
        else if (keyCode == 28 || keyCode == 156)
        {
            this.actionPerformed(this.buttonList.get(0));
        }
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.textField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        drawCenteredString(this.fontRendererObj, I18n.format("selectServer.direct"), this.width / 2, 20, 16777215);
        drawString(this.fontRendererObj, I18n.format("addServer.enterIp"), this.width / 2 - 100, 100, 10526880);
        this.textField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
