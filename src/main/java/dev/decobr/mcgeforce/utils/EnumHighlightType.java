package dev.decobr.mcgeforce.utils;

public enum EnumHighlightType {

    KILL("mcdrgn_kill"),
    WIN("mcdrgn_win"),
    DEATH("mcdrgn_death");

    private final String id;

    EnumHighlightType(final String newId) {
        id = newId;
    }

    public String getId() { return id; }

}
