package me.moirai.storyengine.infrastructure.event.message;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import me.moirai.storyengine.core.application.event.message.MessageTranscriptChangedEvent;
import me.moirai.storyengine.core.port.outbound.message.MessageBroadcastPort;

@Component
public class MessageEventListener {

    private final MessageBroadcastPort messageBroadcastPort;

    public MessageEventListener(MessageBroadcastPort messageBroadcastPort) {
        this.messageBroadcastPort = messageBroadcastPort;
    }

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onTranscriptChanged(MessageTranscriptChangedEvent event) {

        messageBroadcastPort.broadcast(event.adventurePublicId(), event.update());
    }
}
