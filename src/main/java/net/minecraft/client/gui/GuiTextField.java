package net.minecraft.client.gui;

import net.inceptioncloud.dragonfly.engine.font.IFontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.MathHelper;

import java.util.function.Predicate;

public class GuiTextField extends Gui
{
    private final int id;
    private final IFontRenderer fontRendererInstance;
    /**
     * The width of this text field.
     */
    private final int width;
    private final int height;
    public int xPosition;
    public int yPosition;
    /**
     * Has the current text being edited on the textbox.
     */
    private String text = "";
    private int maxStringLength = 32;
    private int cursorCounter;
    private boolean enableBackgroundDrawing = true;
    /**
     * if true the textbox can lose focus by clicking elsewhere on the screen
     */
    private boolean canLoseFocus = true;
    /**
     * If this value is true along with isEnabled, keyTyped will process the keys.
     */
    private boolean isFocused;
    /**
     * If this value is true along with isFocused, keyTyped will process the keys.
     */
    private boolean isEnabled = true;
    /**
     * The current character index that should be used as start of the rendered text.
     */
    private int lineScrollOffset;
    private int cursorPosition;
    /**
     * other selection position, maybe the same as the cursor
     */
    private int selectionEnd;
    private int enabledColor = 14737632;
    private int disabledColor = 7368816;
    /**
     * True if this textbox is visible
     */
    private boolean visible = true;
    private GuiPageButtonList.GuiResponder guiResponder;
    private Predicate<String> validator = s -> true;

