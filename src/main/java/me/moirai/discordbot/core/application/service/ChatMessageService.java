package me.moirai.discordbot.core.application.service;

import java.util.Map;

public interface ChatMessageService {

    Map<String, Object> addMessagesToContext(Map<String, Object> context, int reservedTokens);

    Map<String, Object> addMessagesToContext(Map<String, Object> context, int reservedTokens, int amountOfMessage);

    Map<String, Object> addMessagesToContext(Map<String, Object> context, int reservedTokens, String assetManipulated);
}
