package me.moirai.discordbot.core.application.usecase.completion.request;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

import java.util.ArrayList;
import java.util.List;

public class CompleteTextFixture {

    public static CompleteText.Message.Builder userMessage() {

        return CompleteText.Message.builder()
                .isAuthorBot(false)
                .messageContent("Message");
    }

    public static CompleteText.Builder withModerationDisabled() {

        List<CompleteText.Message> messages = new ArrayList<>();
        messages.add(userMessage().build());

        return CompleteText.builder()
                .aiModel("gpt4-omni")
                .authorDiscordId("123456")
                .frequencyPenalty(0D)
                .logitBias(emptyMap())
                .maxTokenLimit(250)
                .messages(messages)
                .moderationLevel("disabled")
                .personaId("1234")
                .presencePenalty(0D)
                .stopSequences(emptyList())
                .temperature(1D)
                .worldId("12345");
    }

    public static CompleteText.Builder withStrictModeration() {

        List<CompleteText.Message> messages = new ArrayList<>();
        messages.add(userMessage().build());

        return CompleteText.builder()
                .aiModel("gpt4-omni")
                .authorDiscordId("123456")
                .frequencyPenalty(0D)
                .logitBias(emptyMap())
                .maxTokenLimit(250)
                .messages(messages)
                .moderationLevel("strict")
                .personaId("1234")
                .presencePenalty(0D)
                .stopSequences(emptyList())
                .temperature(1D)
                .worldId("12345");
    }

    public static CompleteText.Builder withPermissiveModeration() {

        List<CompleteText.Message> messages = new ArrayList<>();
        messages.add(userMessage().build());

        return CompleteText.builder()
                .aiModel("gpt4-omni")
                .authorDiscordId("123456")
                .frequencyPenalty(0D)
                .logitBias(emptyMap())
                .maxTokenLimit(250)
                .messages(messages)
                .moderationLevel("permissive")
                .personaId("1234")
                .presencePenalty(0D)
                .stopSequences(emptyList())
                .temperature(1D)
                .worldId("12345");
    }
}
