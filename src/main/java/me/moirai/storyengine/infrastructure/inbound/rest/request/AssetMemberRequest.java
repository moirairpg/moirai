package me.moirai.storyengine.infrastructure.inbound.rest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import me.moirai.storyengine.common.enums.PermissionLevel;

public record AssetMemberRequest(
        @NotBlank(message = "cannot be empty") String username,
        @NotNull(message = "cannot be null") PermissionLevel level) {
}
