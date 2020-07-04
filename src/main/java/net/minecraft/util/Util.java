package net.minecraft.util;

import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class Util
{
    public static Util.EnumOS getOSType ()
    {
        String s = System.getProperty("os.name").toLowerCase();
        return s.contains("win") ? Util.EnumOS.WINDOWS : ( s.contains("mac") ? Util.EnumOS.OSX : ( s.contains("solaris") ? Util.EnumOS.SOLARIS : ( s.contains("sunos") ? Util.EnumOS.SOLARIS : ( s.contains("linux") ? Util.EnumOS.LINUX : ( s.contains("unix") ? Util.EnumOS.LINUX : Util.EnumOS.UNKNOWN ) ) ) ) );
    }

    public static <V> V func_181617_a (FutureTask<V> futureTask, Logger logger)
    {
        try {
            futureTask.run();
            return futureTask.get();
        } catch (ExecutionException | InterruptedException executionexception) {
            logger.fatal("Error executing task", executionexception);
        }

        return null;
    }

    public enum EnumOS
    {
        LINUX,
        SOLARIS,
        WINDOWS,
        OSX,
        UNKNOWN
    }
}
