package me.moirai.storyengine.common.enums;

public enum CharacterClass {

    BARD("Bard"),
    RANGER("Ranger"),
    BARBARIAN("Barbarian"),
    PALADIN("Paladin"),
    MAGE("Mage"),
    ROGUE("Rogue"),
    WITCH("Witch"),
    CLERIC("Cleric");

    private final String label;

    private CharacterClass(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}