package me.moirai.storyengine.core.domain.adventure;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import me.moirai.storyengine.core.domain.character.PlayerCharacterDeletedEvent;
import me.moirai.storyengine.core.port.outbound.adventure.AdventureRepository;

@Component
public class AdventureDomainEventListener {

    private final AdventureRepository adventureRepository;

    public AdventureDomainEventListener(AdventureRepository adventureRepository) {
        this.adventureRepository = adventureRepository;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onPlayerCharacterDeleted(PlayerCharacterDeletedEvent event) {

        adventureRepository.removeCharacterFromAllRosters(event.getPlayerCharacterId());
    }
}
