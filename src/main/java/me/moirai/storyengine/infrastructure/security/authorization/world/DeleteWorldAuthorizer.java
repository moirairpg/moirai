package me.moirai.storyengine.infrastructure.security.authorization.world;

import org.springframework.stereotype.Component;

import me.moirai.storyengine.common.exception.NotFoundException;
import me.moirai.storyengine.common.security.authentication.MoiraiPrincipal;
import me.moirai.storyengine.common.security.authorization.AuthorizationContext;
import me.moirai.storyengine.common.security.authorization.AuthorizationOperation;
import me.moirai.storyengine.common.security.authorization.OperationAuthorizer;
import me.moirai.storyengine.core.port.inbound.AssetPermissionsData;
import me.moirai.storyengine.core.port.outbound.world.WorldAuthorizationReader;

@Component
public class DeleteWorldAuthorizer implements OperationAuthorizer {

    private final WorldAuthorizationReader reader;

    public DeleteWorldAuthorizer(WorldAuthorizationReader reader) {
        this.reader = reader;
    }

    @Override
    public AuthorizationOperation getOperation() {
        return AuthorizationOperation.DELETE_WORLD;
    }

    @Override
    public boolean authorize(AuthorizationContext context) {

        var worldId = context.getFieldAsUuid("worldId");
        var principal = context.getPrincipal();

        var authData = reader.getAuthorizationData(worldId)
                .orElseThrow(() -> new NotFoundException("World not found"));

        return isOwner(authData, principal);
    }

    private boolean isOwner(AssetPermissionsData authorizationData, MoiraiPrincipal principal) {
        return authorizationData.ownerId().equals(principal.publicId())
                || principal.isAdmin();
    }
}
