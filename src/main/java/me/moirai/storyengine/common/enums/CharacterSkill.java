package me.moirai.storyengine.common.enums;

public enum CharacterSkill {

    ATHLETICS("Athletics", CharacterAttribute.STRENGTH),
    ACROBATICS("Acrobatics", CharacterAttribute.AGILITY),
    STEALTH("Stealth", CharacterAttribute.AGILITY),
    ENDURANCE("Endurance", CharacterAttribute.VIGOR),
    LORE("Lore", CharacterAttribute.INTELLIGENCE),
    ALCHEMY("Alchemy", CharacterAttribute.INTELLIGENCE),
    DESTRUCTION("Destruction", CharacterAttribute.INTELLIGENCE),
    RESTORATION("Restoration", CharacterAttribute.INTELLIGENCE),
    ILLUSION("Illusion", CharacterAttribute.INTELLIGENCE),
    CONJURATION("Conjuration", CharacterAttribute.INTELLIGENCE),
    ALTERATION("Alteration", CharacterAttribute.INTELLIGENCE),
    PERCEPTION("Perception", CharacterAttribute.AWARENESS),
    SURVIVAL("Survival", CharacterAttribute.AWARENESS),
    INTUITION("Intuition", CharacterAttribute.AWARENESS),
    PERSUASION("Persuasion", CharacterAttribute.CHARISMA),
    DECEPTION("Deception", CharacterAttribute.CHARISMA),
    INTIMIDATION("Intimidation", CharacterAttribute.CHARISMA),
    PERFORMANCE("Performance", CharacterAttribute.CHARISMA);

    private final String label;
    private final CharacterAttribute attribute;

    private CharacterSkill(String label, CharacterAttribute attribute) {
        this.label = label;
        this.attribute = attribute;
    }

    public String getLabel() {
        return label;
    }

    public CharacterAttribute getAttribute() {
        return attribute;
    }
}
