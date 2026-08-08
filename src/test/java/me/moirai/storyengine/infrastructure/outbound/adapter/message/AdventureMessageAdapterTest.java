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

import me.moirai.storyengine.common.dto.MessageSummary;
import me.moirai.storyengine.common.enums.MessageAuthorRole;
import me.moirai.storyengine.common.enums.MessageStatus;
import me.moirai.storyengine.core.port.outbound.message.AdventureMessageUpdate;

@ExtendWith(MockitoExtension.class)
public class AdventureMessageAdapterTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private AdventureMessageAdapter adapter;

    @Test
    void shouldSendToTheAdventureTopicWhenAnUpdateIsSent() {

        // given
        var adventureId = UUID.fromString("00000000-0000-0000-0000-0000000000aa");
        var update = AdventureMessageUpdate.messageAdded(new MessageSummary(
                UUID.randomUUID(), MessageAuthorRole.ASSISTANT, "content", MessageStatus.ACTIVE,
                null, "Narrator", Instant.now()), false);

        // when
        adapter.send(adventureId, update);

        // then
        verify(messagingTemplate).convertAndSend(
                "/topic/adventures/00000000-0000-0000-0000-0000000000aa", update);
    }

    @Test
    void shouldSendThePayloadUnchangedWhenAnUpdateIsSent() {

        // given
        var adventureId = UUID.randomUUID();
        var update = AdventureMessageUpdate.messageAdded(new MessageSummary(
                UUID.randomUUID(), MessageAuthorRole.USER, "content", MessageStatus.ACTIVE,
                UUID.randomUUID(), "Aria", Instant.now()), true);

        // when
        adapter.send(adventureId, update);

        // then
        var destinationCaptor = ArgumentCaptor.forClass(String.class);
        var payloadCaptor = ArgumentCaptor.forClass(Object.class);
        verify(messagingTemplate).convertAndSend(destinationCaptor.capture(), payloadCaptor.capture());

        assertThat(destinationCaptor.getValue()).isEqualTo("/topic/adventures/" + adventureId);
        assertThat(payloadCaptor.getValue()).isSameAs(update);
    }
}
