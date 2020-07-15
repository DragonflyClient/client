package net.minecraft.util;

import net.inceptioncloud.dragonfly.Dragonfly;
import net.inceptioncloud.dragonfly.event.client.ScreenshotEvent;
import net.inceptioncloud.dragonfly.screenshot.ScreenshotUtilities;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.event.ClickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ScreenShotHelper
{
    private static final Logger logger = LogManager.getLogger();
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

    /**
     * A buffer to hold pixel values returned by OpenGL.
     */
    private static IntBuffer pixelBuffer;

    /**
     * The built-up array that contains all the pixel values returned by OpenGL.
     */
    private static int[] pixelValues;

    /**
     * Saves a screenshot in the game directory with a time-stamped filename.  Args: gameDirectory,
     * requestedWidthInPixels, requestedHeightInPixels, frameBuffer
     */
    public static void saveScreenshot (File gameDirectory, int width, int height, Framebuffer buffer, Consumer<IChatComponent> callback)
    {
        saveScreenshot(gameDirectory, null, width, height, buffer, callback);
    }

    /**
     * Saves a screenshot in the game directory with the given file name (or null to generate a time-stamped name).
     * Args: gameDirectory, fileName, requestedWidthInPixels, requestedHeightInPixels, frameBuffer
     */
    public static void saveScreenshot (File gameDirectory, String screenshotName, int width, int height, Framebuffer buffer, Consumer<IChatComponent> callback)
    {
        try {
            File screenshotsFolder = new File(gameDirectory, "screenshots");
            screenshotsFolder.mkdir();

            if (OpenGlHelper.isFramebufferEnabled()) {
                width = buffer.framebufferTextureWidth;
                height = buffer.framebufferTextureHeight;
            }

            int i = width * height;

            if (pixelBuffer == null || pixelBuffer.capacity() < i) {
                pixelBuffer = BufferUtils.createIntBuffer(i);
                pixelValues = new int[i];
            }

            GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
            GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
            pixelBuffer.clear();

            if (OpenGlHelper.isFramebufferEnabled()) {
                GlStateManager.bindTexture(buffer.framebufferTexture);
                GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);
            } else {
                GL11.glReadPixels(0, 0, width, height, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);
            }

            pixelBuffer.get(pixelValues);
            TextureUtil.processPixelValues(pixelValues, width, height);
            final BufferedImage[] bufferedimage = new BufferedImage[1];

            final int finalWidth = width;
            final int finalHeight = height;
            final int finalWidth1 = width;

            CompletableFuture.runAsync(() ->
            {
                if (OpenGlHelper.isFramebufferEnabled()) {
                    bufferedimage[0] = new BufferedImage(buffer.framebufferWidth, buffer.framebufferHeight, 1);
                    int j = buffer.framebufferTextureHeight - buffer.framebufferHeight;

                    for (int k = j ; k < buffer.framebufferTextureHeight ; ++k) {
                        for (int l = 0 ; l < buffer.framebufferWidth ; ++l) {
                            bufferedimage[0].setRGB(l, k - j, pixelValues[k * buffer.framebufferTextureWidth + l]);
                        }
                    }
                } else {
                    bufferedimage[0] = new BufferedImage(finalWidth, finalHeight, 1);
                    bufferedimage[0].setRGB(0, 0, finalWidth, finalHeight, pixelValues, 0, finalWidth1);
                }

                File file;

                if (screenshotName == null) {
                    LogManager.getLogger().info("screenshotsFolder = " + screenshotsFolder);
                    file = getTimestampedPNGFileForDirectory(screenshotsFolder);
                } else {
                    file = new File(screenshotsFolder, screenshotName);
                }

                LogManager.getLogger().info("screenshotsFolder.getAbsolutePath() = " + screenshotsFolder.getAbsolutePath());
                LogManager.getLogger().info("file.getAbsolutePath() = " + file.getAbsolutePath());

                final ScreenshotEvent event = new ScreenshotEvent(bufferedimage[0], file);
                Dragonfly.getEventBus().post(event);

                if (event.isCancelled()) {
                    return;
                }

                try {
                    ImageIO.write(bufferedimage[0], "png", file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                IChatComponent ichatcomponent = new ChatComponentText(file.getName());
                ichatcomponent.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file.getAbsolutePath()));
                ichatcomponent.getChatStyle().setUnderlined(Boolean.TRUE);

                if (!ScreenshotUtilities.screenshotTaken(bufferedimage[0], file)) {
                    callback.accept(new ChatComponentTranslation("screenshot.success", ichatcomponent));
                }
            });

        } catch (Exception exception) {
            logger.warn("Couldn't save screenshot", exception);
            callback.accept(new ChatComponentTranslation("screenshot.failure", exception.getMessage()));
        }
    }

    /**
     * Creates a unique PNG file in the given directory named by a timestamp.  Handles cases where the timestamp alone
     * is not enough to create a uniquely named file, though it still might suffer from an unlikely race condition where
     * the filename was unique when this method was called, but another process or thread created a file at the same
     * path immediately after this method returned.
     */
    private static File getTimestampedPNGFileForDirectory (File gameDirectory)
    {
        String s = dateFormat.format(new Date());
        int i = 1;

        while (true) {
            File file1 = new File(gameDirectory, s + ( i == 1 ? "" : "_" + i ) + ".png");

            if (!file1.exists()) {
                return file1;
            }

            ++i;
        }
    }
}
