package net.inceptioncloud.minecraftmod.event.control;

import lombok.*;

/**
 * When the user presses the zoom keybind for zooming in.
 */
@Getter
@Setter
@AllArgsConstructor
public class ZoomEvent
{
    /**
     * The current field of view as a float. Can be modified.
     */
    private float fieldOfView;
}
