package me.moirai.storyengine.core.domain.adventure;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.moirai.storyengine.core.domain.character.PlayerCharacter;
import me.moirai.storyengine.core.domain.character.PlayerCharacterDeletedEvent;
import me.moirai.storyengine.core.domain.character.PlayerCharacterFixture;
import me.moirai.storyengine.core.port.outbound.adventure.AdventureRepository;

@ExtendWith(MockitoExtension.class)
public class AdventureDomainEventListenerTest {

    @Mock
    private AdventureRepository adventureRepository;

    @InjectMocks
    private AdventureDomainEventListener listener;

    @Test
    void shouldRemoveCharacterFromAllRostersWhenCharacterIsDeleted() {

        // given
        var event = deletedEventFor(PlayerCharacterFixture.samplePlayerCharacterWithId());

        // when
        listener.onPlayerCharacterDeleted(event);

        // then
        verify(adventureRepository).removeCharacterFromAllRosters(PlayerCharacterFixture.NUMERIC_ID);
    }

    private PlayerCharacterDeletedEvent deletedEventFor(PlayerCharacter character) {

        character.communicateCharacterDeleted();

        return (PlayerCharacterDeletedEvent) character.drainEvents().getFirst();
    }
}
