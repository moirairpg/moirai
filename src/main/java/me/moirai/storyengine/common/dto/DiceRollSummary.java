package me.moirai.storyengine.common.dto;

import me.moirai.storyengine.common.enums.ActionDifficulty;
import me.moirai.storyengine.common.enums.ActionOutcome;

public record DiceRollSummary(
        String characterName,
        String attribute,
        int attributeLevel,
        String skill,
        int skillLevel,
        ActionDifficulty difficulty,
        int dc,
        int naturalRoll,
        int modifier,
        int total,
        ActionOutcome outcome) {
}
