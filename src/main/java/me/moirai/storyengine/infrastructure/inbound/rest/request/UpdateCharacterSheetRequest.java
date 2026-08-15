package me.moirai.storyengine.infrastructure.inbound.rest.request;

import java.util.Map;

import jakarta.validation.constraints.NotNull;
import me.moirai.storyengine.common.enums.CharacterAttribute;
import me.moirai.storyengine.common.enums.CharacterClass;
import me.moirai.storyengine.common.enums.CharacterSkill;
import me.moirai.storyengine.common.enums.SignatureSkill;

public record UpdateCharacterSheetRequest(
        @NotNull(message = "cannot be null") CharacterClass characterClass,
        @NotNull(message = "cannot be null") Map<CharacterAttribute, Integer> attributes,
        @NotNull(message = "cannot be null") Map<CharacterSkill, Integer> skills,
        @NotNull(message = "cannot be null") Map<SignatureSkill, Integer> signatureSkill) {
}
