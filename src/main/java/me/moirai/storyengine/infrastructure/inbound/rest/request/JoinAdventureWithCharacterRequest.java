package me.moirai.storyengine.infrastructure.inbound.rest.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record JoinAdventureWithCharacterRequest(
        @NotNull(message = "cannot be null") UUID playerCharacterId) {
}
