package me.moirai.storyengine.core.port.outbound.adventure;

import java.util.UUID;

import me.moirai.storyengine.common.enums.CharacterClass;

public record AdventureMembershipSummaryRow(
        UUID playerCharacterId,
        UUID playerId,
        String playerUsername,
        String name,
        CharacterClass characterClass,
        String imageKey) {
}
