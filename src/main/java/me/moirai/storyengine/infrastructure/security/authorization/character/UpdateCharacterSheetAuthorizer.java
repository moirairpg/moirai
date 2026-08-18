package me.moirai.storyengine.infrastructure.security.authorization.character;

import org.springframework.stereotype.Component;

import me.moirai.storyengine.common.security.authorization.AuthorizationContext;
import me.moirai.storyengine.common.security.authorization.AuthorizationOperation;
import me.moirai.storyengine.common.security.authorization.OperationAuthorizer;
import me.moirai.storyengine.core.port.outbound.character.PlayerCharacterReader;

@Component
public class UpdateCharacterSheetAuthorizer implements OperationAuthorizer {

    private final PlayerCharacterReader reader;

    public UpdateCharacterSheetAuthorizer(PlayerCharacterReader reader) {
        this.reader = reader;
    }

    @Override
    public AuthorizationOperation getOperation() {
        return AuthorizationOperation.UPDATE_CHARACTER_SHEET;
    }

    @Override
    public boolean authorize(AuthorizationContext context) {

        var characterId = context.getFieldAsUuid("characterId");
        var principal = context.getPrincipal();

        return reader.getOwnerUsername(characterId)
                .map(ownerUsername -> ownerUsername.equals(principal.username()))
                .orElse(false);
    }
}
