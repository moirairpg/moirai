package me.moirai.storyengine.infrastructure.outbound.adapter.message;

import java.util.UUID;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import me.moirai.storyengine.core.port.outbound.message.AdventureMessagePort;
import me.moirai.storyengine.core.port.outbound.message.AdventureMessageUpdate;

@Component
public class AdventureMessageAdapter implements AdventureMessagePort {

    private static final String ADVENTURE_TOPIC = "/topic/adventures/";
    private static final String PLAYER_EVENT_QUEUE = "/queue/adventures/";

    private final SimpMessagingTemplate messagingTemplate;

    public AdventureMessageAdapter(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void send(UUID adventurePublicId, AdventureMessageUpdate update) {

        messagingTemplate.convertAndSend(ADVENTURE_TOPIC + adventurePublicId, update);
    }

    @Override
    public void sendToPlayer(String username, UUID adventurePublicId, AdventureMessageUpdate update) {

        messagingTemplate.convertAndSendToUser(username, PLAYER_EVENT_QUEUE + adventurePublicId + "/events", update);
    }
}
