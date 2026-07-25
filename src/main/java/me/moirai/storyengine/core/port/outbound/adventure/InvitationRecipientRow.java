package me.moirai.storyengine.core.port.outbound.adventure;

import me.moirai.storyengine.common.enums.InvitationStatus;

public record InvitationRecipientRow(String recipientUsername, InvitationStatus status) {
}
