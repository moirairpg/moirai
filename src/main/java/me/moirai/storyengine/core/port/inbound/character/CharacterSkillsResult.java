package me.moirai.storyengine.core.port.inbound.character;

import java.util.List;

import me.moirai.storyengine.common.util.Functions;

public record CharacterSkillsResult(
        List<CharacterSkillResult> skills,
        int maxLevel,
        SkillCreationRulesResult creation) {

    public CharacterSkillsResult {
        skills = Functions.mapOrDefault(skills, List.of(), List::copyOf);
    }
}
