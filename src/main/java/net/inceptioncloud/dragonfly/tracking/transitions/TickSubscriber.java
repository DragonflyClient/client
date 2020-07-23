package net.inceptioncloud.dragonfly.tracking.transitions;

import com.google.common.eventbus.Subscribe;
import net.inceptioncloud.dragonfly.Dragonfly;
import net.inceptioncloud.dragonfly.event.client.ClientTickEvent;
import net.inceptioncloud.dragonfly.transition.Transition;
import net.inceptioncloud.dragonfly.utils.TimeUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Subscribes to client ticks to collect data.
 */
public class TickSubscriber
{
    /**
     * The time the mod was initialized.
     */
    private final long startUp = System.currentTimeMillis();

    @Subscribe
    public void modTick (ClientTickEvent event)
    {
        if (!TransitionTracker.INSTANCE.getFrame().isVisible())
            return;

        TimeUtils.requireDelay("transition-tracking", 5000, () ->
        {
            final int trackingPoint = ( int ) ( ( System.currentTimeMillis() - startUp ) / 5000 );
            final Map<String, Integer> groupedAmounts =
                Dragonfly.getTransitions().stream().collect(Collectors.groupingBy(Transition::getOriginClass)).entrySet().stream() // Group Transitions by Origin
                    .map(( Function<Map.Entry<String, List<Transition>>, Map.Entry<String, Integer>> ) stringListEntry -> new Map.Entry<String, Integer>() // Map to an String-Integer Map by counting the amount of Transitions
                    {
                        @Override
                        public String getKey ()
                        {
                            return stringListEntry.getKey();
                        }

                        @Override
                        public Integer getValue ()
                        {
                            return stringListEntry.getValue().size();
                        }

                        @Override
                        public Integer setValue (final Integer value)
                        {
                            return value;
                        }
                    }).sorted(Map.Entry.comparingByValue()) // Sort by value
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new)); // Collect to LinkedHashMap

            TransitionTracker.INSTANCE.getData().add(new TrackingData(trackingPoint, groupedAmounts));
            TransitionTracker.INSTANCE.updateUI();
        });
    }
}
