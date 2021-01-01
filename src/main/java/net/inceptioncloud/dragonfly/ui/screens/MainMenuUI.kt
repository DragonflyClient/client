package net.inceptioncloud.dragonfly.ui.screens

import com.google.gson.JsonParser
import kotlinx.coroutines.runBlocking
import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.account.LoginStatusWidget
import net.inceptioncloud.dragonfly.apps.accountmanager.AccountManagerApp
import net.inceptioncloud.dragonfly.engine.font.Typography
import net.inceptioncloud.dragonfly.engine.font.font
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.widgets.assembled.DragonflyButton
import net.inceptioncloud.dragonfly.engine.widgets.assembled.TextField
import net.inceptioncloud.dragonfly.engine.widgets.primitive.Image
import net.inceptioncloud.dragonfly.ui.taskbar.Taskbar
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.*
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.util.ResourceLocation
import org.apache.logging.log4j.LogManager
import org.jetbrains.skija.*
import org.jetbrains.skiko.*
import org.jetbrains.skiko.Library
import java.awt.Color
import java.net.URL
import javax.imageio.ImageIO

class MainMenuUI : GuiScreen() {

    override var backgroundImage: SizedImage? = splashImage

    override var isNativeResolution: Boolean = true

    override fun initGui() {
        inited = false
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawRect(0, 0, width, height, Color.BLUE.rgb)

        if (!inited) {
            println("> Init & render")
            if (skijaState.context == null) {
                skijaState.context = makeGLContext()
            }
            inited = true
            TestRenderer.onInit()
            TestRenderer.onReshape(mc.displayWidth, mc.displayHeight)

            initSkija()

            skijaState.apply {
                canvas!!.clear(Color.PINK.rgb)
                TestRenderer.onRender(canvas!!, mc.displayWidth, mc.displayHeight)
                context!!.flush()
            }
        }
    }

    private val skijaState: SkijaState = SkijaState()
    private var inited: Boolean = false

    private val hardwareLayer = object : HardwareLayer() {
        override val contentScale: Float
            get() = 1.0F
    }

    fun reinit() {
        inited = false
    }

    override fun onGuiClosed() {
        super.onGuiClosed()
        TestRenderer.onDispose()
    }

    private fun initSkija() {
        val dpi = hardwareLayer.contentScale
        initRenderTarget(dpi)
        initSurface()
        scaleCanvas(dpi)
    }

    private fun scaleCanvas(dpi: Float) {
        skijaState.apply {
            canvas!!.scale(dpi, dpi)
        }
    }

    private fun initSurface() {
        skijaState.apply {
            surface = Surface.makeFromBackendRenderTarget(
                context,
                renderTarget,
                SurfaceOrigin.BOTTOM_LEFT,
                SurfaceColorFormat.RGBA_8888,
                ColorSpace.getSRGB()
            )
            canvas = surface!!.canvas
        }
    }

    private fun initRenderTarget(dpi: Float) {
        skijaState.apply {
            clear()
            val gl = OpenGLApi.instance
            println("render target")
            val fbId = gl.glGetIntegerv(gl.GL_DRAW_FRAMEBUFFER_BINDING)
            renderTarget = makeGLRenderTarget(
                mc.displayWidth,
                mc.displayHeight,
                0,
                0,
                fbId,
                FramebufferFormat.GR_GL_RGBA8
            )
        }
    }

    companion object {

        init {
            Library.load("/", "skiko")
        }

        /**
         * Load the splash image and its properties from the Dragonfly webserver and creates a [SizedImage]
         * based on them.
         */
        val splashImage: SizedImage by lazy {
            LogManager.getLogger().info("Downloading splash image...");

            try {
                val image = ImageIO.read(URL("https://cdn.icnet.dev/dragonfly/splash/image.png"))
                val properties = JsonParser().parse(
                    URL("https://cdn.icnet.dev/dragonfly/splash/properties.json").readText()
                ).asJsonObject

                val width = properties.get("width").asInt.toFloat()
                val height = properties.get("height").asInt.toFloat()

                SizedImage(ImageResource(DynamicTexture(image)), width, height)
            } catch (e: Exception) {
                LogManager.getLogger().warn("Could not download splash image! Using offline backup...")
                SizedImage(ImageResource("dragonflyres/splashes/offline-main-menu.png"), 1920.0f, 1080.0f)
            }
        }
    }
}

