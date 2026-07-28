package me.moirai.storyengine.infrastructure.security.authorization.adventure;

import org.springframework.stereotype.Component;

import me.moirai.storyengine.common.security.authorization.AuthorizationContext;
import me.moirai.storyengine.common.security.authorization.AuthorizationOperation;
import me.moirai.storyengine.common.security.authorization.OperationAuthorizer;
import me.moirai.storyengine.core.port.outbound.adventure.AdventureReader;

@Component
public class PlayAdventureAuthorizer implements OperationAuthorizer {

    private final AdventureReader reader;

    public PlayAdventureAuthorizer(AdventureReader reader) {
        this.reader = reader;
    }

    @Override
    public AuthorizationOperation getOperation() {
        return AuthorizationOperation.PLAY_ADVENTURE;
    }

    @Override
    public boolean authorize(AuthorizationContext context) {

        var principal = context.getPrincipal();
        var adventureId = context.getFieldAsUuid("adventureId");

        return reader.getEnrolledPlayerIds(adventureId).contains(principal.publicId());
    }
}