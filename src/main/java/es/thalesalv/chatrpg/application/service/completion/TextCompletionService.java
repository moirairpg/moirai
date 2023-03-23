package es.thalesalv.chatrpg.application.service.completion;

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
import es.thalesalv.chatrpg.domain.model.openai.dto.MessageEventData;
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
    private final StringProcessor inputProcessor;

    private static final Logger LOGGER = LoggerFactory.getLogger(TextCompletionService.class);

    @Override
    public Mono<String> generate(List<String> messages, MessageEventData eventData) {

        LOGGER.debug("Called inference for Text Completions.");
        final Mentions mentions = eventData.getMessage().getMentions();
        final User author = eventData.getMessageAuthor();
        final Persona persona = eventData.getChannelConfig().getPersona();

        inputProcessor.addRule(s -> Pattern.compile(eventData.getBot().getName()).matcher(s).replaceAll(r -> persona.getName()));
        outputProcessor.addRule(s -> Pattern.compile("\\bAs " + persona.getName() + ", (\\w)").matcher(s).replaceAll(r -> r.group(1).toUpperCase()));
        outputProcessor.addRule(s -> Pattern.compile("\\bas " + persona.getName() + ", (\\w)").matcher(s).replaceAll(r -> r.group(1)));

        final Set<LorebookEntryEntity> entriesFound = messageFormatHelper.handleEntriesMentioned(messages);
        if (persona.getIntent().equals("dungeonMaster")) {
            messageFormatHelper.handlePlayerCharacterEntries(entriesFound, messages, author, mentions);
            messageFormatHelper.processEntriesFoundForRpg(entriesFound, messages, author.getJDA());
        } else {
            messageFormatHelper.processEntriesFoundForChat(entriesFound, messages);
        }

        final List<String> chatMessages = messageFormatHelper.formatMessages(messages, eventData);
        final String chatifiedMessage = messageFormatHelper.chatifyMessages(chatMessages, eventData, inputProcessor);
        final TextCompletionRequest request = textCompletionRequestTranslator.buildRequest(chatifiedMessage, eventData.getChannelConfig());
        return openAiService.callGptApi(request, eventData)
                .map(response -> {
                    final String responseText = response.getChoices().get(0).getText();
                    if (StringUtils.isBlank(responseText)) {
                        throw new ModelResponseBlankException();
                    }

                    return outputProcessor.process(responseText.trim());
                })
                .doOnError(ModelResponseBlankException.class::isInstance, e -> commonErrorHandler.handleEmptyResponse(eventData));
    }
}
