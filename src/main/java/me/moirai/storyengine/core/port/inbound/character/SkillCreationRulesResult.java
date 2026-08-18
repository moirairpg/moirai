package me.moirai.storyengine.core.port.inbound.character;

public record SkillCreationRulesResult(
        int points,
        int levelCap,
        int favoredCost,
        int offClassCost,
        int signatureStartingLevel) {
}
