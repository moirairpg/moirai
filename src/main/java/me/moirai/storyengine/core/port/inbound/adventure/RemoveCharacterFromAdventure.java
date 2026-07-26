package me.moirai.storyengine.core.port.inbound.adventure;

import java.util.UUID;

import me.moirai.storyengine.common.cqs.command.Command;

public record RemoveCharacterFromAdventure(
        UUID adventureId,
        UUID playerCharacterId,
        Long requesterId)
        implements Command<Void> {
}
