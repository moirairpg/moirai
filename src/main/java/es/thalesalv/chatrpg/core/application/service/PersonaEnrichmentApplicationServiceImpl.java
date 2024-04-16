package es.thalesalv.chatrpg.core.application.service;

import static org.apache.commons.lang3.StringUtils.LF;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import discord4j.discordjson.json.MessageData;
import es.thalesalv.chatrpg.core.domain.channelconfig.ModelConfiguration;
import es.thalesalv.chatrpg.core.domain.persona.Persona;
import es.thalesalv.chatrpg.core.domain.persona.PersonaDomainService;
import es.thalesalv.chatrpg.core.domain.port.TokenizerPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unchecked")
public class PersonaEnrichmentApplicationServiceImpl implements PersonaEnrichmentApplicationService {

    private static final String SAYS = "%s says: %s";
    private static final String PERSONA_DESCRIPTION = "[ DEBUG MODE ON: You are an actor interpreting the role of %s. %s's persona is as follows, and you are to maintain character during this conversation: %s ]";
    private static final String MESSAGE_HISTORY = "messageHistory";
    private static final String PERSONA = "persona";
    private static final String RETRIEVED_MESSAGES = "retrievedMessages";

    private final TokenizerPort tokenizerPort;
    private final PersonaDomainService personaDomainService;

    @Override
    public Mono<Map<String, Object>> enrich(String personaId, String botName, Map<String, Object> processedContext,
            ModelConfiguration modelConfiguration) {

        int totalTokens = modelConfiguration.getAiModel().getHardTokenLimit();
        int reservedTokensForPersona = (int) Math.floor(totalTokens * 0.20);

        return Mono.just(personaDomainService.getPersonaById(personaId))
                .map(persona -> addPersonaToContext(persona, processedContext, reservedTokensForPersona))
                .map(context -> addExtraMessagesToContext(context, reservedTokensForPersona));
    }

    private Map<String, Object> addPersonaToContext(Persona persona,
            Map<String, Object> processedContext, int reservedTokensForPersona) {

        String formattedPersona = String.format(PERSONA_DESCRIPTION, persona.getName(), persona.getName(),
                persona.getPersonality());

        int tokensInPersona = tokenizerPort.getTokenCountFrom(formattedPersona);

        if (tokensInPersona > reservedTokensForPersona) {
            throw new IllegalStateException("Persona is too big to fit in context");
        }

        processedContext.put(PERSONA, formattedPersona);

        return processedContext;
    }

    private Map<String, Object> addExtraMessagesToContext(Map<String, Object> contextWithPersona,
            int reservedTokensForStory) {

        String persona = (String) contextWithPersona.get(PERSONA);
        List<String> messageHistory = (List<String>) contextWithPersona.get(MESSAGE_HISTORY);
        List<MessageData> retrievedMessages = (List<MessageData>) contextWithPersona.get(RETRIEVED_MESSAGES);

        retrievedMessages.stream()
                .takeWhile(messageData -> {
                    String message = String.format(SAYS, messageData.author().username(),
                            messageData.content());

                    int tokensInPersona = tokenizerPort.getTokenCountFrom(persona);
                    int tokensInMessage = tokenizerPort.getTokenCountFrom(message);
                    int tokensInContext = tokenizerPort.getTokenCountFrom(stringifyList(messageHistory))
                            + tokensInPersona;

                    int tokensLeftInContext = reservedTokensForStory - tokensInContext;

                    return tokensInMessage <= tokensLeftInContext;
                })
                .map(messageData -> String.format(SAYS, messageData.author().username(),
                        messageData.content()))
                .forEach(messageHistory::addFirst);

        retrievedMessages.removeIf(message -> messageHistory.contains(formatMessage(message)));

        contextWithPersona.put(MESSAGE_HISTORY, messageHistory);
        contextWithPersona.put(RETRIEVED_MESSAGES, retrievedMessages);

        return contextWithPersona;
    }

    private String stringifyList(List<String> list) {

        return list.stream().collect(Collectors.joining(LF));
    }

    private String formatMessage(MessageData message) {
        return String.format(SAYS, message.author().username(), message.content());
    }
}
