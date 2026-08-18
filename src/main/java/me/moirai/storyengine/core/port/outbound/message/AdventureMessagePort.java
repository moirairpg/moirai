package me.moirai.storyengine.core.port.outbound.message;

import java.util.UUID;

public interface AdventureMessagePort {

    void send(UUID adventurePublicId, AdventureMessageUpdate update);

    void sendToPlayer(String username, UUID adventurePublicId, AdventureMessageUpdate update);
}
