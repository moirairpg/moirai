package me.moirai.storyengine.infrastructure.security.authorization.message;

import static me.moirai.storyengine.common.enums.Role.ADMIN;

import org.springframework.stereotype.Component;

import me.moirai.storyengine.common.exception.NotFoundException;
import me.moirai.storyengine.common.security.authentication.MoiraiPrincipal;
import me.moirai.storyengine.common.security.authorization.AuthorizationContext;
import me.moirai.storyengine.common.security.authorization.AuthorizationOperation;
import me.moirai.storyengine.common.security.authorization.OperationAuthorizer;
import me.moirai.storyengine.core.port.inbound.AssetPermissionsData;
import me.moirai.storyengine.core.port.outbound.adventure.AdventureAuthorizationReader;
import me.moirai.storyengine.core.port.outbound.message.MessageAuthorizationReader;

@Component
public class EditMessageAuthorizer implements OperationAuthorizer {

    private final AdventureAuthorizationReader adventureAuthorizationReader;
    private final MessageAuthorizationReader messageAuthorizationReader;

    public EditMessageAuthorizer(
            AdventureAuthorizationReader adventureAuthorizationReader,
            MessageAuthorizationReader messageAuthorizationReader) {

        this.adventureAuthorizationReader = adventureAuthorizationReader;
        this.messageAuthorizationReader = messageAuthorizationReader;
    }

    @Override
    public AuthorizationOperation getOperation() {
        return AuthorizationOperation.EDIT_MESSAGE;
    }

    @Override
    public boolean authorize(AuthorizationContext context) {

        var adventureId = context.getFieldAsUuid("adventureId");
        var messageId = context.getFieldAsUuid("messageId");
        var principal = context.getPrincipal();

        var authorizationData = adventureAuthorizationReader.getAuthorizationData(adventureId)
                .orElseThrow(() -> new NotFoundException("Adventure not found"));

        if (canManage(authorizationData, principal)) {
            return true;
        }

        return messageAuthorizationReader.getLastPlayerMessage(adventureId)
                .filter(lastPlayerMessage -> lastPlayerMessage.messageId().equals(messageId))
                .map(lastPlayerMessage -> principal.username().equals(lastPlayerMessage.authorUsername()))
                .orElse(false);
    }

    private boolean canManage(AssetPermissionsData authorizationData, MoiraiPrincipal principal) {
        return authorizationData.ownerId().equals(principal.publicId())
                || authorizationData.writers().contains(principal.publicId())
                || principal.role() == ADMIN;
    }
}
