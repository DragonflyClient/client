package net.inceptioncloud.dragonfly.cosmetics.types

import net.inceptioncloud.dragonfly.cosmetics.*
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.toWidgetColor
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.AbstractClientPlayer
import net.minecraft.client.model.ModelRenderer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.RenderPlayer
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.cos
import kotlin.math.sin


class CosmeticWings(renderPlayer: RenderPlayer) : Cosmetic(renderPlayer) {

    private val resource = ResourceLocation("dragonflyres/metal-wings.png")

    private val modelWings = ModelWings(renderPlayer)

    override fun render(player: AbstractClientPlayer, partialTicks: Float, data: CosmeticRenderData) {

        GlStateManager.pushMatrix()

        renderPlayer.bindTexture(resource)
        if (player.isSneaking)
            GlStateManager.translate(0.0, 0.225, 0.0)

        GlStateManager.color(1.0F, 1.0F, 1.0F)
        modelWings.render(player, data)

        GlStateManager.popMatrix()

    }

    private class ModelWings(renderPlayer: RenderPlayer) : CosmeticModel(renderPlayer) {

        private var mc: Minecraft? = null
        private var location: ResourceLocation? = null
        private var wing: ModelRenderer? = null
        private var wingTip: ModelRenderer? = null
        private var playerUsesFullHeight = false
        private var partialTicks: Float = 0f

        init {
            mc = Minecraft.getMinecraft()
            location = ResourceLocation("dragonflyres/wings.png")
            playerUsesFullHeight = false

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

        private fun renderWings(player: EntityPlayer, partialTicks: Float, data: CosmeticRenderData) {
            val scale: Double = 70 / 100.0
            val rotate = player.cameraYaw.toDouble()
            GL11.glPushMatrix()
            GL11.glScaled(-scale, scale, -scale)
            GL11.glRotated(180 + rotate, 0.0, 1.0, 0.0) // Rotate the wings to be with the player.
            GL11.glTranslated(0.0, 0.1 / scale, 0.0) // Move wings correct amount up.
            GL11.glTranslated(0.0, 0.0, 0.2 / scale)
            if (player.isSneaking) {
                GL11.glTranslated(0.0, 0.125 / scale, 0.0)
            }
            mc!!.textureManager.bindTexture(location)

            val speed = 3000
            val percent = System.currentTimeMillis() % speed / speed.toFloat()
            val rainbowColor = Color.getHSBColor(percent, 1f, 1f)
            rainbowColor.toWidgetColor().glBindColor()

            for (j in 0..1) {
                GL11.glEnable(GL11.GL_CULL_FACE)
                val f11 = System.currentTimeMillis() % 1000 / 1000f * Math.PI.toFloat() * 2.0f
                wing!!.rotateAngleX = Math.toRadians(-80.0).toFloat() - cos(f11.toDouble()).toFloat() * 0.2f
                wing!!.rotateAngleY = Math.toRadians(20.0).toFloat() + sin(f11.toDouble()).toFloat() * 0.4f
                wing!!.rotateAngleZ = Math.toRadians(20.0).toFloat()
                wingTip!!.rotateAngleZ = -(sin((f11 + 2.0f).toDouble()) + 0.5).toFloat() * 0.35f
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

        private fun interpolate(yaw1: Float, yaw2: Float, percent: Float): Float {
            var f = (yaw1 + (yaw2 - yaw1) * percent) % 360
            if (f < 0) {
                f += 360f
            }
            return f
        }

        override fun setLivingAnimations(entitylivingbaseIn: EntityLivingBase?, p_78086_2_: Float, p_78086_3_: Float, partialTickTime: Float) {
            super.setLivingAnimations(entitylivingbaseIn, p_78086_2_, p_78086_3_, partialTickTime)
            this.partialTicks = partialTickTime
        }

        override fun render(entity: Entity, data: CosmeticRenderData) {
            renderWings(entity as EntityPlayer, partialTicks, data)
        }
    }
}