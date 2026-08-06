package me.moirai.storyengine.core.application.command.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import me.moirai.storyengine.core.application.event.message.StoryContinuedEvent;
import me.moirai.storyengine.core.port.inbound.message.Go;

@ExtendWith(MockitoExtension.class)
public class GoHandlerTest {

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private GoHandler handler;

    @Test
    public void shouldThrowExceptionWhenAdventureIdIsNull() {

        // given
        var command = new Go(null);

        // when / then
        assertThrows(IllegalArgumentException.class, () -> handler.handle(command));
    }

    @Test
    public void shouldRequestNarrationWhenTheStoryIsContinued() {

        // given
        var adventureId = UUID.randomUUID();
        var command = new Go(adventureId);

        // when
        handler.handle(command);

        // then
        var published = ArgumentCaptor.forClass(StoryContinuedEvent.class);
        verify(eventPublisher).publishEvent(published.capture());

        assertThat(published.getValue().adventurePublicId()).isEqualTo(adventureId);
    }
}