    public GuiTextField (int componentId, IFontRenderer fontrendererObj, int x, int y, int width, int height)
    {
        this.id = componentId;
        this.fontRendererInstance = fontrendererObj;
        this.xPosition = x;
        this.yPosition = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Getter Method for {@link #height}
     */
    public int getHeight ()
    {
        return height;
    }

    /**
     * Getter Method for {@link #xPosition}
     */
    public int getxPosition ()
    {
        return xPosition;
    }

    /**
     * Getter Method for {@link #yPosition}
     */
    public int getyPosition ()
    {
        return yPosition;
    }

    /**
     * Draws the textbox
     */
    public void drawTextBox ()
    {
        if (this.getVisible()) {
            if (this.getEnableBackgroundDrawing()) {
                drawRect(
                        this.xPosition - 1,
                        this.yPosition - 1,
                        this.xPosition + this.width + 1,
                        this.yPosition + this.height + 1,
                        -6250336
                );
                drawRect(
                        this.xPosition,
                        this.yPosition,
                        this.xPosition + this.width,
                        this.yPosition + this.height,
                        -16777216
                );
            }

            int color = this.isEnabled ? this.enabledColor : this.disabledColor;
            int cursorPos = this.cursorPosition - this.lineScrollOffset;
            int k = this.selectionEnd - this.lineScrollOffset;
            String visibleText = this.fontRendererInstance.trimStringToWidth(
                    this.text.substring(this.lineScrollOffset),
                    this.getWidth()
            );
            boolean cursorInBounds = cursorPos >= 0 && cursorPos <= visibleText.length();
            boolean cursorVisible = this.isFocused && this.cursorCounter / 6 % 2 == 0 && cursorInBounds;
            int x = this.enableBackgroundDrawing ? this.xPosition + 4 : this.xPosition;
            int y = this.enableBackgroundDrawing ? this.yPosition + (this.height - 8) / 2 : this.yPosition;
            int x1 = x;

            if (k > visibleText.length()) {
                k = visibleText.length();
            }

            if (visibleText.length() > 0) {
                String s1 = cursorInBounds ? visibleText.substring(0, cursorPos) : visibleText;
                x1 = this.fontRendererInstance.drawStringWithShadow(s1, (float) x, (float) y, color);
            }

            boolean cursorNotAtEnd = this.cursorPosition < this.text.length() || this.text.length() >= this.getMaxStringLength();
            int cursorX = x1;

            if (!cursorInBounds) {
                cursorX = cursorPos > 0 ? x + this.width : x;
            } else if (cursorNotAtEnd) {
                cursorX = x1 - 1;
                --x1;
            }

            if (visibleText.length() > 0 && cursorInBounds && cursorPos < visibleText.length()) {
                this.fontRendererInstance.drawStringWithShadow(visibleText.substring(cursorPos), (float) x1, (float) y, color);
            }

            if (cursorVisible) {
                if (cursorNotAtEnd) {
                    Gui.drawRect(cursorX, y - 1, cursorX + 1, y + 1 + this.fontRendererInstance.getHeight(), -3092272);
                } else {
                    this.fontRendererInstance.drawStringWithShadow("_", (float) cursorX, (float) y, color);
                }
            }

            if (k != cursorPos) {
                int l1 = x + this.fontRendererInstance.getStringWidth(visibleText.substring(0, k));
                this.drawCursorVertical(cursorX, y - 1, l1 - 1, y + 1 + this.fontRendererInstance.getHeight());
            }
        }
    }

    public void setGuiResponder (GuiPageButtonList.GuiResponder guiResonder)
    {
        this.guiResponder = guiResonder;
    }

    /**
     * Increments the cursor counter
     */
    public void updateCursorCounter ()
    {
        ++this.cursorCounter;
    }

    /**
     * Returns the contents of the textbox
     */
    public String getText ()
    {
        return this.text;
    }

    /**
     * Sets the text of the textbox
     */
    public void setText (String text)
    {
        if (this.validator.test(text)) {
            if (text.length() > this.maxStringLength) {
                this.text = text.substring(0, this.maxStringLength);
            } else {
                this.text = text;
            }

            this.setCursorPositionEnd();
        }
    }

    /**
     * returns the text between the cursor and selectionEnd
     */
    public String getSelectedText ()
    {
        int i = Math.min(this.cursorPosition, this.selectionEnd);
        int j = Math.max(this.cursorPosition, this.selectionEnd);
        return this.text.substring(i, j);
    }

    public void setValidator (java.util.function.Predicate<String> validator)
    {
        this.validator = validator;
    }

    /**
     * replaces selected text, or inserts text at the position on the cursor
     */
    public void writeText (String text) {
        String result = "";
        String allowedCharacters = ChatAllowedCharacters.filterAllowedCharacters(text);
        int i = Math.min(this.cursorPosition, this.selectionEnd);
        int j = Math.max(this.cursorPosition, this.selectionEnd);
        int k = this.maxStringLength - this.text.length() - (i - j);
        int l;

        if (this.text.length() > 0) {
            result = result + this.text.substring(0, i);
        }

        if (k < allowedCharacters.length()) {
            result = result + allowedCharacters.substring(0, k);
            l = k;
        } else {
            result = result + allowedCharacters;
            l = allowedCharacters.length();
        }

        if (this.text.length() > 0 && j < this.text.length()) {
            result = result + this.text.substring(j);
        }

        if (this.validator.test(result)) {
            this.text = result;
            this.moveCursorBy(i - this.selectionEnd + l);

            if (this.guiResponder != null) {
                this.guiResponder.setEntryValue(this.id, this.text);
            }
        }
    }

    /**
     * Deletes the specified number of words starting at the cursor position. Negative numbers will delete words left of
     * the cursor.
     */
    public void deleteWords (int num)
    {
        if (this.text.length() != 0) {
            if (this.selectionEnd != this.cursorPosition) {
                this.writeText("");
            } else {
                this.deleteFromCursor(this.getNthWordFromCursor(num) - this.cursorPosition);
            }
        }
    }

    /**
     * delete the selected text, otherwsie deletes characters from either side of the cursor. params: delete num
     */
    public void deleteFromCursor (int num)
    {
        if (this.text.length() != 0) {
            if (this.selectionEnd != this.cursorPosition) {
                this.writeText("");
            } else {
                boolean flag = num < 0;
                int i = flag ? this.cursorPosition + num : this.cursorPosition;
                int j = flag ? this.cursorPosition : this.cursorPosition + num;
                String s = "";

                if (i >= 0) {
                    s = this.text.substring(0, i);
                }

                if (j < this.text.length()) {
                    s = s + this.text.substring(j);
                }

                if (this.validator.test(s)) {
                    this.text = s;

                    if (flag) {
                        this.moveCursorBy(num);
                    }

                    if (this.guiResponder != null) {
                        this.guiResponder.setEntryValue(this.id, this.text);
                    }
                }
            }
        }
    }

    public int getId ()
    {
        return this.id;
    }

    /**
     * see @getNthNextWordFromPos() params: N, position
     */
    public int getNthWordFromCursor (int n)
    {
        return this.getNthWordFromPos(n, this.getCursorPosition());
    }

    /**
     * gets the position of the nth word. N may be negative, then it looks backwards. params: N, position
     */
    public int getNthWordFromPos (int n, int pos)
    {
        return this.getNthWordFromPosWS(n, pos, true);
    }

    public int getNthWordFromPosWS (int n, int pos, boolean skipWs)
    {
        int i = pos;
        boolean flag = n < 0;
        int j = Math.abs(n);

        for (int k = 0 ; k < j ; ++k) {
            if (!flag) {
                int l = this.text.length();
                i = this.text.indexOf(32, i);

                if (i == -1) {
                    i = l;
                } else {
                    while (skipWs && i < l && this.text.charAt(i) == 32) {
                        ++i;
                    }
                }
            } else {
                while (skipWs && i > 0 && this.text.charAt(i - 1) == 32) {
                    --i;
                }

                while (i > 0 && this.text.charAt(i - 1) != 32) {
                    --i;
                }
            }
        }

        return i;
    }

    /**
     * Moves the text cursor by a specified number of characters and clears the selection
     */
    public void moveCursorBy (int amount)
    {
        this.setCursorPosition(this.selectionEnd + amount);
    }

    /**
     * sets the cursors position to the beginning
     */
    public void setCursorPositionZero ()
    {
        this.setCursorPosition(0);
    }

    /**
     * sets the cursors position to after the text
     */
    public void setCursorPositionEnd ()
    {
        this.setCursorPosition(this.text.length());
    }

    /**
     * Call this method from your GuiScreen to process the keys into the textbox
     */
    public boolean textboxKeyTyped (char character, int keyCode)
    {
        if (!this.isFocused) {
            return false;
        } else if (GuiScreen.isKeyComboCtrlA(keyCode)) {
            this.setCursorPositionEnd();
            this.setSelectionPos(0);
            return true;
        } else if (GuiScreen.isKeyComboCtrlC(keyCode)) {
            GuiScreen.setClipboardString(this.getSelectedText());
            return true;
        } else if (GuiScreen.isKeyComboCtrlV(keyCode)) {
            if (this.isEnabled) {
                this.writeText(GuiScreen.getClipboardString());
            }

            return true;
        } else if (GuiScreen.isKeyComboCtrlX(keyCode)) {
            GuiScreen.setClipboardString(this.getSelectedText());

            if (this.isEnabled) {
                this.writeText("");
            }

            return true;
        } else {
            switch (keyCode) {
                case 14:
                    if (GuiScreen.isCtrlKeyDown()) {
                        if (this.isEnabled) {
                            this.deleteWords(-1);
                        }
                    } else if (this.isEnabled) {
                        this.deleteFromCursor(-1);
                    }

                    return true;

                case 199:
                    if (GuiScreen.isShiftKeyDown()) {
                        this.setSelectionPos(0);
                    } else {
                        this.setCursorPositionZero();
                    }

                    return true;

                case 203:
                    if (GuiScreen.isShiftKeyDown()) {
                        if (GuiScreen.isCtrlKeyDown()) {
                            this.setSelectionPos(this.getNthWordFromPos(-1, this.getSelectionEnd()));
                        } else {
                            this.setSelectionPos(this.getSelectionEnd() - 1);
                        }
                    } else if (GuiScreen.isCtrlKeyDown()) {
                        this.setCursorPosition(this.getNthWordFromCursor(-1));
                    } else {
                        this.moveCursorBy(-1);
                    }

                    return true;

                case 205:
                    if (GuiScreen.isShiftKeyDown()) {
                        if (GuiScreen.isCtrlKeyDown()) {
                            this.setSelectionPos(this.getNthWordFromPos(1, this.getSelectionEnd()));
                        } else {
                            this.setSelectionPos(this.getSelectionEnd() + 1);
                        }
                    } else if (GuiScreen.isCtrlKeyDown()) {
                        this.setCursorPosition(this.getNthWordFromCursor(1));
                    } else {
                        this.moveCursorBy(1);
                    }

                    return true;

                case 207:
                    if (GuiScreen.isShiftKeyDown()) {
                        this.setSelectionPos(this.text.length());
                    } else {
                        this.setCursorPositionEnd();
                    }

                    return true;

                case 211:
                    if (GuiScreen.isCtrlKeyDown()) {
                        if (this.isEnabled) {
                            this.deleteWords(1);
                        }
                    } else if (this.isEnabled) {
                        this.deleteFromCursor(1);
                    }

                    return true;

                default:
                    if (ChatAllowedCharacters.isAllowedCharacter(character)) {
                        if (this.isEnabled) {
                            this.writeText(Character.toString(character));
                        }

                        return true;
                    } else {
                        return false;
                    }
            }
        }
    }

    /**
     * Args: x, y, buttonClicked
     */
    public void mouseClicked(int x, int y, int button) {
        boolean flag = x >= this.xPosition && x < this.xPosition + this.width && y >= this.yPosition && y < this.yPosition + this.height;

        if (this.canLoseFocus) {
            this.setFocused(flag);
        }

        if (this.isFocused && flag && button == 0) {
            int i = x - this.xPosition;

            if (this.enableBackgroundDrawing) {
                i -= 4;
            }

            String s = this.fontRendererInstance.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getWidth());
            this.setCursorPosition(this.fontRendererInstance.trimStringToWidth(s, i).length() + this.lineScrollOffset);
        }
    }

    /**
     * draws the vertical line cursor in the textbox
     */
    private void drawCursorVertical(int left, int top, int right, int bottom) {
        if (left < right) {
            int i = left;
            left = right;
            right = i;
        }

        if (top < bottom) {
            int j = top;
            top = bottom;
            bottom = j;
        }

        if (right > this.xPosition + this.width) {
            right = this.xPosition + this.width;
        }

        if (left > this.xPosition + this.width) {
            left = this.xPosition + this.width;
        }

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.color(0.0F, 0.0F, 255.0F, 255.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.enableColorLogic();
        GlStateManager.colorLogicOp(5387);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(left, bottom, 0.0D).endVertex();
        worldrenderer.pos(right, bottom, 0.0D).endVertex();
        worldrenderer.pos(right, top, 0.0D).endVertex();
        worldrenderer.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.disableColorLogic();
        GlStateManager.enableTexture2D();
    }

    /**
     * returns the maximum number of character that can be contained in this textbox
     */
    public int getMaxStringLength ()
    {
        return this.maxStringLength;
    }

    public void setMaxStringLength (int p_146203_1_)
    {
        this.maxStringLength = p_146203_1_;

        if (this.text.length() > p_146203_1_) {
            this.text = this.text.substring(0, p_146203_1_);
        }
    }

    /**
     * returns the current position of the cursor
     */
    public int getCursorPosition ()
    {
        return this.cursorPosition;
    }

    /**
     * sets the position of the cursor to the provided index
     */
    public void setCursorPosition (int index)
    {
        this.cursorPosition = index;
        int i = this.text.length();
        this.cursorPosition = MathHelper.clamp_int(this.cursorPosition, 0, i);
        this.setSelectionPos(this.cursorPosition);
    }

    /**
     * get enable drawing background and outline
     */
    public boolean getEnableBackgroundDrawing ()
    {
        return this.enableBackgroundDrawing;
    }

    /**
     * enable drawing background and outline
     */
    public void setEnableBackgroundDrawing (boolean p_146185_1_)
    {
        this.enableBackgroundDrawing = p_146185_1_;
    }

    /**
     * Sets the text colour for this textbox (disabled text will not use this colour)
     */
    public void setTextColor (int p_146193_1_)
    {
        this.enabledColor = p_146193_1_;
    }

    public void setDisabledTextColour (int p_146204_1_)
    {
        this.disabledColor = p_146204_1_;
    }

    /**
     * Getter for the focused field
     */
    public boolean isFocused ()
    {
        return this.isFocused;
    }

    /**
     * Sets focus to this gui element
     */
    public void setFocused (boolean p_146195_1_)
    {
        if (p_146195_1_ && !this.isFocused) {
            this.cursorCounter = 0;
        }

        this.isFocused = p_146195_1_;
    }

    public void setEnabled (boolean p_146184_1_)
    {
        this.isEnabled = p_146184_1_;
    }

    /**
     * the side of the selection that is not the cursor, may be the same as the cursor
     */
    public int getSelectionEnd ()
    {
        return this.selectionEnd;
    }

    /**
     * returns the width of the textbox depending on if background drawing is enabled
     */
    public int getWidth ()
    {
        return this.getEnableBackgroundDrawing() ? this.width - 8 : this.width;
    }

    /**
     * Sets the position of the selection anchor (i.e. position the selection was started at)
     */
    public void setSelectionPos(int pos) {
        int i = this.text.length();

        if (pos > i) {
            pos = i;
        }

        if (pos < 0) {
            pos = 0;
        }

        this.selectionEnd = pos;

        if (this.fontRendererInstance != null) {
            if (this.lineScrollOffset > i) {
                this.lineScrollOffset = i;
            }

            int j = this.getWidth();
            String s = this.fontRendererInstance.trimStringToWidth(this.text.substring(this.lineScrollOffset), j);
            int k = s.length() + this.lineScrollOffset;

            if (pos == this.lineScrollOffset) {
                this.lineScrollOffset -= this.fontRendererInstance.trimStringToWidth(this.text, j, true).length();
            }

            if (pos > k) {
                this.lineScrollOffset += pos - k;
            } else if (pos <= this.lineScrollOffset) {
                this.lineScrollOffset -= this.lineScrollOffset - pos;
            }

            this.lineScrollOffset = MathHelper.clamp_int(this.lineScrollOffset, 0, i);
        }
    }

    /**
     * if true the textbox can lose focus by clicking elsewhere on the screen
     */
    public void setCanLoseFocus (boolean p_146205_1_)
    {
        this.canLoseFocus = p_146205_1_;
    }

    /**
     * returns true if this textbox is visible
     */
    public boolean getVisible ()
    {
        return this.visible;
    }

    /**
     * Sets whether or not this textbox is visible
     */
    public void setVisible (boolean p_146189_1_)
    {
        this.visible = p_146189_1_;
    }
}