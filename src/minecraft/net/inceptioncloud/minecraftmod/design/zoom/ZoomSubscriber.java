package net.inceptioncloud.minecraftmod.design.zoom;

import com.google.common.eventbus.Subscribe;
import net.inceptioncloud.minecraftmod.event.control.ZoomEvent;
import net.inceptioncloud.minecraftmod.options.sections.OptionsSectionZoom;
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
    private final SmoothDoubleTransition zoomProgress = SmoothDoubleTransition.builder().start(0.0).end(1.0).fadeIn(0).stay(20).fadeOut(15).autoTransformator((ForwardBackward) () -> Config.zoomMode).build();

    /**
     * {@link ZoomEvent} Subscriber
     */
    @Subscribe
    public void zoom (ZoomEvent event)
    {
        final Minecraft mc = Minecraft.getMinecraft();
        final EntityRenderer entityRenderer = mc.entityRenderer;

        boolean keyDown = false;
        float fov = event.getFieldOfView();

        final boolean useAnimation = OptionsSectionZoom.getAnimation().getKey().get();
        final int fovPercent = OptionsSectionZoom.getFieldOfView().getKey().get();
        final int mousePercent = OptionsSectionZoom.getMouseSensitivity().getKey().get();

        final float fovDifferenceTotal = (float) (fov * (1 - fovPercent / 100D));
        final float fovDifferenceAnimated = (float) (fovDifferenceTotal * zoomProgress.get());
        final float mouseSensitivityDivisor = 100F / mousePercent;

        if (mc.currentScreen == null) {
            GameSettings gamesettings = mc.gameSettings;
            keyDown = GameSettings.isKeyDown(gamesettings.ofKeyBindZoom);
        }

        if (keyDown) {
            if (!Config.zoomMode) {
                Config.zoomMode = true;
                mc.gameSettings.mouseSensitivity /= mouseSensitivityDivisor;
            }

            if (!useAnimation) {
                fov -= fovDifferenceTotal;
            }
        } else if (Config.zoomMode) {
            Config.zoomMode = false;
            entityRenderer.mouseFilterXAxis = new MouseFilter();
            entityRenderer.mouseFilterYAxis = new MouseFilter();
            mc.renderGlobal.displayListEntitiesDirty = true;
            mc.gameSettings.mouseSensitivity *= mouseSensitivityDivisor;
        }

        if (useAnimation) {
            fov -= fovDifferenceAnimated;
        }

        event.setFieldOfView(fov);
    }
}
