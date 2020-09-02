package dev.decobr.mcgeforce.utils;

public enum EnumHighlightType {

    KILL("mcdrgn_kill"),
    KILLSTREAK("mcdrgn_killstreak"),
    WIN("mcdrgn_win"),
    DEATH("mcdrgn_death"),
    LOSE("mcdrgn_lose");

    private final String id;

    EnumHighlightType(final String newId) {
        id = newId;
    }

    public String getId() { return id; }

}
