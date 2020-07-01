package net.inceptioncloud.dragonfly.utils;

import java.util.Arrays;

/**
 * Utility Methods used in the Runtime
 */
public class RuntimeUtils
{
    /**
     * Returns the class that from which the method was called by creating a Stack Trace
     * in the current Thread.
     *
     * @param exceptions The classes that shouldn't be counted
     *
     * @return The Stack Trace Element that was found
     */
    public static StackTraceElement getStackTrace (Class<?>... exceptions)
    {
        StackTraceElement[] array = Thread.currentThread().getStackTrace();
        for (int i = 1 ; i < array.length ; i++) {
            StackTraceElement stackTraceElement = array[i];
            if (isNoException(stackTraceElement, exceptions) && isNoException(stackTraceElement, Thread.class, RuntimeUtils.class))
                return stackTraceElement;
        }

        return null;
    }


    /**
     * Validates that the current class in the Stack Trace Element isn't in the exceptions list.
     *
     * @param stackTrace The Stack Trace Element
     * @param exceptions All exceptions
     *
     * @return Whether it is in there or not
     */
    private static boolean isNoException (StackTraceElement stackTrace, Class<?>... exceptions)
    {
        return Arrays.stream(exceptions).noneMatch(clazz -> stackTrace.getClassName().equals(clazz.getName()));
    }
}
