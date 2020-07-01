package net.inceptioncloud.dragonfly.ui.components.list

import net.inceptioncloud.dragonfly.design.color.BluePalette
import net.inceptioncloud.dragonfly.design.color.RGB
import net.inceptioncloud.dragonfly.ui.renderer.RenderUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.*
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.MathHelper
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.GL11.*

/**
 * A modern scrollable list with a set of [UIListEntry] instances.
 */
class UIList(

        /**
         * Minecraft Client Instance
         */
        mc: Minecraft?,

        /**
         * Width of the entire list
         */
        width: Int,

        /**
         * Height of the entire list
         */
        height: Int,

        /**
         * X position of the entire list
         */
        val x: Int,

        /**
         * Y position of the entire list
         */
        val y: Int,

        /**
         * Height of the single entry slots
         */
        entryHeight: Int,

        /**
         * Width of the single entry slots
         */
        private val entryWidth: Int,

        /**
         * List of all entries
         */
        val entries: List<UIListEntry>

) : GuiSlot(mc, width, height, y, y + height, entryHeight)
{
    /**
     * Init Block for optimizing constructor parameters.
     */
    init
    {
        left = x
        right = left + width
    }

    /** The time when the last mouse event was performed. */
    private var lastMouseEvent: Long = 0

    /** The last used mouse button. */
    private var eventButton: Int = 0

    /** Distance that has already been scrolled with the mouse but has to be applied to the content and scroll bar. */
    private var toScroll: Int = 0

    /** Inherited method from [GuiSlot] that determines the size of the entry list. */
    override fun getSize(): Int = entries.size

    /** Overrides the default constant with the proper with variable. */
    override fun getListWidth(): Int
    {
        return width
    }

    /** Called when the list should draw a specific entry slot. */
    override fun drawSlot(index: Int, x: Int, y: Int, height: Int, mouseXIn: Int, mouseYIn: Int)
    {
        entries[index].cacheLocation(x, y)
        entries[index].drawEntry(x, y, height, entryWidth)
    }

    /** Returns true if the element passed in is currently selected. */
    override fun isSelected(index: Int): Boolean = entries[index].selected

    /** The element in the slot that was clicked, boolean for whether it was double clicked or not */
    override fun elementClicked(index: Int, isDoubleClick: Boolean, mouseX: Int, mouseY: Int)
    {
        val target = entries[index]
        target.clicked(isDoubleClick, mouseX - target.x, mouseY - target.y, entryWidth, entryHeight)
    }

    /** Empty method so the UIList has no background. */
    override fun drawBackground()
    {
    }

    /** Draws all slots and the selection box. */
    override fun drawSlots(x: Int, y: Int, mouseXIn: Int, mouseYIn: Int)
    {
        val size = this.size

        for (index in 0 until size)
        {
            val topY = y + (index * entryHeight)
            val targetHeight = entryHeight

            if (topY > bottom || topY + targetHeight < top)
            {
                updateItemPos(index, x, topY)
            }

            if (showSelectionBox && isSelected(index))
            {
                entries[index].drawSelectionEffect(x, x + entryWidth, topY, targetHeight)
            }

            drawSlot(index, x, topY, targetHeight, mouseXIn, mouseYIn)
        }
    }

    /** Called when the mouse scrolls a certain distance. */
    override fun scrollBy(amount: Int)
    {
        toScroll += amount
    }

    /** Improved screen drawing method. */
    override fun drawScreen(mouseXIn: Int, mouseYIn: Int, partialTicks: Float)
    {
        if (visible)
        {
            val scroll = toScroll / 15
            toScroll -= scroll
            amountScrolled += scroll

            mouseX = mouseXIn
            mouseY = mouseYIn
            val barLeft = this.scrollBarX
            val barRight = barLeft + 2
            bindAmountScrolled()

            GlStateManager.disableLighting()
            GlStateManager.disableFog()
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)

            val entriesLeft = left
            val entriesTop = top - getAmountScrolled()

            val scaledResolution = ScaledResolution(mc)
            val scisWidth = width * scaledResolution.scaleFactor
            val scisHeight = height * scaledResolution.scaleFactor
            val scisLeft = x * scaledResolution.scaleFactor
            val scisBottom = (mc.currentScreen.height - (y + height)) * scaledResolution.scaleFactor

//            --- Enable Scissors
            glEnable(GL_SCISSOR_TEST)
            glScissor(scisLeft, scisBottom, scisWidth, scisHeight)

//            --- Draw the slots
            drawSlots(entriesLeft, entriesTop, mouseXIn, mouseYIn)

//            --- Disable Scissors
            glDisable(GL_SCISSOR_TEST)

            GlStateManager.disableDepth()
            GlStateManager.enableBlend()
            GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1)
            GlStateManager.disableAlpha()
            GlStateManager.shadeModel(7425)
            GlStateManager.disableTexture2D()

            val maxScroll = this.maxScroll
            if (maxScroll > 0)
            {
                val backgroundColor = RGB.of(BluePalette.FOREGROUND).alpha(0.4F).toColor()
                val barColor = BluePalette.PRIMARY
                val barHeight = MathHelper.clamp_int(height * height / this.contentHeight, 32, height - 8)
                var barTop = getAmountScrolled() * (height - barHeight) / maxScroll + top

                if (barTop < top)
                {
                    barTop = top
                }

                // Background
                Gui.drawRect(barLeft, top, barRight, bottom, backgroundColor.rgb)

                // Bar
                RenderUtils.drawFilledCircle(barLeft + 1, barTop + 1, 1F, barColor)
                RenderUtils.drawFilledCircle(barLeft + 1, barTop + barHeight - 1, 1F, barColor)
                Gui.drawRect(barLeft, barTop + 1, barRight, barTop + barHeight - 1, barColor.rgb)

                // Bar corner fill
                if (bottom == barTop + barHeight)
                {
                    Gui.drawRect(barLeft, barTop + barHeight - 1, barRight, bottom, barColor.rgb)
                } else if (amountScrolled == 0F)
                {
                    Gui.drawRect(barLeft, barTop, barRight, barTop + 1, barColor.rgb)
                }
            }

            renderDecorations(mouseXIn, mouseYIn)
            GlStateManager.enableTexture2D()
            GlStateManager.shadeModel(7424)
            GlStateManager.enableAlpha()
            GlStateManager.disableBlend()
        }
    }

    /**
     * Improved mouse input method.
     */
    override fun handleMouseInput()
    {
        if (Mouse.isButtonDown(0) && enabled)
        {
            if (initialClickY == -1)
            {
                var flag1 = true
                if (mouseY in top..bottom)
                {
                    val listBoundLeft = x
                    val listBoundRight = x + listWidth
                    val l2 = mouseY - top - headerPadding + amountScrolled.toInt() - 4
                    val i1 = l2 / entryHeight
                    if (i1 < this.size && mouseX >= listBoundLeft && mouseX <= listBoundRight && i1 >= 0 && l2 >= 0)
                    {
                        val flag = entries[i1].selected && Minecraft.getSystemTime() - lastClicked < 250L
                        elementClicked(i1, flag, mouseX, mouseY)

                        entries[i1].updateSelectionState(true)
                        entries.filter { it != entries[i1] }.forEach { it.updateSelectionState(false) }

                        lastClicked = Minecraft.getSystemTime()
                    } else if (mouseX in listBoundLeft..listBoundRight && l2 < 0)
                    {
                        clickedHeader(mouseX - listBoundLeft, mouseY - top + amountScrolled.toInt() - 4)
                        flag1 = false
                    }
                    val i3 = this.scrollBarX
                    val j1 = i3 + 6
                    if (mouseX in i3..j1)
                    {
                        scrollMultiplier = -1.0f
                        var k1 = this.maxScroll
                        if (k1 < 1)
                        {
                            k1 = 1
                        }
                        var l1 = (((bottom - top) * (bottom - top)).toFloat() / this.contentHeight.toFloat()).toInt()
                        l1 = MathHelper.clamp_int(l1, 32, bottom - top - 8)
                        scrollMultiplier /= (bottom - top - l1).toFloat() / k1.toFloat()
                    } else
                    {
                        scrollMultiplier = 1.0f
                    }
                    initialClickY = if (flag1)
                    {
                        mouseY
                    } else
                    {
                        -2
                    }
                } else
                {
                    initialClickY = -2
                }
            } else if (initialClickY >= 0)
            {
                val distance = (-(mouseY - initialClickY)).toFloat() * scrollMultiplier
                scrollBy(distance.toInt())
                initialClickY = mouseY
            }
        } else
        {
            initialClickY = -1
        }
        var i2 = Mouse.getEventDWheel()
        if (i2 != 0)
        {
            i2 = if (i2 > 0)
            {
                -1
            } else
            {
                1
            }
            val distance = i2 * entryHeight / 2f
            scrollBy(distance.toInt())
        }
    }

    /**
     * Notifies all entries when the mouse is dragged (click-moved).
     *
     * This method fires whenever the action is performed on the screen. It doesn't have to be on the list or on any entry.
     */
    fun mouseDragged(mouseX: Int, mouseY: Int, eventButton: Int, duration: Long)
    {
        entries.forEach { it.mouseDragged(mouseX, mouseY, eventButton, duration) }
    }

    /**
     * Notifies all entries when the mouse is released.
     *
     * This method fires whenever the action is performed on the screen.
     * It doesn't have to be on the list or on any entry.
     */
    fun mouseReleased(mouseX: Int, mouseY: Int, eventButton: Int)
    {
        entries.forEach { it.mouseReleased(mouseX, mouseY, eventButton) }
    }

    /**
     * Notifies all entries when the mouse is pressed.
     *
     * This method fires whenever the action is performed on the screen.
     * It doesn't have to be on the list or on any entry.
     */
    fun mousePressed(mouseX: Int, mouseY: Int, eventButton: Int)
    {
        entries.forEach { it.mousePressed(mouseX, mouseY, eventButton) }
    }

    /**
     * Getter with the proper scroll bar position.
     */
    override fun getScrollBarX(): Int
    {
        return right
    }

    /**
     * Notifies all entries when a key is typed.
     *
     * This method fires whenever the action is performed on the screen.
     * It doesn't have to be on the list or on any entry.
     */
    fun keyTyped(typedChar: Char, keyCode: Int)
    {
        entries.forEach { it.keyTyped(typedChar, keyCode) }
    }
}

