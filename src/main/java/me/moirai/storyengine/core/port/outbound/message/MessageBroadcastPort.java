package me.moirai.storyengine.core.port.outbound.message;

import java.util.UUID;

public interface MessageBroadcastPort {

    void broadcast(UUID adventurePublicId, AdventureMessageUpdate update);
}
