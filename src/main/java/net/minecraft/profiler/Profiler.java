package net.minecraft.profiler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.client.renderer.GlStateManager;
import optifine.Config;
import optifine.Lagometer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class Profiler
{
    private static final Logger logger = LogManager.getLogger();
    private static final String __OBFID = "CL_00001497";
    private static final String SCHEDULED_EXECUTABLES = "scheduledExecutables";
    private static final String TICK = "tick";
    private static final String PRE_RENDER_ERRORS = "preRenderErrors";
    private static final String RENDER = "render";
    private static final String DISPLAY = "display";
    private static final int HASH_SCHEDULED_EXECUTABLES = "scheduledExecutables".hashCode();
    private static final int HASH_TICK = "tick".hashCode();
    private static final int HASH_PRE_RENDER_ERRORS = "preRenderErrors".hashCode();
    private static final int HASH_RENDER = "render".hashCode();
    private static final int HASH_DISPLAY = "display".hashCode();
    /**
     * List of parent sections
     */
    private final List sectionList = Lists.newArrayList();
    /**
     * List of timestamps (System.nanoTime)
     */
    private final List timestampList = Lists.newArrayList();
    /**
     * Profiling map
     */
    private final Map profilingMap = Maps.newHashMap();
    /**
     * Flag profiling enabled
     */
    public boolean profilingEnabled;
    public boolean profilerGlobalEnabled = true;
    /**
     * Current profiling section
     */
    private String profilingSection = "";
    private boolean profilerLocalEnabled = true;

    /**
     * Clear profiling.
     */
    public void clearProfiling ()
    {
        this.profilingMap.clear();
        this.profilingSection = "";
        this.sectionList.clear();
        this.profilerLocalEnabled = this.profilerGlobalEnabled;
    }

    /**
     * Start section
     */
    public void startSection (String name)
    {
        if (Lagometer.isActive()) {
            int i = name.hashCode();

            if (i == HASH_SCHEDULED_EXECUTABLES && name.equals("scheduledExecutables")) {
                Lagometer.timerScheduledExecutables.start();
            } else if (i == HASH_TICK && name.equals("tick") && Config.isMinecraftThread()) {
                Lagometer.timerScheduledExecutables.end();
                Lagometer.timerTick.start();
            } else if (i == HASH_PRE_RENDER_ERRORS && name.equals("preRenderErrors")) {
                Lagometer.timerTick.end();
            }
        }

        if (Config.isFastRender()) {
            int j = name.hashCode();

            if (j == HASH_RENDER && name.equals("render")) {
                GlStateManager.clearEnabled = false;
            } else if (j == HASH_DISPLAY && name.equals("display")) {
                GlStateManager.clearEnabled = true;
            }
        }

        if (this.profilerLocalEnabled) {
            if (this.profilingEnabled) {
                if (this.profilingSection.length() > 0) {
                    this.profilingSection = this.profilingSection + ".";
                }

                this.profilingSection = this.profilingSection + name;
                this.sectionList.add(this.profilingSection);
                this.timestampList.add(System.nanoTime());
            }
        }
    }

    /**
     * End section
     */
    public void endSection ()
    {
        if (this.profilerLocalEnabled) {
            if (this.profilingEnabled) {
                long i = System.nanoTime();
                long j = (Long) this.timestampList.remove(this.timestampList.size() - 1);
                this.sectionList.remove(this.sectionList.size() - 1);
                long k = i - j;

                if (this.profilingMap.containsKey(this.profilingSection)) {
                    this.profilingMap.put(this.profilingSection, (Long) this.profilingMap.get(this.profilingSection) + k);
                } else {
                    this.profilingMap.put(this.profilingSection, k);
                }

                if (k > 100000000L) {
                    logger.warn("Something's taking too long! '" + this.profilingSection + "' took aprox " + (double) k / 1000000.0D + " ms");
                }

                this.profilingSection = !this.sectionList.isEmpty() ? (String) this.sectionList.get(this.sectionList.size() - 1) : "";
            }
        }
    }

    /**
     * Get profiling data
     */
    public List getProfilingData (String startSection)
    {
        this.profilerLocalEnabled = this.profilerGlobalEnabled;

        if (!this.profilerLocalEnabled) {
            return new ArrayList(Collections.singletonList(new Result("root", 0.0D, 0.0D)));
        } else if (!this.profilingEnabled) {
            return null;
        } else {
            long rootPercentage = this.profilingMap.containsKey("root") ? (Long) this.profilingMap.get("root") : 0L;
            long startSectionPercentage = this.profilingMap.containsKey(startSection) ? (Long) this.profilingMap.get(startSection) : -1L;
            ArrayList arraylist = Lists.newArrayList();

            if (startSection.length() > 0) {
                startSection = startSection + ".";
            }

            long fullSectionPercentage = 0L;

            for (Object s : this.profilingMap.keySet()) {
                if (((String) s).length() > startSection.length() && ((String) s).startsWith(startSection) && ((String) s).indexOf(".", startSection.length() + 1) < 0) {
                    fullSectionPercentage += (Long) this.profilingMap.get(s);
                }
            }

            float f = (float) fullSectionPercentage;

            if (fullSectionPercentage < startSectionPercentage) {
                fullSectionPercentage = startSectionPercentage;
            }

            if (rootPercentage < fullSectionPercentage) {
                rootPercentage = fullSectionPercentage;
            }

            for (Object s10 : this.profilingMap.keySet()) {
                String s1 = (String) s10;

                if (s1.length() > startSection.length() && s1.startsWith(startSection) && s1.indexOf(".", startSection.length() + 1) < 0) {
                    long subSectionPercentage = (Long) this.profilingMap.get(s1);
                    double percentageOfStartSection = (double) subSectionPercentage * 100.0D / (double) fullSectionPercentage;
                    double percentageOfRoot = (double) subSectionPercentage * 100.0D / (double) rootPercentage;
                    String s2 = s1.substring(startSection.length());
                    arraylist.add(new Profiler.Result(s2, percentageOfStartSection, percentageOfRoot));
                }
            }

            this.profilingMap.replaceAll((s, v) -> (Long) this.profilingMap.get(s) * 950L / 1000L);

            if ((float) fullSectionPercentage > f) {
                arraylist.add(new Profiler.Result("unspecified", (double) ((float) fullSectionPercentage - f) * 100.0D / (double) fullSectionPercentage, (double) ((float) fullSectionPercentage - f) * 100.0D / (double) rootPercentage));
            }

            Collections.sort(arraylist);
            arraylist.add(0, new Profiler.Result(startSection, 100.0D, (double) fullSectionPercentage * 100.0D / (double) rootPercentage));
            return arraylist;
        }
    }

    /**
     * End current section and start a new section
     */
    public void endStartSection (String name)
    {
        if (this.profilerLocalEnabled) {
            this.endSection();
            this.startSection(name);
        }
    }

    public String getNameOfLastSection ()
    {
        return this.sectionList.size() == 0 ? "[UNKNOWN]" : (String) this.sectionList.get(this.sectionList.size() - 1);
    }

    public static final class Result implements Comparable
    {
        private static final String __OBFID = "CL_00001498";
        public double percentageOfStartSection;
        public double percentageOfRoot;
        public String sectionName;

        public Result (String sectionName, double percentageOfStartSection, double percentageOfRoot)
        {
            this.sectionName = sectionName;
            this.percentageOfStartSection = percentageOfStartSection;
            this.percentageOfRoot = percentageOfRoot;
        }

        public int compareTo (Profiler.Result p_compareTo_1_)
        {
            return p_compareTo_1_.percentageOfStartSection < this.percentageOfStartSection ? -1 : (p_compareTo_1_.percentageOfStartSection > this.percentageOfStartSection ? 1 : p_compareTo_1_.sectionName
                    .compareTo(this.sectionName));
        }

        public int func_76329_a ()
        {
            return (this.sectionName.hashCode() & 11184810) + 4473924;
        }

        public int compareTo (Object p_compareTo_1_)
        {
            return this.compareTo((Profiler.Result) p_compareTo_1_);
        }
    }
}
