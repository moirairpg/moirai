package me.moirai.storyengine.common.enums;

public enum CharacterAttribute {

    STRENGTH("Strength"),
    AGILITY("Agility"),
    VIGOR("Vigor"),
    INTELLIGENCE("Intelligence"),
    AWARENESS("Awareness"),
    CHARISMA("Charisma");

    private final String label;

    private CharacterAttribute(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
