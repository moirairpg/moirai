package es.thalesalv.chatrpg.application.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.adapters.data.db.entity.LorebookEntry;
import es.thalesalv.chatrpg.adapters.rest.OpenAIApiService;
import es.thalesalv.chatrpg.application.config.MessageEventData;
import es.thalesalv.chatrpg.application.errorhandling.CommonErrorHandler;
import es.thalesalv.chatrpg.application.service.helper.MessageFormatHelper;
import es.thalesalv.chatrpg.application.service.interfaces.GptModelService;
import es.thalesalv.chatrpg.application.translator.Gpt3RequestTranslator;
import es.thalesalv.chatrpg.domain.exception.ModelResponseBlankException;
import es.thalesalv.chatrpg.domain.model.openai.gpt.Gpt3Request;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.User;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class Gpt3ModelService implements GptModelService {

    private final ModerationService moderationService;
    private final MessageFormatHelper lorebookEntryExtractionHelper;
    private final CommonErrorHandler commonErrorHandler;
    private final Gpt3RequestTranslator gptRequestTranslator;
    private final OpenAIApiService openAiService;

    private static final Logger LOGGER = LoggerFactory.getLogger(Gpt3ModelService.class);

    @Override
    public Mono<String> generate(final String prompt, final List<String> messages, final MessageEventData eventData) {

        LOGGER.debug("Called inference for GPT3. Persona -> {}", eventData.getPersona());
        final Mentions mentions = eventData.getMessage().getMentions();
        final User author = eventData.getMessageAuthor();
        final Set<LorebookEntry> entriesFound = new HashSet<>();

        lorebookEntryExtractionHelper.handleEntriesMentioned(messages, entriesFound);
        if (eventData.getPersona().getIntent().equals("dungeonMaster")) {
            lorebookEntryExtractionHelper.handlePlayerCharacterEntries(entriesFound, messages, author, mentions);
            lorebookEntryExtractionHelper.processEntriesFoundForRpg(entriesFound, messages, author.getJDA());
        } else {
            lorebookEntryExtractionHelper.processEntriesFoundForChat(entriesFound, messages, author.getJDA());
        }

        final Gpt3Request request = gptRequestTranslator.buildRequest(prompt, eventData.getPersona());
        return openAiService.callGptApi(request, eventData)
                .map(response -> {
                    final String responseText = response.getChoices().get(0).getText();
                    moderationService.moderate(responseText, eventData).subscribe();
                    return response;
                })
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