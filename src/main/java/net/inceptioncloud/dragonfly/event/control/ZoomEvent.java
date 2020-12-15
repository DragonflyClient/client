package net.inceptioncloud.dragonfly.event.control;

import net.inceptioncloud.dragonfly.event.Event;

/**
 * When the user presses the zoom keybind for zooming in.
 */
public class ZoomEvent implements Event {
    /**
     * The current field of view as a float. Can be modified.
     */
    private float fieldOfView;

    public ZoomEvent(final float fieldOfView) {
        this.fieldOfView = fieldOfView;
    }

    public float getFieldOfView() {
        return fieldOfView;
    }

    public void setFieldOfView(final float fieldOfView) {
        this.fieldOfView = fieldOfView;
    }
}
