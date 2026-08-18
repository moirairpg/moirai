package me.moirai.storyengine.common.dto;

public record LevelUpSummary(
        String characterName,
        int newLevel,
        int attributePoints,
        int skillPoints) {
}
