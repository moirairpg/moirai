package me.moirai.storyengine.core.port.outbound.message;

import java.util.UUID;

import me.moirai.storyengine.core.port.inbound.message.MessageResult;

public interface MessageBroadcastPort {

    void broadcast(UUID adventurePublicId, MessageResult message);
}
