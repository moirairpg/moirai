package me.moirai.storyengine.core.port.outbound.adventure;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InvitationReader {

    Optional<PendingInvitationRow> getPendingByAdventureAndRecipient(
            UUID adventurePublicId,
            String recipientUsername);

    Optional<PendingInvitationRow> getPendingByPublicId(UUID invitationId);

    Optional<InvitationRecipientRow> getByPublicId(UUID invitationId);

    List<PendingInvitationRow> getAllPendingByRecipient(String recipientUsername);
}
