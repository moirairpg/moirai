package me.moirai.storyengine.core.application.command.message;

import org.springframework.context.ApplicationEventPublisher;

import me.moirai.storyengine.common.annotation.Authorize;
import me.moirai.storyengine.common.annotation.CommandHandler;
import me.moirai.storyengine.common.cqs.command.AbstractCommandHandler;
import me.moirai.storyengine.common.security.authorization.AuthorizationOperation;
import me.moirai.storyengine.core.application.event.message.StoryContinuedEvent;
import me.moirai.storyengine.core.port.inbound.message.Go;

@CommandHandler
@Authorize(operation = AuthorizationOperation.PLAY_ADVENTURE, fields = "#request.adventureId")
public class GoHandler extends AbstractCommandHandler<Go, Void> {

    private final ApplicationEventPublisher eventPublisher;

    public GoHandler(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void validate(Go command) {

        if (command.adventureId() == null) {
            throw new IllegalArgumentException("Adventure ID cannot be null");
        }
    }

    @Override
    public Void execute(Go command) {

        eventPublisher.publishEvent(new StoryContinuedEvent(command.adventureId()));

        return null;
    }
}
