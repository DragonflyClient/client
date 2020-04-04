package net.inceptioncloud.minecraftmod.design.zoom;

import com.google.common.eventbus.Subscribe;
import net.inceptioncloud.minecraftmod.event.control.ZoomEvent;
import net.inceptioncloud.minecraftmod.options.sets.IngameOptions;
import net.inceptioncloud.minecraftmod.transition.number.DoubleTransition;
import net.inceptioncloud.minecraftmod.transition.number.SmoothDoubleTransition;
import net.inceptioncloud.minecraftmod.transition.supplier.ForwardBackward;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.MouseFilter;
import optifine.Config;

/**
 * Listens to the {@link ZoomEvent} to provide a zoom animation if enabled.
 */
public class ZoomSubscriber
{
    /**
     * The transition that animates the OptiFine zoom.
     */
    private final SmoothDoubleTransition zoomFactor = SmoothDoubleTransition.builder().start(1.0).end(3.5).fadeIn(0).stay(20).fadeOut(15).autoTransformator(( ForwardBackward ) () -> Config.zoomMode).build();

    /**
     * {@link ZoomEvent} Subscriber
     */
    @Subscribe
    public void zoom (ZoomEvent event)
    {
        final Minecraft mc = Minecraft.getMinecraft();
        final EntityRenderer entityRenderer = mc.entityRenderer;

        boolean flag = false;
        float fov = event.getFieldOfView();

        if (mc.currentScreen == null) {
            GameSettings gamesettings = mc.gameSettings;
            flag = GameSettings.isKeyDown(gamesettings.ofKeyBindZoom);
        }

        if (IngameOptions.ZOOM_ANIMATION.get()) {

            if (flag) {
                if (!Config.zoomMode)
                    Config.zoomMode = true;
            } else if (Config.zoomMode) {
                Config.zoomMode = false;
                entityRenderer.mouseFilterXAxis = new MouseFilter();
                entityRenderer.mouseFilterYAxis = new MouseFilter();
                mc.renderGlobal.displayListEntitiesDirty = true;
            }

            fov /= zoomFactor.get();

        } else {

            if (flag) {
                if (!Config.zoomMode) {
                    Config.zoomMode = true;
                    mc.gameSettings.smoothCamera = true;
                }

                fov /= 4.0F;
            } else if (Config.zoomMode) {
                Config.zoomMode = false;
                mc.gameSettings.smoothCamera = false;
                entityRenderer.mouseFilterXAxis = new MouseFilter();
                entityRenderer.mouseFilterYAxis = new MouseFilter();
                mc.renderGlobal.displayListEntitiesDirty = true;
            }
        }

        event.setFieldOfView(fov);
    }
}
