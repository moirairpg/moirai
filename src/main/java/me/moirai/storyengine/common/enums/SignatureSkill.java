package me.moirai.storyengine.common.enums;

public enum SignatureSkill {

    INSPIRE("Inspire", CharacterAttribute.CHARISMA),
    DEADEYE("Deadeye", CharacterAttribute.AGILITY),
    BERSERK("Berserk", CharacterAttribute.VIGOR),
    ZEAL("Zeal", CharacterAttribute.CHARISMA),
    SPELLWEAVE("Spellweave", CharacterAttribute.INTELLIGENCE),
    BACKSTAB("Backstab", CharacterAttribute.AGILITY),
    HEX("Hex", CharacterAttribute.INTELLIGENCE),
    BLESSING("Blessing", CharacterAttribute.AWARENESS),
    COMMUNE("Commune", CharacterAttribute.AWARENESS);

    private final String label;
    private final CharacterAttribute attribute;

    private SignatureSkill(String label, CharacterAttribute attribute) {
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
