package me.moirai.storyengine.core.port.outbound.message;

import java.util.Optional;
import java.util.UUID;

import me.moirai.storyengine.core.port.inbound.message.MessageAuthorship;

public interface MessageAuthorizationReader {

    Optional<MessageAuthorship> getLastPlayerMessage(UUID adventurePublicId);
}
