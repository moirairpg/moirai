package es.thalesalv.chatrpg.application.service;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.adapters.rest.OpenAIApiService;
import es.thalesalv.chatrpg.application.config.CommandEventData;
import es.thalesalv.chatrpg.application.config.MessageEventData;
import es.thalesalv.chatrpg.application.config.Persona;
import es.thalesalv.chatrpg.domain.exception.DiscordFunctionException;
import es.thalesalv.chatrpg.domain.exception.ModerationException;
import es.thalesalv.chatrpg.domain.model.openai.moderation.ModerationRequest;
import es.thalesalv.chatrpg.domain.model.openai.moderation.ModerationResponse;
import es.thalesalv.chatrpg.domain.model.openai.moderation.ModerationResult;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ModerationService {

    private final OpenAIApiService openAIApiService;

    @Value("${config.bot.generation.default-threshold}")
    private double defaultThreshold;

    private static final Logger LOGGER = LoggerFactory.getLogger(ModerationService.class);
    private static final String FLAGGED_MESSAGE = "The message you sent has content that was flagged by OpenAI's moderation. Your message has been deleted from the conversation channel.";
    private static final String FLAGGED_ENTRY = "Your lorebook entry has content that was flagged by OpenAI's moderation. It cannot be saved until it's edited to conform to OpenAI's safety standards.";
    private static final String FLAGGED_TOPICS_MESSAGE = "\n**Message content:** {0}\n**Flagged topics:** {1}";
    private static final String FLAGGED_TOPICS_LOREBOOK = "\n**Flagged topics:** {0}\n```json\n{1}```";

    public Mono<ModerationResponse> moderate(final String prompt, final CommandEventData commandEventData, final ModalInteractionEvent event) {

        final ModerationRequest request = ModerationRequest.builder().input(prompt).build();
        return openAIApiService.callModerationApi(request)
                .doOnNext(response -> {
                    final ModerationResult moderationResult = response.getModerationResult().get(0);
                    checkModerationThresholds(moderationResult, commandEventData.getPersona());
                })
                .doOnError(ModerationException.class::isInstance, ex -> {
                    final ModerationException e = (ModerationException) ex;
                    handleFlags(e.getFlaggedTopics(), event, prompt);
                });
    }

    public Mono<ModerationResponse> moderate(final String prompt, final MessageEventData messageEventData) {

        final ModerationRequest request = ModerationRequest.builder().input(prompt).build();
        return openAIApiService.callModerationApi(request)
                .doOnNext(response -> {
                    final ModerationResult moderationResult = response.getModerationResult().get(0);
                    checkModerationThresholds(moderationResult, messageEventData.getPersona());
                })
                .doOnError(ModerationException.class::isInstance, ex -> {
                    final ModerationException e = (ModerationException) ex;
                    handleFlags(e.getFlaggedTopics(), messageEventData);
                });
    }

    private void checkModerationThresholds(final ModerationResult moderationResult, final Persona persona) {

        if (Boolean.parseBoolean(persona.getModerationAbsolute()) && moderationResult.getFlagged().booleanValue())
            throw new ModerationException("Unsafe content detected");
        
        final List<String> flaggedTopics = moderationResult.getCategoryScores().entrySet().stream()
        		.filter(entry -> entry.getValue() > Optional.ofNullable(persona.getModerationRules().get(entry.getKey())).orElse(defaultThreshold))
        		.map(Map.Entry::getKey)
        		.collect(Collectors.toList());

        if (!flaggedTopics.isEmpty())
            throw new ModerationException("Unsafe content detected", flaggedTopics);
    }

    private void handleFlags(final List<String> flaggedTopics, final MessageEventData messageEventData) {

        LOGGER.warn("Unsafe content detected in a message. -> {}", flaggedTopics);
        final TextChannel channel = messageEventData.getChannel().asTextChannel();
        final Message message = channel.retrieveMessageById(messageEventData.getMessage().getId()).complete();
        final User messageAuthor = messageEventData.getMessageAuthor();
        final StringBuilder flaggedMessage = new StringBuilder().append(FLAGGED_MESSAGE);

        if (flaggedTopics != null) {
            final String flaggedTopicsString = flaggedTopics.stream().collect(Collectors.joining(", "));
            flaggedMessage.append(MessageFormat.format(FLAGGED_TOPICS_MESSAGE, message.getContentDisplay(), flaggedTopicsString));
        }

        message.delete().complete();
        messageAuthor.openPrivateChannel().submit()
                .thenCompose(c -> c.sendMessage(flaggedMessage.toString()).submit())
                .whenComplete((msg, error) -> {
                    if (error != null) {
                        LOGGER.error("Error sending PM to user with flagged messages", error);
                        throw new DiscordFunctionException("Error sending PM to user", error);
                    }
                });
    }

    private void handleFlags(final List<String> flaggedTopics, final ModalInteractionEvent event, final String content) {

        LOGGER.warn("Unsafe content detected in a lorebook entry. -> {}", flaggedTopics);
        String flaggedMessage = FLAGGED_ENTRY;

        if (flaggedTopics != null) {
            final String flaggedTopicsString = flaggedTopics.stream().collect(Collectors.joining(", "));
            flaggedMessage += MessageFormat.format(FLAGGED_TOPICS_LOREBOOK, flaggedTopicsString, content);
        }

        event.reply(flaggedMessage).setEphemeral(true).complete();
    }
}
