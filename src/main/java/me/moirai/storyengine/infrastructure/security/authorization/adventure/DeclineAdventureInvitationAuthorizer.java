package me.moirai.storyengine.infrastructure.security.authorization.adventure;

import org.springframework.stereotype.Component;

import me.moirai.storyengine.common.enums.InvitationStatus;
import me.moirai.storyengine.common.security.authorization.AuthorizationContext;
import me.moirai.storyengine.common.security.authorization.AuthorizationOperation;
import me.moirai.storyengine.common.security.authorization.OperationAuthorizer;
import me.moirai.storyengine.core.port.outbound.adventure.InvitationReader;

@Component
public class DeclineAdventureInvitationAuthorizer implements OperationAuthorizer {

    private final InvitationReader invitationReader;

    public DeclineAdventureInvitationAuthorizer(InvitationReader invitationReader) {
        this.invitationReader = invitationReader;
    }

    @Override
    public AuthorizationOperation getOperation() {
        return AuthorizationOperation.DECLINE_ADVENTURE_INVITATION;
    }

    @Override
    public boolean authorize(AuthorizationContext context) {

        var invitationId = context.getFieldAsUuid("invitationId");
        var principal = context.getPrincipal();

        return invitationReader.getByPublicId(invitationId)
                .map(invitation -> invitation.recipientUsername().equals(principal.username())
                        && invitation.status() == InvitationStatus.PENDING)
                .orElse(false);
    }
}
