package me.moirai.storyengine.core.port.outbound.generation;

import me.moirai.storyengine.common.enums.ActionDifficulty;
import me.moirai.storyengine.common.enums.ActionVerdict;
import me.moirai.storyengine.common.enums.CharacterAttribute;

public record ActionEvaluationResult(
        ActionVerdict verdict,
        CharacterAttribute attribute,
        String skill,
        ActionDifficulty difficulty,
        String reason) {
}
