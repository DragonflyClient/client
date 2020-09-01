package dev.decobr.mcgeforce.utils;

public enum EnumHighlightType {

    KILL("",30),
    DEATH("",30),
    WIN("",30);

    private final String id;
    private final int time;

    EnumHighlightType(final String newId, final int newTime) {
        id = newId;
        time = newTime;
    }

    public String getId() { return id; }
    public int getTime() { return time; }

}
