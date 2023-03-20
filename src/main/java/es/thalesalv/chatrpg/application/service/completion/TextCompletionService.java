package es.thalesalv.chatrpg.application.service.completion;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.adapters.data.db.entity.LorebookEntryEntity;
import es.thalesalv.chatrpg.adapters.rest.OpenAIApiService;
import es.thalesalv.chatrpg.application.errorhandling.CommonErrorHandler;
import es.thalesalv.chatrpg.application.helper.MessageFormatHelper;
import es.thalesalv.chatrpg.application.translator.airequest.TextCompletionRequestTranslator;
import es.thalesalv.chatrpg.application.util.StringProcessor;
import es.thalesalv.chatrpg.domain.exception.ModelResponseBlankException;
import es.thalesalv.chatrpg.domain.model.openai.dto.Bump;
import es.thalesalv.chatrpg.domain.model.openai.dto.MessageEventData;
import es.thalesalv.chatrpg.domain.model.openai.dto.Nudge;
import es.thalesalv.chatrpg.domain.model.openai.dto.Persona;
import es.thalesalv.chatrpg.domain.model.openai.gpt.Gpt3Request;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.User;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TextCompletionService implements CompletionService {

    private final MessageFormatHelper lorebookEntryExtractionHelper;
    private final CommonErrorHandler commonErrorHandler;
    private final TextCompletionRequestTranslator textCompletionRequestTranslator;
    private final OpenAIApiService openAiService;
    private final StringProcessor outputProcessor;

    private static final Logger LOGGER = LoggerFactory.getLogger(TextCompletionService.class);

    @Override
    public Mono<String> generate(List<String> messages, MessageEventData eventData) {

        LOGGER.debug("Called inference for Text Completions.");
        final Mentions mentions = eventData.getMessage().getMentions();
        final User author = eventData.getMessageAuthor();
        final Set<LorebookEntryEntity> entriesFound = new HashSet<>();
        final Persona persona = eventData.getChannelConfig().getPersona();
        outputProcessor.addRule(s -> Pattern.compile("\\bAs " + persona.getName() + ", (\\w)").matcher(s).replaceAll(r -> r.group(1).toUpperCase()));
        outputProcessor.addRule(s -> Pattern.compile("\\bas " + persona.getName() + ", (\\w)").matcher(s).replaceAll(r -> r.group(1)));

        lorebookEntryExtractionHelper.handleEntriesMentioned(messages, entriesFound);
        if (persona.getIntent().equals("dungeonMaster")) {
            lorebookEntryExtractionHelper.handlePlayerCharacterEntries(entriesFound, messages, author, mentions);
            lorebookEntryExtractionHelper.processEntriesFoundForRpg(entriesFound, messages, author.getJDA());
        } else {
            lorebookEntryExtractionHelper.processEntriesFoundForChat(entriesFound, messages);
        }

        Optional.ofNullable(persona.getNudge())
                .filter(Nudge.isValid)
                .ifPresent(ndge -> {
                    messages.add(messages.stream()
                        .filter(m -> !m.startsWith(persona.getName()))
                        .mapToInt(messages::indexOf)
                        .reduce((a, b) -> b)
                        .orElse(0) + 1,
                        ndge.content);
                });

        Optional.ofNullable(persona.getBump())
                .filter(Bump.isValid)
                .ifPresent(bmp -> {
                        for (int index = messages.size() - 1 - bmp.frequency;
                            index > 0; index = index - bmp.frequency) {

                        messages.add(index, bmp.getContent());
                    }
                });

        final String chatifiedMessage = chatifyMessages(messages, eventData);
        final Gpt3Request request = textCompletionRequestTranslator.buildRequest(chatifiedMessage, eventData.getChannelConfig());
        return openAiService.callGptApi(request, eventData)
                .map(response -> {
                    final String responseText = response.getChoices().get(0).getText();
                    if (StringUtils.isBlank(responseText)) {
                        throw new ModelResponseBlankException();
                    }

                    return responseText.trim();
                })
                .doOnError(ModelResponseBlankException.class::isInstance, e -> commonErrorHandler.handleEmptyResponse(eventData));
    }

    /**
     * Stringifies messages and turns them into a prompt format
     *
     * @param messages Messages in the chat room
     * @param eventData Object containing event data
     * @return Stringified messages for prompt
     */
    private static String chatifyMessages(final List<String> messages, final MessageEventData eventData) {

        LOGGER.debug("Entered chatbot conversation formatter");
        final Persona persona = eventData.getChannelConfig().getPersona();
        messages.replaceAll(m -> m.replace(eventData.getBot().getName(), persona.getName()).trim());
        return MessageFormat.format("{0}\n{1} said: ",
                String.join("\n", messages), persona.getName()).trim();
    }
}
