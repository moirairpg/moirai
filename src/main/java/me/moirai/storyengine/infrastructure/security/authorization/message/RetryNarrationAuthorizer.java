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
import me.moirai.storyengine.core.port.outbound.adventure.AdventureReader;
import me.moirai.storyengine.core.port.outbound.message.MessageAuthorizationReader;

@Component
public class RetryNarrationAuthorizer implements OperationAuthorizer {

    private final AdventureAuthorizationReader adventureAuthorizationReader;
    private final AdventureReader adventureReader;
    private final MessageAuthorizationReader messageAuthorizationReader;

    public RetryNarrationAuthorizer(
            AdventureAuthorizationReader adventureAuthorizationReader,
            AdventureReader adventureReader,
            MessageAuthorizationReader messageAuthorizationReader) {

        this.adventureAuthorizationReader = adventureAuthorizationReader;
        this.adventureReader = adventureReader;
        this.messageAuthorizationReader = messageAuthorizationReader;
    }

    @Override
    public AuthorizationOperation getOperation() {
        return AuthorizationOperation.RETRY_NARRATION;
    }

    @Override
    public boolean authorize(AuthorizationContext context) {

        var adventureId = context.getFieldAsUuid("adventureId");
        var principal = context.getPrincipal();

        if (!adventureReader.getEnrolledPlayerIds(adventureId).contains(principal.publicId())) {
            return false;
        }

        var authorizationData = adventureAuthorizationReader.getAuthorizationData(adventureId)
                .orElseThrow(() -> new NotFoundException("Adventure not found"));

        if (canManage(authorizationData, principal)) {
            return true;
        }

        return messageAuthorizationReader.getLastPlayerMessage(adventureId)
                .map(lastPlayerMessage -> principal.username().equals(lastPlayerMessage.authorUsername()))
                .orElse(false);
    }

    private boolean canManage(AssetPermissionsData authorizationData, MoiraiPrincipal principal) {
        return authorizationData.ownerId().equals(principal.publicId())
                || authorizationData.writers().contains(principal.publicId())
                || principal.role() == ADMIN;
    }
}
