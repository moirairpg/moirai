package me.moirai.storyengine.common.dto;

import me.moirai.storyengine.common.enums.ActionDifficulty;
import me.moirai.storyengine.common.enums.ActionOutcome;

public record DiceRollSummary(
        String characterName,
        String attribute,
        String skill,
        ActionDifficulty difficulty,
        int dc,
        int naturalRoll,
        int modifier,
        int total,
        ActionOutcome outcome) {
}
