package me.moirai.storyengine.infrastructure.inbound.rest.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import me.moirai.storyengine.common.enums.Visibility;

public record UpdateAssetPermissionsRequest(
        @NotNull(message = "cannot be null") Visibility visibility,
        @NotNull(message = "cannot be null") List<@Valid AssetMemberRequest> members) {
}
