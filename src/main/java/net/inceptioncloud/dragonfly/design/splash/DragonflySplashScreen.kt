package net.inceptioncloud.dragonfly.design.splash

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.Dragonfly.fontManager
import net.inceptioncloud.dragonfly.design.color.*
import net.inceptioncloud.dragonfly.engine.font.renderer.IFontRenderer
import net.inceptioncloud.dragonfly.engine.font.renderer.UnicodeFontRenderer
import net.inceptioncloud.dragonfly.engine.internal.WidgetColor
import net.inceptioncloud.dragonfly.utils.RuntimeUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.texture.TextureManager
import net.minecraft.client.shader.Framebuffer
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import java.awt.*
import java.text.DecimalFormat

/**
 * The customized InceptionCloud Mod Splash Screen.
 */
class DragonflySplashScreen {

    /** The default-sized custom Font Renderer. */
    private var defaultFR: IFontRenderer? = null

    /** A larger Font Renderer for drawing the title. */
    private var titleFR: IFontRenderer? = null

    /** A smaller Font Renderer for drawing the current action; */
    private var actionFR: IFontRenderer? = null

    /** The Resource Location of the InceptionCloud Logo. */
    private var logo: ResourceLocation? = null

    /** The percentage of the loading in decimal format. */
    private var percentage = 0.0

    private var targetPercentage = 0.0

    var additionalLoadingMillis = 0L

    /** The action that is currently being performed. */
    private var action: String? = null

    /** Whether the splash screen is active due to the loading of the client. */
    var isActive = true

    /**
     * Updates the splash screen if the Minecraft instance is able to do this.
     */
    fun update() {
        if (Minecraft.getMinecraft() == null || Minecraft.getMinecraft().languageManager == null || !isActive) return

        val element = RuntimeUtils.getStackTrace(DragonflySplashScreen::class.java)
        targetPercentage = (percentage + 1.0 / 20).coerceIn(0.0..1.0)
        action = if (element == null) "..." else element.className.split(".").last() + " - " + element.methodName

        if (Dragonfly.isDeveloperMode /* improve startup performance in developer mode */ ) {
            percentage = targetPercentage
        } else {
            val start = System.currentTimeMillis()
            while (targetPercentage > percentage) {
                percentage += 0.003
                performRender()
            }

            additionalLoadingMillis += System.currentTimeMillis() - start
        }
    }

    /**
     * The draw method for the splash screen.
     */
    fun performRender() {
        if (titleFR == null) titleFR = UnicodeFontRenderer.newInstance(fontManager.fontFamily + " Medium", 132, Font.PLAIN)
        if (defaultFR == null) defaultFR = UnicodeFontRenderer.newInstance(fontManager.fontFamily, 75, Font.PLAIN)
        if (actionFR == null) actionFR = UnicodeFontRenderer.newInstance(fontManager.fontFamily, 50, Font.PLAIN)
        if (logo == null) logo = ResourceLocation("dragonflyres/logos/splash.png")

        val resolution = Dimension(400, 500)
        val factor = 2.0
        val framebuffer = Framebuffer(resolution.width, resolution.height, true)
        framebuffer.bindFramebuffer(false)

        GlStateManager.matrixMode(GL11.GL_PROJECTION)
        GlStateManager.loadIdentity()
        GlStateManager.ortho(0.0, resolution.width / factor, resolution.height / factor, 0.0, 1000.0, 3000.0)
        GlStateManager.matrixMode(GL11.GL_MODELVIEW)
        GlStateManager.loadIdentity()
        GlStateManager.translate(0f, 0f, -2000f)
        GlStateManager.disableLighting()
        GlStateManager.disableFog()
        GlStateManager.disableDepth()
        GlStateManager.enableTexture2D()
        GlStateManager.resetColor()
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        GlStateManager.enableAlpha()

        drawScreen(Dimension((resolution.width / factor).toInt(), (resolution.height / factor).toInt()))

        framebuffer.unbindFramebuffer()
        framebuffer.framebufferRender(resolution.width, resolution.height)

        GlStateManager.enableAlpha()
        GlStateManager.alphaFunc(516, 0.1f)
        Minecraft.getMinecraft().updateDisplay()
    }

    /**
     * Draw the content of the Splash Screen.
     */
    private fun drawScreen(resolution: Dimension) {
        Gui.drawRect(0, 0, resolution.width, resolution.height, DragonflyPalette.background.rgb)
        resetState()

        Minecraft.getMinecraft().textureManager.bindTexture(logo)
        Gui.drawModalRectWithCustomSizedTexture(resolution.width / 2 - 45, 20, 0f, 0f, 90, 90, 90f, 90f)

        titleFR!!.drawCenteredString("Dragonfly", resolution.width / 2, 120, DragonflyPalette.accentNormal.rgb, true)
        defaultFR!!.drawCenteredString("Initializing...", resolution.width / 2, 150, DragonflyPalette.foreground.rgb, true)
        actionFR!!.drawCenteredString(action, resolution.width / 2, resolution.height - 50, Color(0x717577).rgb, true)

//        titleFR!!.drawCenteredString("InceptionCloud", resolution.scaledWidth / 2, 100, Color.WHITE.rgb, false)
//        defaultFR!!.drawCenteredString("Dragonfly", resolution.scaledWidth / 2, 126, Color(255, 255, 255, 170).rgb, false)
//        actionFR!!.drawCenteredString(action, resolution.scaledWidth / 2 - 15, 210, Color(255, 255, 255, 100).rgb, false)
//
        val rectWidth = resolution.width - 40
        val rectHeight = 15
        val rectLeft = resolution.width / 2 - rectWidth / 2
        val rectRight = resolution.width / 2 + rectWidth / 2
        val rectY = resolution.height - 35
        val loadingWidth = ((rectRight - rectLeft) * percentage).toInt()

        Gui.drawRect(rectLeft, rectY, rectRight, rectY + rectHeight, DragonflyPalette.foreground.rgb)
        Gui.drawRect(rectLeft + 1, rectY + 1, rectRight - 1, rectY + rectHeight - 1, DragonflyPalette.background.rgb)
        Gui.drawGradientHorizontal(rectLeft + 1, rectY + 1, rectLeft + loadingWidth - 1, rectY + rectHeight - 1,
            colorLeft.rgb, getProgressColor().rgb)

        actionFR!!.drawCenteredString("${(percentage * 100).toInt()}%", resolution.width / 2, rectY + 3, DragonflyPalette.foreground.rgb, true)
    }

    private val colorLeft = DragonflyPalette.accentBright
    private val colorRight = DragonflyPalette.accentDark

    private val offsetR = colorRight.red - colorLeft.red
    private val offsetG = colorRight.green - colorLeft.green
    private val offsetB = colorRight.blue - colorLeft.blue

    fun getProgressColor(): WidgetColor {
        return WidgetColor(
            colorLeft.red + (offsetR * percentage).toInt(),
            colorLeft.green + (offsetG * percentage).toInt(),
            colorLeft.blue + (offsetB * percentage).toInt()
        )
    }

    /**
     * Reset the GL color and texture state.
     */
    private fun resetState() {
        GlStateManager.resetColor()
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        GlStateManager.textureState[GlStateManager.activeTextureUnit].textureName = -1
    }

}
