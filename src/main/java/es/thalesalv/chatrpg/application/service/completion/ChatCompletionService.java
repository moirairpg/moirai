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
import es.thalesalv.chatrpg.application.translator.airequest.ChatCompletionRequestTranslator;
import es.thalesalv.chatrpg.application.util.StringProcessor;
import es.thalesalv.chatrpg.domain.exception.ModelResponseBlankException;
import es.thalesalv.chatrpg.domain.model.openai.completion.ChatCompletionRequest;
import es.thalesalv.chatrpg.domain.model.openai.completion.ChatMessage;
import es.thalesalv.chatrpg.domain.model.openai.dto.MessageEventData;
import es.thalesalv.chatrpg.domain.model.openai.dto.Persona;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.User;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ChatCompletionService implements CompletionService {

    private final MessageFormatHelper messageFormatHelper;
    private final CommonErrorHandler commonErrorHandler;
    private final ChatCompletionRequestTranslator chatCompletionsRequestTranslator;
    private final OpenAIApiService openAiService;
    private final StringProcessor outputProcessor;

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatCompletionService.class);

    @Override
    public Mono<String> generate(final List<String> messages, final MessageEventData eventData) {

        LOGGER.debug("Called inference for Chat Completions.");
        final Mentions mentions = eventData.getMessage().getMentions();
        final User author = eventData.getMessageAuthor();
        final List<String> processedMessages = new ArrayList<>(messages);
        final Set<LorebookEntryEntity> entriesFound = new HashSet<>();
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

        final List<ChatMessage> chatMessages = messageFormatHelper.formatMessagesForChatCompletions(processedMessages, eventData, eventData.getBot());
        final ChatCompletionRequest request = chatCompletionsRequestTranslator.buildRequest(chatMessages, eventData.getChannelConfig());
        return openAiService.callGptChatApi(request, eventData)
                .map(response -> {
                    final String responseText = response.getChoices().get(0).getMessage().getContent();
                    if (StringUtils.isBlank(responseText)) {
                        throw new ModelResponseBlankException();
                    }

                    return outputProcessor.process(responseText.trim());
                })
            .doOnError(ModelResponseBlankException.class::isInstance, e -> commonErrorHandler.handleEmptyResponse(eventData));
    }
}
