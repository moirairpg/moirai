package es.thalesalv.chatrpg.application.service.completion;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
import es.thalesalv.chatrpg.domain.model.openai.completion.TextCompletionRequest;
import es.thalesalv.chatrpg.domain.model.openai.dto.EventData;
import es.thalesalv.chatrpg.domain.model.openai.dto.Persona;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.User;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TextCompletionService implements CompletionService {

    private final MessageFormatHelper messageFormatHelper;
    private final CommonErrorHandler commonErrorHandler;
    private final TextCompletionRequestTranslator textCompletionRequestTranslator;
    private final OpenAIApiService openAiService;
    private final StringProcessor outputProcessor;

    private static final Logger LOGGER = LoggerFactory.getLogger(TextCompletionService.class);

    @Override
    public Mono<String> generate(List<String> messages, EventData eventData) {

        LOGGER.debug("Called inference for Text Completions.");
        final Mentions mentions = eventData.getMessage().getMentions();
        final User author = eventData.getMessageAuthor();
        final Set<LorebookEntryEntity> entriesFound = new HashSet<>();
        final List<String> processedMessages = new ArrayList<>(messages);
        final Persona persona = eventData.getChannelConfig().getPersona();
        outputProcessor.addRule(s -> Pattern.compile("\\bAs " + persona.getName() + ", (\\w)").matcher(s).replaceAll(r -> r.group(1).toUpperCase()));
        outputProcessor.addRule(s -> Pattern.compile("\\bas " + persona.getName() + ", (\\w)").matcher(s).replaceAll(r -> r.group(1)));

        messageFormatHelper.handleEntriesMentioned(processedMessages, entriesFound);
        if (persona.getIntent().equals("dungeonMaster")) {
            messageFormatHelper.handlePlayerCharacterEntries(entriesFound, processedMessages, author, mentions);
            messageFormatHelper.processEntriesFoundForRpg(entriesFound, processedMessages, author.getJDA());
        } else {
            messageFormatHelper.processEntriesFoundForChat(entriesFound, processedMessages);
        }

        final List<String> chatMessages = messageFormatHelper.formatMessages(processedMessages, eventData);
        final String chatifiedMessage = messageFormatHelper.chatifyMessages(chatMessages, eventData);
        final TextCompletionRequest request = textCompletionRequestTranslator.buildRequest(chatifiedMessage, eventData.getChannelConfig());
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
}
