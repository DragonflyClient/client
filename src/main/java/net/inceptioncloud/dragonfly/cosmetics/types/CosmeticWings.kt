package net.inceptioncloud.dragonfly.cosmetics.types

import net.inceptioncloud.dragonfly.cosmetics.*
import net.minecraft.client.entity.AbstractClientPlayer
import net.minecraft.client.model.ModelRenderer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.RenderPlayer
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import kotlin.math.exp


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

        var leftWing: ModelRenderer? = null
        var rightWing: ModelRenderer? = null

        val scale = 0.0625f

        init {
            textureWidth = 64
            textureHeight = 32

            leftWing = ModelRenderer(this, 0, 0)
            leftWing!!.addBox(-10f, 0f, -0.5f, 20, 29, 1)
            leftWing!!.setTextureSize(64, 32)

            rightWing = ModelRenderer(this, 0, 0)
            rightWing!!.addBox(-10f, 0f, -0.5f, 20, 29, 1)
            rightWing!!.setTextureSize(64, 32)
            rightWing!!.mirror = true

        }

        override fun render(entity: Entity, data: CosmeticRenderData) {
            val player = entity as? EntityPlayer ?: return
            val angle: Float = getWingAngle(player.capabilities.isFlying, 40f, 4000, 250, player.entityId)

            setRotation(leftWing!!, Math.toRadians(angle + 20.toDouble()).toFloat(), Math.toRadians(-4.0).toFloat(),
                Math.toRadians(6.0).toFloat())
            setRotation(rightWing!!, Math.toRadians(-angle - 20.toDouble()).toFloat(), Math.toRadians(4.0).toFloat(),
                Math.toRadians(6.0).toFloat())

            GL11.glPushMatrix()
            GL11.glTranslatef(0f, 4 * scale, 1.5f * scale)
            GL11.glRotatef(90f, 0f, 1f, 0f)
            GL11.glRotatef(90f, 0f, 0f, 1f)

            GL11.glPushMatrix()
            GL11.glTranslatef(0f, 0f, 0f * 3 * scale)
            GL11.glScalef(0.75F, 0.75F, 0.75F)
            leftWing!!.render(scale)
            GL11.glPopMatrix()

            GL11.glPushMatrix()
            GL11.glTranslatef(0f, 0f, -0f * 3 * scale)
            GL11.glScalef(0.75F, 0.75F, 0.75F)
            rightWing!!.render(scale)
            GL11.glPopMatrix()

            GL11.glPopMatrix()
        }

        fun getWingAngle(isFlying: Boolean, maxAngle: Float, totalTime: Int, flyingTime: Int, offset: Int): Float {
            val flapTime = if (isFlying) flyingTime else totalTime
            val deltaTime = getAnimationTime(flapTime, offset)
            println(flapTime)
            return if (deltaTime <= 0.5f) {
                sigmoid(-4 + (deltaTime * 2 * 8).toDouble())
            } else {
                1 - sigmoid(-4 + ((deltaTime * 2 - 1) * 8).toDouble())
            } * maxAngle
        }

        fun setRotation(model: ModelRenderer, x: Float, y: Float, z: Float) {
            model.rotateAngleX = x
            model.rotateAngleY = y
            model.rotateAngleZ = z
        }

        private fun sigmoid(value: Double): Float {
            return 1.0f / (1.0f + exp(-value).toFloat())
        }

        private fun getAnimationTime(totalTime: Int, offset: Int): Float {
            val time = (System.currentTimeMillis() + offset) % totalTime.toFloat()
            return time / totalTime
        }
    }
}