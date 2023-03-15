package es.thalesalv.chatrpg.application.service;

import es.thalesalv.chatrpg.adapters.data.db.entity.LorebookEntry;
import es.thalesalv.chatrpg.adapters.rest.OpenAIApiService;
import es.thalesalv.chatrpg.application.config.MessageEventData;
import es.thalesalv.chatrpg.application.errorhandling.CommonErrorHandler;
import es.thalesalv.chatrpg.application.service.helper.MessageFormatHelper;
import es.thalesalv.chatrpg.application.service.interfaces.GptModelService;
import es.thalesalv.chatrpg.application.translator.ChatGptRequestTranslator;
import es.thalesalv.chatrpg.domain.exception.ModelResponseBlankException;
import es.thalesalv.chatrpg.domain.model.openai.gpt.ChatGptMessage;
import es.thalesalv.chatrpg.domain.model.openai.gpt.ChatGptRequest;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatGptModelService implements GptModelService {

    private final MessageFormatHelper lorebookEntryExtractionHelper;
    private final CommonErrorHandler commonErrorHandler;
    private final ChatGptRequestTranslator chatGptRequestTranslator;
    private final OpenAIApiService openAiService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatGptModelService.class);

    @Override
    public Mono<String> generate(final String prompt, final List<String> messages, final MessageEventData eventData) {

        LOGGER.debug("Called inference for ChatGPT. Persona -> {}", eventData.getPersona());
        final Mentions mentions = eventData.getMessage().getMentions();
        final User author = eventData.getMessageAuthor();
        final Set<LorebookEntry> entriesFound = new HashSet<>();
        final Map<String, String> nudge = eventData.getPersona().getNudge();

        lorebookEntryExtractionHelper.handleEntriesMentioned(messages, entriesFound);
        if (eventData.getPersona().getIntent().equals("dungeonMaster")) {
            lorebookEntryExtractionHelper.handlePlayerCharacterEntries(entriesFound, messages, author, mentions);
            lorebookEntryExtractionHelper.processEntriesFoundForRpg(entriesFound, messages, author.getJDA());
        } else {
            lorebookEntryExtractionHelper.processEntriesFoundForChat(entriesFound, messages);
        }

        final List<ChatGptMessage> chatGptMessages = lorebookEntryExtractionHelper.formatMessagesForChatGpt(messages, eventData);
        Optional.ofNullable(nudge)
                .ifPresent(n -> chatGptMessages.add(chatGptMessages.stream()
                                .filter(m -> "user".equals(m.getRole()))
                                .mapToInt(chatGptMessages::indexOf)
                                .reduce((a, b) -> b)
                                .orElse(0) + 1,
                        ChatGptMessage.builder()
                                .role(n.get("role"))
                                .content(n.get("content"))
                                .build()));
        final ChatGptRequest request = chatGptRequestTranslator.buildRequest(eventData.getPersona(), chatGptMessages);
        return openAiService.callGptChatApi(request, eventData)
                .map(response -> {
                    final String responseText = response.getChoices().get(0).getMessage().getContent();
                    if (StringUtils.isBlank(responseText)) {
                        throw new ModelResponseBlankException();
                    }

                    return responseText.trim();
                })
            .doOnError(ModelResponseBlankException.class::isInstance, e -> commonErrorHandler.handleEmptyResponse(eventData));
    }
}
