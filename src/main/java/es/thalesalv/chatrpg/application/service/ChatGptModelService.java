package es.thalesalv.chatrpg.application.service;

import es.thalesalv.chatrpg.adapters.data.db.entity.LorebookEntry;
import es.thalesalv.chatrpg.adapters.rest.OpenAIApiService;
import es.thalesalv.chatrpg.application.config.Bump;
import es.thalesalv.chatrpg.application.config.MessageEventData;
import es.thalesalv.chatrpg.application.config.Nudge;
import es.thalesalv.chatrpg.application.config.Persona;
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
        final Persona persona = Objects.requireNonNull(eventData.getPersona());
        final Optional<Nudge> nudge = Optional.ofNullable(persona.getNudge()).filter(Nudge.isValid);
        final Optional<Bump> bump = Optional.ofNullable(persona.getBump()).filter(Bump.isValid);

        lorebookEntryExtractionHelper.handleEntriesMentioned(messages, entriesFound);
        if (eventData.getPersona().getIntent().equals("dungeonMaster")) {
            lorebookEntryExtractionHelper.handlePlayerCharacterEntries(entriesFound, messages, author, mentions);
            lorebookEntryExtractionHelper.processEntriesFoundForRpg(entriesFound, messages, author.getJDA());
        } else {
            lorebookEntryExtractionHelper.processEntriesFoundForChat(entriesFound, messages);
        }

        final List<ChatGptMessage> chatGptMessages = lorebookEntryExtractionHelper.formatMessagesForChatGpt(messages, eventData);
        nudge
                .ifPresent(ndge -> chatGptMessages.add(chatGptMessages.stream()
                                .filter(m -> "user".equals(m.getRole()))
                                .mapToInt(chatGptMessages::indexOf)
                                .reduce((a, b) -> b)
                                .orElse(0) + 1,
                        ChatGptMessage.builder()
                                .role(ndge.role)
                                .content(ndge.content)
                                .build()));
        bump
                .ifPresent(bmp -> {
                    ChatGptMessage bumpMessage = ChatGptMessage.builder()
                            .role(bmp.role)
                            .content(bmp.content)
                            .build();
                    for (int index = chatGptMessages.size()-1-bmp.frequency; index > 0; index = index - bmp.frequency) {
                        chatGptMessages.add(index, bumpMessage);
                    }
                });
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
