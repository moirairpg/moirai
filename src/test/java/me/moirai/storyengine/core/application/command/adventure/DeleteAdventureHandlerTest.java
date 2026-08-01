package me.moirai.storyengine.core.application.command.adventure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import me.moirai.storyengine.common.exception.NotFoundException;
import me.moirai.storyengine.core.domain.adventure.Adventure;
import me.moirai.storyengine.core.domain.adventure.AdventureDeletedEvent;
import me.moirai.storyengine.core.domain.adventure.AdventureFixture;
import me.moirai.storyengine.core.port.inbound.adventure.DeleteAdventure;
import me.moirai.storyengine.core.port.outbound.adventure.AdventureRepository;

@ExtendWith(MockitoExtension.class)
public class DeleteAdventureHandlerTest {

    @Mock
    private AdventureRepository repository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private DeleteAdventureHandler handler;

    private Adventure adventureWithId() {
        var adventure = AdventureFixture.privateAdventureWithoutNarrator().build();
        ReflectionTestUtils.setField(adventure, "id", AdventureFixture.NUMERIC_ID);
        ReflectionTestUtils.setField(adventure, "publicId", AdventureFixture.PUBLIC_ID);
        return adventure;
    }

    @Test
    public void errorWhenIdIsNull() {

        // given
        DeleteAdventure command = new DeleteAdventure(null);

        // then
        assertThrows(IllegalArgumentException.class, () -> handler.handle(command));
    }

    @Test
    public void deleteAdventure_whenAdventureNotFound_thenThrowException() {

        // given
        DeleteAdventure command = new DeleteAdventure(AdventureFixture.PUBLIC_ID);

        when(repository.findByPublicId(any(UUID.class))).thenReturn(Optional.empty());

        // then
        assertThrows(NotFoundException.class, () -> handler.handle(command));
    }

    @Test
    public void shouldDeleteTheAdventureWhenItIsFound() {

        // given
        var adventure = adventureWithId();
        var command = new DeleteAdventure(AdventureFixture.PUBLIC_ID);

        when(repository.findByPublicId(AdventureFixture.PUBLIC_ID)).thenReturn(Optional.of(adventure));

        // when
        handler.handle(command);

        // then
        verify(repository).deleteByPublicId(AdventureFixture.PUBLIC_ID);
    }

    @Test
    public void shouldPublishDeletionEventCarryingTheImageKeyWhenTheAdventureHasOne() {

        // given
        var adventure = adventureWithId();
        ReflectionTestUtils.setField(adventure, "imageKey", "adventures/test/image.png");
        var command = new DeleteAdventure(AdventureFixture.PUBLIC_ID);

        when(repository.findByPublicId(AdventureFixture.PUBLIC_ID)).thenReturn(Optional.of(adventure));

        // when
        handler.handle(command);

        // then
        var captor = ArgumentCaptor.forClass(AdventureDeletedEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());

        assertThat(captor.getValue().getAdventureId()).isEqualTo(AdventureFixture.NUMERIC_ID);
        assertThat(captor.getValue().getPublicId()).isEqualTo(AdventureFixture.PUBLIC_ID);
        assertThat(captor.getValue().getImageKey()).isEqualTo("adventures/test/image.png");
    }

    @Test
    public void shouldPublishDeletionEventWithoutAnImageKeyWhenTheAdventureHasNone() {

        // given
        var adventure = adventureWithId();
        var command = new DeleteAdventure(AdventureFixture.PUBLIC_ID);

        when(repository.findByPublicId(AdventureFixture.PUBLIC_ID)).thenReturn(Optional.of(adventure));

        // when
        handler.handle(command);

        // then
        var captor = ArgumentCaptor.forClass(AdventureDeletedEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());

        assertThat(captor.getValue().getImageKey()).isNull();
    }
}
