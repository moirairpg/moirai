package me.moirai.storyengine.infrastructure.security.authorization.character;

import java.util.UUID;

import org.springframework.stereotype.Component;

import me.moirai.storyengine.common.enums.Visibility;
import me.moirai.storyengine.common.security.authentication.MoiraiPrincipal;
import me.moirai.storyengine.common.security.authorization.AuthorizationContext;
import me.moirai.storyengine.common.security.authorization.AuthorizationOperation;
import me.moirai.storyengine.common.security.authorization.OperationAuthorizer;
import me.moirai.storyengine.core.port.inbound.AssetPermissionsData;
import me.moirai.storyengine.core.port.outbound.character.PlayerCharacterReader;
import me.moirai.storyengine.core.port.outbound.character.PlayerCharacterPermissionsData;

@Component
public class ViewPlayerCharacterAuthorizer implements OperationAuthorizer {

    private final PlayerCharacterReader reader;

    public ViewPlayerCharacterAuthorizer(PlayerCharacterReader reader) {
        this.reader = reader;
    }

    @Override
    public AuthorizationOperation getOperation() {
        return AuthorizationOperation.VIEW_PLAYER_CHARACTER;
    }

    @Override
    public boolean authorize(AuthorizationContext context) {

        var characterId = context.getFieldAsUuid("characterId");
        var principal = context.getPrincipal();

        if (principal.isAdmin()) {
            return true;
        }

        return reader.getPermissions(characterId)
                .map(data -> isOwnerOrEnrolledAdventureMember(data, principal))
                .orElse(false);
    }

    private boolean isOwnerOrEnrolledAdventureMember(
            PlayerCharacterPermissionsData data,
            MoiraiPrincipal principal) {

        if (data.ownerUsername().equals(principal.username())) {
            return true;
        }

        return data.enrolledAdventurePermissions().stream()
                .anyMatch(permissions -> admitsCaller(permissions, principal.publicId()));
    }

    private boolean admitsCaller(AssetPermissionsData permissions, UUID callerPublicId) {

        return permissions.visibility() == Visibility.PUBLIC
                || permissions.ownerId().equals(callerPublicId)
                || permissions.writers().contains(callerPublicId)
                || permissions.readers().contains(callerPublicId);
    }
}
