package net.inceptioncloud.dragonfly.cosmetics.types.wings

import net.inceptioncloud.dragonfly.cosmetics.CosmeticModel
import net.inceptioncloud.dragonfly.cosmetics.CosmeticRenderProperties
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.mc
import net.minecraft.client.model.ModelRenderer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import kotlin.math.cos
import kotlin.math.sin

class CosmeticWingsModel : CosmeticModel() {

    private val location: ResourceLocation = ResourceLocation("dragonflyres/wings.png")
    private var wing: ModelRenderer? = null
    private var wingTip: ModelRenderer? = null

    init {
        // Set texture offsets.
        setTextureOffset("wing.bone", 0, 0)
        setTextureOffset("wing.skin", -10, 8)
        setTextureOffset("wingtip.bone", 0, 5)
        setTextureOffset("wingtip.skin", -10, 18)

        // Create wing model renderer.
        wing = ModelRenderer(this, "wing")
        wing!!.setTextureSize(30, 30) // 300px / 10px
        wing!!.setRotationPoint(-1f, 0f, 0f)
        wing!!.addBox("bone", -10.0f, -1.0f, -1.0f, 10, 2, 2)
        wing!!.addBox("skin", -10.0f, 0.0f, 0.5f, 10, 0, 10)

        // Create wing tip model renderer.
        wingTip = ModelRenderer(this, "wingtip")
        wingTip!!.setTextureSize(30, 30) // 300px / 10px
        wingTip!!.setRotationPoint(-10.0f, 0.0f, 0.0f)
        wingTip!!.addBox("bone", -10.0f, -0.5f, -0.5f, 10, 1, 1)
        wingTip!!.addBox("skin", -10.0f, 0.0f, 0.5f, 10, 0, 10)
        wing!!.addChild(wingTip) // Make the wingtip rotate around the wing.
    }

    override fun render(player: EntityPlayer, properties: CosmeticRenderProperties) {
        val enableBlending = false
        val enableShadows = false
        val color = DragonflyPalette.accentNormal
        val scale = 0.7 // 0.4 - 1.0
        val tilt = 15.0 // 0.0 - 15.0
        val height = 0.25 // 0.15 - 0.3
        val rotationX = 0.4 // 0.0 - 0.4
        val rotationY = 0.2 // 0.0 - 0.4
        val rotationZ = 1.0 // 0.5 - 1.0
        val wingTipRotation = 0.6 // 0.0 - 1.0

        val rotate = player.cameraYaw.toDouble()

        GL11.glPushMatrix()
        GL11.glScaled(-scale, scale, -scale)
        GL11.glRotated(180 + rotate, 0.0, 1.0, 0.0) // Rotate the wings to be with the player.
        GL11.glRotated(tilt / scale, 1.0, 0.0, 0.0)
        GL11.glTranslated(0.0, height / scale, 0.0) // Move wings correct amount up.
        GL11.glTranslated(0.0, 0.0, 0.10)
        if (player.isSneaking) {
            GL11.glTranslated(0.0, 0.25, 0.15)
            GL11.glRotated(-tilt / scale, 1.0, 0.0, 0.0)
            GL11.glRotated(20.0, 1.0, 0.0, 0.0)
        }
        mc.textureManager.bindTexture(location)

        color.glBindColor()

        if (enableShadows) {
            GlStateManager.enableLighting()
        } else {
            GlStateManager.disableLighting()
        }

        if (enableBlending) {
            GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE)
        } else {
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        }

        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0f)
        GlStateManager.enableBlend()

        for (j in 0..1) {
            GL11.glEnable(GL11.GL_CULL_FACE)

            val f11 = System.currentTimeMillis() % 1000 / 1000f * Math.PI.toFloat() * 2.0f
            wing!!.rotateAngleX = Math.toRadians(-80.0).toFloat() - cos(f11.toDouble()).toFloat() * rotationX.toFloat()
            wing!!.rotateAngleY = Math.toRadians(20.0).toFloat() + sin(f11.toDouble()).toFloat() * rotationY.toFloat()
            wing!!.rotateAngleZ = Math.toRadians(20.0).toFloat() * rotationZ.toFloat()
            wingTip!!.rotateAngleZ = -(sin((f11 + 2.0f).toDouble()) + 0.5).toFloat() * wingTipRotation.toFloat()
            wing!!.render(0.0625f)
            GL11.glScalef(-1.0f, 1.0f, 1.0f)
            if (j == 0) {
                GL11.glCullFace(1028)
            }
        }

        GL11.glCullFace(1029)
        GL11.glDisable(GL11.GL_CULL_FACE)
        GL11.glColor3f(255f, 255f, 255f)
        GL11.glPopMatrix()
    }
}