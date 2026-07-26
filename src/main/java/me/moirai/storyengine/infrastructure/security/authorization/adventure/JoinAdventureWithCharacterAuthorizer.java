package me.moirai.storyengine.infrastructure.security.authorization.adventure;

import java.util.UUID;

import org.springframework.stereotype.Component;

import me.moirai.storyengine.common.enums.InvitationStatus;
import me.moirai.storyengine.common.security.authentication.MoiraiPrincipal;
import me.moirai.storyengine.common.security.authorization.AuthorizationContext;
import me.moirai.storyengine.common.security.authorization.AuthorizationOperation;
import me.moirai.storyengine.common.security.authorization.OperationAuthorizer;
import me.moirai.storyengine.core.port.outbound.adventure.InvitationReader;
import me.moirai.storyengine.core.port.outbound.character.PlayerCharacterReader;

@Component
public class JoinAdventureWithCharacterAuthorizer implements OperationAuthorizer {

    private final InvitationReader invitationReader;
    private final PlayerCharacterReader playerCharacterReader;

    public JoinAdventureWithCharacterAuthorizer(
            InvitationReader invitationReader,
            PlayerCharacterReader playerCharacterReader) {

        this.invitationReader = invitationReader;
        this.playerCharacterReader = playerCharacterReader;
    }

    @Override
    public AuthorizationOperation getOperation() {
        return AuthorizationOperation.JOIN_ADVENTURE_WITH_CHARACTER;
    }

    @Override
    public boolean authorize(AuthorizationContext context) {

        var invitationId = context.getFieldAsUuid("invitationId");
        var playerCharacterId = context.getFieldAsUuid("playerCharacterId");
        var principal = context.getPrincipal();

        return isPendingRecipient(invitationId, principal) && ownsCharacter(playerCharacterId, principal);
    }

    private boolean isPendingRecipient(UUID invitationId, MoiraiPrincipal principal) {

        return invitationReader.getByPublicId(invitationId)
                .map(invitation -> invitation.recipientUsername().equals(principal.username())
                        && invitation.status() == InvitationStatus.PENDING)
                .orElse(false);
    }

    private boolean ownsCharacter(UUID playerCharacterId, MoiraiPrincipal principal) {

        return playerCharacterReader.getVisibilityData(playerCharacterId)
                .map(data -> data.ownerUsername().equals(principal.username()))
                .orElse(false);
    }
}
