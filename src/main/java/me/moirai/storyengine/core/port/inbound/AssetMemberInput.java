package me.moirai.storyengine.core.port.inbound;

import me.moirai.storyengine.common.enums.PermissionLevel;

public record AssetMemberInput(String username, PermissionLevel level) {}
