package me.moirai.storyengine.common.enums;

import java.util.List;

public enum CharacterClass {

    BARD(
            "Bard",
            SignatureSkill.INSPIRE,
            List.of(
                    CharacterSkill.PERFORMANCE,
                    CharacterSkill.PERSUASION,
                    CharacterSkill.ILLUSION,
                    CharacterSkill.LORE)),
    RANGER(
            "Ranger",
            SignatureSkill.DEADEYE,
            List.of(
                    CharacterSkill.SURVIVAL,
                    CharacterSkill.PERCEPTION,
                    CharacterSkill.MARKSMANSHIP,
                    CharacterSkill.ATHLETICS)),
    BARBARIAN(
            "Barbarian",
            SignatureSkill.BERSERK,
            List.of(
                    CharacterSkill.MELEE,
                    CharacterSkill.BRAWL,
                    CharacterSkill.ENDURANCE,
                    CharacterSkill.INTIMIDATION)),
    PALADIN(
            "Paladin",
            SignatureSkill.ZEAL,
            List.of(
                    CharacterSkill.PERSUASION,
                    CharacterSkill.ENDURANCE,
                    CharacterSkill.RESTORATION,
                    CharacterSkill.MELEE)),
    MAGE(
            "Mage",
            SignatureSkill.SPELLWEAVE,
            List.of(
                    CharacterSkill.DESTRUCTION,
                    CharacterSkill.CONJURATION,
                    CharacterSkill.ALTERATION,
                    CharacterSkill.LORE)),
    ROGUE(
            "Rogue",
            SignatureSkill.BACKSTAB,
            List.of(
                    CharacterSkill.STEALTH,
                    CharacterSkill.ACROBATICS,
                    CharacterSkill.DECEPTION,
                    CharacterSkill.PERCEPTION)),
    WITCH(
            "Witch",
            SignatureSkill.HEX,
            List.of(
                    CharacterSkill.ILLUSION,
                    CharacterSkill.ALCHEMY,
                    CharacterSkill.DECEPTION,
                    CharacterSkill.INTUITION)),
    CLERIC(
            "Cleric",
            SignatureSkill.BLESSING,
            List.of(
                    CharacterSkill.RESTORATION,
                    CharacterSkill.MELEE,
                    CharacterSkill.INTUITION,
                    CharacterSkill.LORE)),
    DRUID(
            "Druid",
            SignatureSkill.COMMUNE,
            List.of(
                    CharacterSkill.SURVIVAL,
                    CharacterSkill.INTUITION,
                    CharacterSkill.RESTORATION,
                    CharacterSkill.PERCEPTION));

    private final String label;
    private final SignatureSkill signature;
    private final List<CharacterSkill> favoredSkills;

    private CharacterClass(String label, SignatureSkill signature, List<CharacterSkill> favoredSkills) {
        this.label = label;
        this.signature = signature;
        this.favoredSkills = favoredSkills;
    }

    public String getLabel() {
        return label;
    }

    public SignatureSkill getSignature() {
        return signature;
    }

    public List<CharacterSkill> getFavoredSkills() {
        return favoredSkills;
    }
}
