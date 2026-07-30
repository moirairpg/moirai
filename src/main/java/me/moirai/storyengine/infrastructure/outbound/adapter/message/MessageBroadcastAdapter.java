package me.moirai.storyengine.infrastructure.outbound.adapter.message;

import java.util.UUID;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import me.moirai.storyengine.core.port.inbound.message.MessageResult;
import me.moirai.storyengine.core.port.outbound.message.MessageBroadcastPort;

@Component
public class MessageBroadcastAdapter implements MessageBroadcastPort {

    private static final String ADVENTURE_TOPIC = "/topic/adventures/";

    private final SimpMessagingTemplate messagingTemplate;

    public MessageBroadcastAdapter(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void broadcast(UUID adventurePublicId, MessageResult message) {

        messagingTemplate.convertAndSend(ADVENTURE_TOPIC + adventurePublicId, message);
    }
}
