package me.moirai.storyengine.core.port.outbound.generation;

import java.util.List;

public record ActionEvaluationRequest(String instructions, List<ChatMessage> messages) {

    public ActionEvaluationRequest {
        messages = List.copyOf(messages);
    }
}
