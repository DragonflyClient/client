package optifine;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;

import net.inceptioncloud.dragonfly.cosmetics.CapeManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.ls.LSOutput;

public class CapeUtils {

    private static CapeManager manager = new CapeManager();

    public static void downloadCape(final AbstractClientPlayer player) {

        String username = player.getNameClear();

        if (username != null && !username.isEmpty()) {

            String url = manager.getCapeURLByUsername(username);
            String mptHash = FilenameUtils.getBaseName(url);
            final ResourceLocation resourcelocation = new ResourceLocation("capeof/" + mptHash);
            TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
            ITextureObject texture = textureManager.getTexture(resourcelocation);

            if (texture != null && texture instanceof ThreadDownloadImageData) {

                ThreadDownloadImageData image = (ThreadDownloadImageData) texture;

                if (image.imageFound != null) {

                    if (image.imageFound.booleanValue()) {

                        player.setLocationOfCape(resourcelocation);
                    }

                    return;
                }
            }

            IImageBuffer iimagebuffer = new IImageBuffer() {
                final ImageBufferDownload ibd = new ImageBufferDownload();

                public BufferedImage parseUserSkin(BufferedImage image) {
                    return CapeUtils.parseCape(image);
                }

                public void skinAvailable() {
                    player.setLocationOfCape(resourcelocation);
                }
            };
            ThreadDownloadImageData threaddownloadimagedata1 = new ThreadDownloadImageData(null, url, null, iimagebuffer);
            threaddownloadimagedata1.pipeline = true;
            textureManager.loadTexture(resourcelocation, threaddownloadimagedata1);
        }
    }

    public static BufferedImage parseCape(BufferedImage p_parseCape_0_) {
        int i = 64;
        int j = 32;
        int k = p_parseCape_0_.getWidth();

        for (int l = p_parseCape_0_.getHeight(); i < k || j < l; j *= 2) {
            i *= 2;
        }

        BufferedImage bufferedimage = new BufferedImage(i, j, 2);
        Graphics graphics = bufferedimage.getGraphics();
        graphics.drawImage(p_parseCape_0_, 0, 0, null);
        graphics.dispose();
        return bufferedimage;
    }
}
