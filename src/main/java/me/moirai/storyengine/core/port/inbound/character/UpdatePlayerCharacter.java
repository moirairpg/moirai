package me.moirai.storyengine.core.port.inbound.character;

import java.util.UUID;

import me.moirai.storyengine.common.cqs.command.Command;

public record UpdatePlayerCharacter(
        UUID characterId,
        String name,
        String personality,
        String physicalDescription,
        String background,
        Double uiImagePositionX,
        Double uiImagePositionY,
        String requesterUsername)
        implements Command<PlayerCharacterDetails> {
}
