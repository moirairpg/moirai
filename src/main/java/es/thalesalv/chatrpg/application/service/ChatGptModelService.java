package es.thalesalv.chatrpg.application.service;

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
import es.thalesalv.chatrpg.application.translator.ChatGptRequestTranslator;
import es.thalesalv.chatrpg.domain.exception.ModelResponseBlankException;
import es.thalesalv.chatrpg.domain.model.openai.dto.Bump;
import es.thalesalv.chatrpg.domain.model.openai.dto.MessageEventData;
import es.thalesalv.chatrpg.domain.model.openai.dto.Nudge;
import es.thalesalv.chatrpg.domain.model.openai.dto.Persona;
import es.thalesalv.chatrpg.domain.model.openai.gpt.ChatGptMessage;
import es.thalesalv.chatrpg.domain.model.openai.gpt.ChatGptRequest;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.User;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ChatGptModelService implements GptModelService {

    private final MessageFormatHelper lorebookEntryExtractionHelper;
    private final CommonErrorHandler commonErrorHandler;
    private final ChatGptRequestTranslator chatGptRequestTranslator;
    private final OpenAIApiService openAiService;
    private final StringProcessor outputProcessor = new StringProcessor();

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatGptModelService.class);

    @Override
    public Mono<String> generate(final String prompt, final List<String> messages, final MessageEventData eventData) {
        LOGGER.debug("Called inference for ChatGPT.");
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

        final List<ChatGptMessage> chatGptMessages = lorebookEntryExtractionHelper.formatMessagesForChatGpt(messages, eventData);
        Optional.ofNullable(persona.getNudge())
                .filter(Nudge.isValid)
                .ifPresent(ndge -> {
                    chatGptMessages.add(chatGptMessages.stream()
                        .filter(m -> "user".equals(m.getRole()))
                        .mapToInt(chatGptMessages::indexOf)
                        .reduce((a, b) -> b)
                        .orElse(0) + 1,
                        ChatGptMessage.builder()
                                .role(ndge.role)
                                .content(ndge.content)
                                .build());
                });

        Optional.ofNullable(persona.getBump())
                .filter(Bump.isValid)
                .ifPresent(bmp -> {
                    ChatGptMessage bumpMessage = ChatGptMessage.builder()
                            .role(bmp.role)
                            .content(bmp.content)
                            .build();

                    for (int index = chatGptMessages.size() - 1 - bmp.frequency;
                            index > 0; index = index - bmp.frequency) {

                        chatGptMessages.add(index, bumpMessage);
                    }
                });
        final ChatGptRequest request = chatGptRequestTranslator.buildRequest(chatGptMessages, eventData.getChannelConfig());
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
