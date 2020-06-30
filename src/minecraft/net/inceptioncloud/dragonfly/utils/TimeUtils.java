package net.inceptioncloud.dragonfly.utils;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Utility Methods for Time Tracking and Delay Execution.
 */
public class TimeUtils
{
    /**
     * When the task with the specific identifier was executed the last time.
     */
    private static final Map<String, Long> LAST_EXECUTIONS = Maps.newHashMap();

    /**
     * Run the task if the last execution of the identifier is long enough in the past.
     * @param identifier The identifier
     * @param millis The delay in milliseconds
     * @param runnable The task to execute
     */
    public static void requireDelay (String identifier, long millis, Runnable runnable)
    {
        long last = LAST_EXECUTIONS.getOrDefault(identifier, 0L);

        if (System.currentTimeMillis() - last <= millis)
            return;

        runnable.run();
        LAST_EXECUTIONS.put(identifier, System.currentTimeMillis());
    }
}
