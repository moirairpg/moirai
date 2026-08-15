package me.moirai.storyengine.infrastructure.inbound.rest.request;

import jakarta.validation.constraints.NotEmpty;
import me.moirai.storyengine.infrastructure.inbound.rest.validation.Moderated;

public record UpdatePlayerCharacterRequest(
        @NotEmpty(message = "cannot be null") @Moderated String name,
        @NotEmpty(message = "cannot be null") @Moderated String personality,
        @NotEmpty(message = "cannot be null") @Moderated String physicalDescription,
        Double uiImagePositionX,
        Double uiImagePositionY) {
}
