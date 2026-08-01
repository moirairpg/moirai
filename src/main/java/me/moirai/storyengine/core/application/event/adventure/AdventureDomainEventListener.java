package me.moirai.storyengine.core.application.event.adventure;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import me.moirai.storyengine.core.domain.adventure.Adventure;
import me.moirai.storyengine.core.domain.character.PlayerCharacterDeletedEvent;
import me.moirai.storyengine.core.domain.userdetails.UserDeletedEvent;
import me.moirai.storyengine.core.port.outbound.adventure.AdventureRepository;

@Component
public class AdventureDomainEventListener {

    private final AdventureRepository adventureRepository;
    private final ApplicationEventPublisher eventPublisher;

    public AdventureDomainEventListener(
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

    @Transactional(propagation = Propagation.MANDATORY)
    @EventListener
    public void onUserDeleted(UserDeletedEvent event) {

        var deletedIds = deleteOwnedAdventures(event.getUserId());

        adventureRepository.findAllInvolving(event.getUserId()).stream()
                .filter(adventure -> !deletedIds.contains(adventure.getId()))
                .forEach(adventure -> withdrawTracesOf(event.getUserId(), adventure));
    }

    private Set<Long> deleteOwnedAdventures(Long userId) {

        var owned = adventureRepository.findAllOwnedBy(userId);

        owned.forEach(adventure -> {
            adventure.communicateAdventureDeleted();
            adventure.drainEvents().forEach(eventPublisher::publishEvent);

            adventureRepository.deleteByPublicId(adventure.getPublicId());
        });

        return owned.stream()
                .map(Adventure::getId)
                .collect(Collectors.toSet());
    }

    private void withdrawTracesOf(Long userId, Adventure adventure) {

        adventure.withdrawInvitationsInvolving(userId);
        adventure.revoke(userId);

        adventureRepository.save(adventure);
    }
}
