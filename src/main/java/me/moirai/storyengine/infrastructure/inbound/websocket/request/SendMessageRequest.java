package me.moirai.storyengine.infrastructure.inbound.websocket.request;

public record SendMessageRequest(String content, Boolean generateNarration) {

    public SendMessageRequest {
        generateNarration = generateNarration == null || generateNarration;
    }
}
