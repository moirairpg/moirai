package me.moirai.storyengine.core.application.command.world;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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
import me.moirai.storyengine.core.domain.world.WorldDeletedEvent;
import me.moirai.storyengine.core.domain.world.WorldFixture;
import me.moirai.storyengine.core.port.inbound.world.DeleteWorld;
import me.moirai.storyengine.core.port.outbound.world.WorldRepository;

@ExtendWith(MockitoExtension.class)
public class DeleteWorldHandlerTest {

    private static final String IMAGE_KEY = "worlds/test/image.png";

    @Mock
    private WorldRepository repository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private DeleteWorldHandler handler;

    @Test
    public void shouldThrowExceptionWhenIdIsNull() {

        // given
        var command = new DeleteWorld(null);

        // then
        assertThrows(IllegalArgumentException.class, () -> handler.handle(command));
    }

    @Test
    public void shouldDeleteTheWorldWhenItExists() {

        // given
        var world = WorldFixture.publicWorldWithId();
        var command = new DeleteWorld(WorldFixture.PUBLIC_ID);

        when(repository.findByPublicId(any(UUID.class))).thenReturn(Optional.of(world));
        doNothing().when(repository).deleteByPublicId(any(UUID.class));

        // when
        handler.handle(command);

        // then
        verify(repository, times(1)).deleteByPublicId(WorldFixture.PUBLIC_ID);
    }

    @Test
    public void shouldThrowExceptionWhenWorldNotFound() {

        // given
        var command = new DeleteWorld(WorldFixture.PUBLIC_ID);

        when(repository.findByPublicId(any(UUID.class))).thenReturn(Optional.empty());

        // then
        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> handler.handle(command));

        verify(eventPublisher, never()).publishEvent(any(Object.class));
    }

    @Test
    public void shouldAnnounceTheDeletionWithTheImageKeyWhenTheWorldHasOne() {

        // given
        var world = WorldFixture.publicWorldWithId();
        ReflectionTestUtils.setField(world, "imageKey", IMAGE_KEY);

        var command = new DeleteWorld(WorldFixture.PUBLIC_ID);

        when(repository.findByPublicId(any(UUID.class))).thenReturn(Optional.of(world));
        doNothing().when(repository).deleteByPublicId(any(UUID.class));

        // when
        handler.handle(command);

        // then
        var publishedEvent = ArgumentCaptor.forClass(WorldDeletedEvent.class);
        verify(eventPublisher).publishEvent(publishedEvent.capture());

        assertThat(publishedEvent.getValue().getImageKey()).isEqualTo(IMAGE_KEY);
        assertThat(publishedEvent.getValue().getPublicId()).isEqualTo(WorldFixture.PUBLIC_ID);
    }

    @Test
    public void shouldAnnounceTheDeletionWithoutAnImageKeyWhenTheWorldHasNone() {

        // given
        var world = WorldFixture.publicWorldWithId();
        var command = new DeleteWorld(WorldFixture.PUBLIC_ID);

        when(repository.findByPublicId(any(UUID.class))).thenReturn(Optional.of(world));
        doNothing().when(repository).deleteByPublicId(any(UUID.class));

        // when
        handler.handle(command);

        // then
        var publishedEvent = ArgumentCaptor.forClass(WorldDeletedEvent.class);
        verify(eventPublisher).publishEvent(publishedEvent.capture());

        assertThat(publishedEvent.getValue().getImageKey()).isNull();
    }
}
