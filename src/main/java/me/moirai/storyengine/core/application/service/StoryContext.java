package me.moirai.storyengine.core.application.service;

import java.util.Collections;
import java.util.List;

import me.moirai.storyengine.core.domain.message.Message;
import me.moirai.storyengine.core.port.outbound.generation.ChatMessage;

public record StoryContext(
        List<ChatMessage> messages,
        List<String> playerCharacterNames,
        List<Message> activeHistory) {

    public StoryContext {
        messages = Collections.unmodifiableList(messages);
        playerCharacterNames = Collections.unmodifiableList(playerCharacterNames);
        activeHistory = Collections.unmodifiableList(activeHistory);
    }
}
