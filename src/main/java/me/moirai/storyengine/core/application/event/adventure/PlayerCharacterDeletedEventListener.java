package me.moirai.storyengine.core.application.event.adventure;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import me.moirai.storyengine.core.domain.character.PlayerCharacterDeletedEvent;
import me.moirai.storyengine.core.port.outbound.adventure.AdventureRepository;

@Component
public class PlayerCharacterDeletedEventListener {

    private final AdventureRepository adventureRepository;
    private final ApplicationEventPublisher eventPublisher;

    public PlayerCharacterDeletedEventListener(
            AdventureRepository adventureRepository,
            ApplicationEventPublisher eventPublisher) {

        this.adventureRepository = adventureRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @EventListener
    public void onPlayerCharacterDeleted(PlayerCharacterDeletedEvent event) {

        adventureRepository.findAllContainingCharacter(event.getPlayerCharacterId())
                .forEach(adventure -> {
                    adventure.withdrawDeletedCharacter(event.getPlayerCharacterId());
                    adventureRepository.save(adventure);
                    adventure.drainEvents().forEach(eventPublisher::publishEvent);
                });
    }
}
