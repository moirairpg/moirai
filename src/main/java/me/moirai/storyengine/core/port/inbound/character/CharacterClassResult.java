package me.moirai.storyengine.core.port.inbound.character;

import java.util.List;

import me.moirai.storyengine.common.util.Functions;

public record CharacterClassResult(
        String name,
        String label,
        CharacterSkillResult signatureSkill,
        List<String> favoredSkills) {

    public CharacterClassResult {
        favoredSkills = Functions.mapOrDefault(favoredSkills, List.of(), List::copyOf);
    }
}
