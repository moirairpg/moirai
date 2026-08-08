package me.moirai.storyengine.common.security.authorization;

import me.moirai.storyengine.common.security.authentication.MoiraiPrincipal;
import me.moirai.storyengine.core.port.inbound.AssetPermissionsData;

public interface OperationAuthorizer {

    AuthorizationOperation getOperation();

    boolean authorize(AuthorizationContext context);

    default boolean canManage(AssetPermissionsData authorizationData, MoiraiPrincipal principal) {

        return authorizationData.ownerId().equals(principal.publicId())
                || authorizationData.writers().contains(principal.publicId())
                || principal.isAdmin();
    }
}
