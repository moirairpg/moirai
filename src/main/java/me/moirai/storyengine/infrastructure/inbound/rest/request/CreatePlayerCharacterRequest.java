package me.moirai.storyengine.infrastructure.inbound.rest.request;

import java.util.Map;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import me.moirai.storyengine.common.enums.CharacterAttribute;
import me.moirai.storyengine.common.enums.CharacterClass;
import me.moirai.storyengine.common.enums.CharacterSkill;
import me.moirai.storyengine.common.enums.SignatureSkill;
import me.moirai.storyengine.infrastructure.inbound.rest.validation.Moderated;

public record CreatePlayerCharacterRequest(
        @NotEmpty(message = "cannot be null") @Moderated String name,
        @NotNull(message = "cannot be null") CharacterClass characterClass,
        @NotEmpty(message = "cannot be null") @Moderated String personality,
        @NotEmpty(message = "cannot be null") @Moderated String physicalDescription,
        @NotNull(message = "cannot be null") Map<CharacterAttribute, Integer> attributes,
        @NotNull(message = "cannot be null") Map<CharacterSkill, Integer> skills,
        @NotNull(message = "cannot be null") Map<SignatureSkill, Integer> signatureSkill,
        Double uiImagePositionX,
        Double uiImagePositionY) {
}