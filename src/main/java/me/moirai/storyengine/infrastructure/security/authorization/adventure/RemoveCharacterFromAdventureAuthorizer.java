package me.moirai.storyengine.infrastructure.security.authorization.adventure;

import org.springframework.stereotype.Component;

import me.moirai.storyengine.common.exception.NotFoundException;
import me.moirai.storyengine.common.security.authentication.MoiraiPrincipal;
import me.moirai.storyengine.common.security.authorization.AuthorizationContext;
import me.moirai.storyengine.common.security.authorization.AuthorizationOperation;
import me.moirai.storyengine.common.security.authorization.OperationAuthorizer;
import me.moirai.storyengine.core.port.inbound.AssetPermissionsData;
import me.moirai.storyengine.core.port.outbound.adventure.AdventureAuthorizationReader;

@Component
public class RemoveCharacterFromAdventureAuthorizer implements OperationAuthorizer {

    private final AdventureAuthorizationReader reader;

    public RemoveCharacterFromAdventureAuthorizer(AdventureAuthorizationReader reader) {
        this.reader = reader;
    }

    @Override
    public AuthorizationOperation getOperation() {
        return AuthorizationOperation.REMOVE_CHARACTER_FROM_ADVENTURE;
    }

    @Override
    public boolean authorize(AuthorizationContext context) {

        var principal = context.getPrincipal();

        if (principal.isAdmin()) {
            return true;
        }

        var adventureId = context.getFieldAsUuid("adventureId");

        var authData = reader.getAuthorizationData(adventureId)
                .orElseThrow(() -> new NotFoundException("Adventure not found"));

        return canManage(authData, principal);
    }

    private boolean canManage(AssetPermissionsData authData, MoiraiPrincipal principal) {
        return authData.ownerId().equals(principal.publicId())
                || authData.writers().contains(principal.publicId());
    }
}
