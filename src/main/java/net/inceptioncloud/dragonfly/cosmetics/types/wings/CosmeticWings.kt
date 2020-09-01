package net.inceptioncloud.dragonfly.cosmetics.types.wings

import net.inceptioncloud.dragonfly.cosmetics.*
import net.inceptioncloud.dragonfly.cosmetics.logic.EnumCosmeticType
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.mc
import net.minecraft.client.model.ModelRenderer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11.*
import kotlin.math.cos
import kotlin.math.sin

object CosmeticWings : Cosmetic(1) {

    override val models = listOf(CosmeticWingsModel())
}