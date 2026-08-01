package me.moirai.storyengine.core.application.event.character;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import me.moirai.storyengine.core.domain.userdetails.UserDeletedEvent;
import me.moirai.storyengine.core.port.outbound.character.PlayerCharacterRepository;

@Component
public class CharacterDomainEventListener {

    private final PlayerCharacterRepository playerCharacterRepository;
    private final ApplicationEventPublisher eventPublisher;

    public CharacterDomainEventListener(
            PlayerCharacterRepository playerCharacterRepository,
            ApplicationEventPublisher eventPublisher) {

        this.playerCharacterRepository = playerCharacterRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @EventListener
    public void onUserDeleted(UserDeletedEvent event) {

        playerCharacterRepository.findAllByPlayerId(event.getUserId())
                .forEach(character -> {
                    character.communicateCharacterDeleted();
                    character.drainEvents().forEach(eventPublisher::publishEvent);

                    playerCharacterRepository.deleteByPublicId(character.getPublicId());
                });
    }
}
