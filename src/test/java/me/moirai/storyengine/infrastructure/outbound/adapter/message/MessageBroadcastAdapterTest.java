package me.moirai.storyengine.infrastructure.outbound.adapter.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import me.moirai.storyengine.common.enums.MessageAuthorRole;
import me.moirai.storyengine.core.port.inbound.message.MessageResult;
import me.moirai.storyengine.core.port.outbound.message.AdventureMessageUpdate;

@ExtendWith(MockitoExtension.class)
public class MessageBroadcastAdapterTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private MessageBroadcastAdapter adapter;

    @Test
    void shouldSendToTheAdventureTopicWhenBroadcasting() {

        // given
        var adventureId = UUID.fromString("00000000-0000-0000-0000-0000000000aa");
        var update = AdventureMessageUpdate.messageAdded(new MessageResult(
                UUID.randomUUID(), "content", MessageAuthorRole.ASSISTANT, Instant.now()), false);

        // when
        adapter.broadcast(adventureId, update);

        // then
        verify(messagingTemplate).convertAndSend(
                "/topic/adventures/00000000-0000-0000-0000-0000000000aa", update);
    }

    @Test
    void shouldSendThePayloadUnchangedWhenBroadcasting() {

        // given
        var adventureId = UUID.randomUUID();
        var update = AdventureMessageUpdate.messageAdded(new MessageResult(
                UUID.randomUUID(), "content", MessageAuthorRole.USER, Instant.now()), true);

        // when
        adapter.broadcast(adventureId, update);

        // then
        var destinationCaptor = ArgumentCaptor.forClass(String.class);
        var payloadCaptor = ArgumentCaptor.forClass(Object.class);
        verify(messagingTemplate).convertAndSend(destinationCaptor.capture(), payloadCaptor.capture());

        assertThat(destinationCaptor.getValue()).isEqualTo("/topic/adventures/" + adventureId);
        assertThat(payloadCaptor.getValue()).isSameAs(update);
    }
}
