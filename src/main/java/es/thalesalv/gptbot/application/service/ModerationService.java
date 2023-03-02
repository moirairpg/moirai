package es.thalesalv.gptbot.application.service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import es.thalesalv.gptbot.adapters.data.ContextDatastore;
import es.thalesalv.gptbot.adapters.rest.OpenAIApiService;
import es.thalesalv.gptbot.application.config.MessageEventData;
import es.thalesalv.gptbot.application.config.Persona;
import es.thalesalv.gptbot.domain.exception.ModerationException;
import es.thalesalv.gptbot.domain.model.openai.moderation.ModerationRequest;
import es.thalesalv.gptbot.domain.model.openai.moderation.ModerationResponse;
import es.thalesalv.gptbot.domain.model.openai.moderation.ModerationResult;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ModerationService {

    private final JDA jda;
    private final ContextDatastore contextDatastore;
    private final OpenAIApiService openAIApiService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ModerationService.class);
    private static final String FLAGGED_MESSAGE = "The message you sent has content that was flagged by OpenAI''s moderation. Your message has been deleted from the conversation channel.";
    private static final String FLAGGED_TOPICS = "\n**Message content:** {0}\n**Flagged topics:** {1}";

    public Mono<ModerationResponse> moderate(String prompt) {

        final MessageEventData messageEventData = contextDatastore.getMessageEventData();
        final Persona persona = contextDatastore.getPersona();
        final ModerationRequest request = ModerationRequest.builder().input(prompt).build();
        return openAIApiService.callModerationApi(request)
                .doOnNext(response -> {
                    final ModerationResult moderationResult = response.getModerationResult().get(0);
                    checkModerationThresholds(moderationResult, persona);
                })
                .doOnError(ModerationException.class::isInstance, ex -> {
                    final ModerationException e = (ModerationException) ex;
                    handleFlags(e.getFlaggedTopics(), messageEventData);
                });
    }

    private void checkModerationThresholds(final ModerationResult moderationResult, Persona persona) {

        if (Boolean.parseBoolean(persona.getModerationAbsolute()) && moderationResult.isFlagged())
            throw new ModerationException("Unsafe content detected");

        final List<String> flaggedTopics = new ArrayList<>();
        final Map<String, Double> scores = moderationResult.getCategoryScores();
        scores.forEach((scoreTopic, scoreValue) -> persona.getModerationRules().entrySet().stream()
                .filter(moderationEntry -> moderationEntry.getKey().equals(scoreTopic))
                .filter(moderationEntry -> scoreValue.doubleValue() > moderationEntry.getValue().doubleValue())
                .forEach(moderationEntry -> flaggedTopics.add(moderationEntry.getKey())));

        if (!flaggedTopics.isEmpty())
            throw new ModerationException("Unsafe content detected", flaggedTopics);
    }

    private void handleFlags(final List<String> flaggedTopics, final MessageEventData messageEventData) {

        LOGGER.warn("Unsafe content detected.");
        final TextChannel channel = jda.getTextChannelById(messageEventData.getChannelId());
        final Message message = channel.retrieveMessageById(messageEventData.getMessageId()).complete();
        final User messageAuthor = jda.getUserById(messageEventData.getMessageAuthorId());
        String flaggedMessage = FLAGGED_MESSAGE;

        if (flaggedTopics != null) {
            final String flaggedTopicsString = flaggedTopics.stream().collect(Collectors.joining(", "));
            flaggedMessage += MessageFormat.format(FLAGGED_TOPICS, message.getContentDisplay(), flaggedTopicsString);
        }

        messageAuthor.openPrivateChannel().complete()
                .sendMessage(flaggedMessage).complete();

        message.delete().complete();
    }
}
