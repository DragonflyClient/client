package net.inceptioncloud.dragonfly.apps.cosmetics

import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.GraphicsEngine
import net.inceptioncloud.dragonfly.engine.contains
import net.inceptioncloud.dragonfly.engine.font.Typography
import net.inceptioncloud.dragonfly.engine.font.font
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.structure.IDimension
import net.inceptioncloud.dragonfly.engine.structure.IPosition
import net.inceptioncloud.dragonfly.engine.widgets.assembled.TextField
import net.inceptioncloud.dragonfly.mc
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.*
import net.minecraft.entity.EntityLivingBase

class PlayerPreview(
    initializerBlock: (PlayerPreview.() -> Unit)? = null
) : AssembledWidget<PlayerPreview>(initializerBlock), IPosition, IDimension {

    override var x: Double by property(0.0)
    override var y: Double by property(0.0)
    override var width: Double by property(0.0)
    override var height: Double by property(0.0)

    private var rotationHorizontal: Double = 0.0
    private var rotationVertical: Double = 0.0

    private var isDragging: Boolean = false
    private var dragStart: MouseData? = null

    private var originRotationHorizontal: Double = 0.0
    private var originRotationVertical: Double = 0.0

    override fun assemble(): Map<String, Widget<*>> = mapOf(
        "container" to TextField()
    )

    override fun updateStructure() {
        "container"<TextField> {
            staticText = if (mc.thePlayer == null) {
                "Cosmetics preview is unavailable while not ingame!\n\n" +
                        "§7Enter a world or join a server to enable the cosmetics preview for your current account."
            } else ""

            fontRenderer = font(Typography.LARGE)
            x = this@PlayerPreview.x
            y = this@PlayerPreview.y
            width = this@PlayerPreview.width
            height = this@PlayerPreview.height
            textAlignVertical = Alignment.CENTER
            textAlignHorizontal = Alignment.CENTER
            padding = width / 8.0
            backgroundColor = DragonflyPalette.foreground.darker(1.0)
            color = DragonflyPalette.background
        }
    }

    override fun handleMousePress(data: MouseData) {
        if (data in this) {
            dragStart = data
            originRotationHorizontal = rotationHorizontal
            originRotationVertical = rotationVertical
            isDragging = true
        }
        super.handleMousePress(data)
    }

    override fun handleMouseRelease(data: MouseData) {
        isDragging = false
        dragStart = null
        super.handleMouseRelease(data)
    }

    override fun render() {
        super.render()

        if (isDragging) {
            val dragStart = dragStart!!
            val curr = GraphicsEngine.getMouseData()

            if (curr in this) {
                val distanceX = (curr.mouseX - dragStart.mouseX) / (width / 2.0)
                val distanceY = (curr.mouseY - dragStart.mouseY) / (height / 2.0)

                rotationHorizontal = (originRotationHorizontal + distanceX)
                rotationVertical = (originRotationVertical + distanceY)
            }
        }

        val player = mc.thePlayer ?: return
        val x = x + (width / 2.0)
        val y = y + (height / 2.0) + 150.0

        GlStateManager.color(1f, 1f, 1f, 1f)
        drawEntityOnScreen(x, y, 200, player)
    }

    private fun drawEntityOnScreen(posX: Double, posY: Double, scale: Int, ent: EntityLivingBase) {
//        val rotationHorizontal = ((GraphicsEngine.getMouseX() - x - (width / 2)) / width).coerceIn(-1.0..1.0)
//        val rotationVertical = ((GraphicsEngine.getMouseY() - y - (width / 2)) / height).coerceIn(-1.0..1.0)

        GlStateManager.enableColorMaterial()
        GlStateManager.pushMatrix()
        GlStateManager.translate(posX.toFloat(), posY.toFloat(), 500.0f)
        GlStateManager.scale((-scale).toFloat(), scale.toFloat(), scale.toFloat())
        GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f)

        val f = ent.renderYawOffset
        val f1 = ent.rotationYaw
        val f2 = ent.rotationPitch
        val f3 = ent.prevRotationYawHead
        val f4 = ent.rotationYawHead

        RenderHelper.enableStandardItemLighting()
        GlStateManager.disableLighting()
        GlStateManager.rotate(135.0f, 0.0f, 1.0f, 0.0f)
        GlStateManager.rotate(-135.0f, 0.0f, 1.0f, 0.0f)
        GlStateManager.rotate((rotationVertical * 40.0f).toFloat(), 1.0f, 0.0f, 0.0f)

        ent.renderYawOffset = -(rotationHorizontal * 180).toFloat()
        ent.rotationYaw = ent.renderYawOffset
        ent.rotationYawHead = ent.renderYawOffset
        ent.prevRotationYawHead = ent.rotationYaw
        ent.rotationPitch = 0.0f

        GlStateManager.translate(0.0f, 0.0f, 0.0f)

        val renderManager = Minecraft.getMinecraft().renderManager
        renderManager.setPlayerViewY(180.0f)
        renderManager.isRenderShadow = false
        renderManager.renderEntityWithPosYaw(ent, 0.0, 0.0, 0.0, 0.0f, 1.0f)
        renderManager.isRenderShadow = true

        ent.renderYawOffset = f
        ent.rotationYaw = f1
        ent.rotationPitch = f2
        ent.prevRotationYawHead = f3
        ent.rotationYawHead = f4

        GlStateManager.popMatrix()
        RenderHelper.disableStandardItemLighting()
        GlStateManager.disableRescaleNormal()
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit)
        GlStateManager.disableTexture2D()
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit)
    }
}