package me.moirai.storyengine.core.port.inbound.message;

import java.util.UUID;

import me.moirai.storyengine.common.cqs.command.Command;

public record EditMessageAndGenerateOutput(UUID adventureId, UUID messageId, String content) implements Command<Void> {
}
