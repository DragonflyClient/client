package dev.decobr.mcgeforce.utils;

public enum EnumHighlightType {

    KILL("mcdrgn_kill",10),
    KILLSTREAK("mcdrgn_killstreak",10),
    WIN("mcdrgn_win",10),
    DEATH("mcdrgn_death",10);

    private final String id;
    private final int time;

    EnumHighlightType(final String newId, final int newTime) {
        id = newId;
        time = newTime;
    }

    public String getId() { return id; }
    public int getTime() { return time; }

}
