package me.moirai.storyengine.infrastructure.inbound.rest.request;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotEmpty;
import me.moirai.storyengine.infrastructure.inbound.rest.validation.Moderated;
import me.moirai.storyengine.infrastructure.inbound.rest.validation.ModeratedLorebook;

public record UpdateWorldRequest(
        @Moderated @NotEmpty(message = "cannot be empty") String name,
        @Moderated @NotEmpty(message = "cannot be empty") String description,
        @Moderated @NotEmpty(message = "cannot be empty") String adventureStart,
        @Moderated String narratorName,
        @Moderated String narratorPersonality,
        Double uiImagePositionX,
        Double uiImagePositionY,
        @ModeratedLorebook List<WorldLorebookEntryRequest> lorebookEntriesToAdd,
        @ModeratedLorebook List<UpdateWorldLorebookEntryRequest> lorebookEntriesToUpdate,
        List<UUID> lorebookEntriesToDelete) {
}
