package es.thalesalv.chatrpg.application.service.usecases;

import es.thalesalv.chatrpg.application.service.completion.CompletionService;
import es.thalesalv.chatrpg.application.service.moderation.ModerationService;
import es.thalesalv.chatrpg.application.util.StringProcessors;
import es.thalesalv.chatrpg.application.util.TokenCountingStringPredicate;
import es.thalesalv.chatrpg.domain.exception.DiscordFunctionException;
import es.thalesalv.chatrpg.domain.model.EventData;
import es.thalesalv.chatrpg.domain.model.chconf.Bump;
import es.thalesalv.chatrpg.domain.model.chconf.ModelSettings;
import es.thalesalv.chatrpg.domain.model.chconf.Nudge;
import es.thalesalv.chatrpg.domain.model.chconf.Persona;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AuthorUseCase implements BotUseCase {

    private final ModerationService moderationService;

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorUseCase.class);

    @Override
    public EventData generateResponse(final EventData eventData, final CompletionService model) {

        LOGGER.debug("Entered generation for normal text.");
        eventData.getCurrentChannel()
                .sendTyping()
                .complete();
        final Message message = eventData.getMessage();
        final SelfUser bot = eventData.getBot();
        if (message.getContentRaw()
                .trim()
                .equals(bot.getAsMention()
                        .trim())) {
            message.delete()
                    .submit()
                    .whenComplete((d, e) -> {
                        if (e != null) {
                            LOGGER.error("Error deleting trigger mention in Author", e);
                            throw new DiscordFunctionException("Error deleting trigger mention in Author", e);
                        }
                    });
        }

        final List<String> messages = handleHistory(eventData);

        moderationService.moderate(messages, eventData)
                .subscribe(inputModeration -> model.generate(messages, eventData)
                        .subscribe(textResponse -> moderationService.moderateOutput(textResponse, eventData)
                                .subscribe(outputModeration -> {
                                    final Message responseMessage = eventData.getCurrentChannel()
                                            .sendMessage(textResponse)
                                            .complete();
                                    eventData.setResponseMessage(responseMessage);
                                })));

        return eventData;
    }

    /**
     * Formats last messages history including reply reference to give the AI
     * context on the past conversation
     *
     * @param eventData Object containing the event's important data to be processed
     * @return The processed list of messages
     */
    private List<String> handleHistory(final EventData eventData) {

        final Predicate<Message> includeFilter = new IncludeFilter(eventData);
        final Predicate<String> tokenFilter = tokenPredicate(eventData);
        final Function<Message, String> messageMapper = messageMapper(eventData);
        List<String> messages = getHistory(eventData).stream()
                .filter(includeFilter)
                .map(messageMapper)
                .takeWhile(tokenFilter)
                .collect(Collectors.toList());
        Collections.reverse(messages);
        return messages;
    }

    private Function<Message, String> messageMapper(EventData eventData) {

        SelfUser bot = eventData.getBot();
        return m -> m.getAuthor()
                .getId()
                .equals(bot.getId())
                        ? StringProcessors.chatFormatter()
                                .apply(m)
                        : StringProcessors.chatDirectiveFormatter()
                                .apply(m);
    }

    private List<Message> getHistory(final EventData eventData) {

        final MessageChannelUnion channel = eventData.getCurrentChannel();
        final int historySize = eventData.getChannelDefinitions()
                .getChannelConfig()
                .getModelSettings()
                .getChatHistoryMemory();
        return channel.getHistory()
                .retrievePast(historySize)
                .complete();
    }

    private Predicate<String> tokenPredicate(final EventData eventData) {

        ModelSettings modelSettings = eventData.getChannelDefinitions()
                .getChannelConfig()
                .getModelSettings();
        Persona persona = eventData.getChannelDefinitions()
                .getChannelConfig()
                .getPersona();
        String personality = StringProcessors.replacePlaceholderWithPersona(persona)
                .apply(persona.getPersonality());
        Nudge nudge = persona.getNudge();
        Bump bump = persona.getBump();

        int tokens = modelSettings.getModelName()
                .getTokenCap();
        TokenCountingStringPredicate filter = new TokenCountingStringPredicate(tokens);
        filter.reserve(modelSettings.getMaxTokens());
        filter.reserve(personality);
        filter.reserve(nudge.content);
        for (int n = 1; n < Math.floorDiv(modelSettings.getChatHistoryMemory(), bump.frequency); n++) {
            filter.reserve(bump.content);
        }
        return filter;
    }

    private static class IncludeFilter implements Predicate<Message> {

        private final String botId;
        private boolean oneUserMessage = false;

        IncludeFilter(EventData eventData) {

            this.botId = eventData.getBot()
                    .getId();
        }

        @Override
        public boolean test(Message message) {

            if (message.getContentRaw()
                    .trim()
                    .equals(botId)) {
                return false;
            }
            if (!message.getAuthor()
                    .getId()
                    .equals(botId)) {
                if (oneUserMessage) {
                    return false;
                } else {
                    oneUserMessage = true;
                }
            }
            return true;
        }
    }
}
