package es.thalesalv.chatrpg.application.service.completion;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.adapters.rest.OpenAIApiService;
import es.thalesalv.chatrpg.application.errorhandling.CommonErrorHandler;
import es.thalesalv.chatrpg.application.helper.MessageFormatHelper;
import es.thalesalv.chatrpg.application.mapper.airequest.TextCompletionRequestMapper;
import es.thalesalv.chatrpg.application.util.StringProcessor;
import es.thalesalv.chatrpg.domain.exception.ModelResponseBlankException;
import es.thalesalv.chatrpg.domain.model.EventData;
import es.thalesalv.chatrpg.domain.model.chconf.ChannelConfig;
import es.thalesalv.chatrpg.domain.model.chconf.LorebookEntry;
import es.thalesalv.chatrpg.domain.model.chconf.Persona;
import es.thalesalv.chatrpg.domain.model.chconf.World;
import es.thalesalv.chatrpg.domain.model.openai.completion.TextCompletionRequest;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.User;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TextCompletionService implements CompletionService {

    private final MessageFormatHelper messageFormatHelper;
    private final CommonErrorHandler commonErrorHandler;
    private final TextCompletionRequestMapper textCompletionRequestTranslator;
    private final OpenAIApiService openAiService;
    private static final Logger LOGGER = LoggerFactory.getLogger(TextCompletionService.class);

    @Override
    public Mono<String> generate(List<String> messages, EventData eventData) {

        LOGGER.debug("Called inference for Text Completions.");
        final StringProcessor outputProcessor = new StringProcessor();
        final StringProcessor inputProcessor = new StringProcessor();
        final Mentions mentions = eventData.getMessage()
                .getMentions();
        final User author = eventData.getMessageAuthor();
        final ChannelConfig channelConfig = eventData.getChannelDefinitions()
                .getChannelConfig();
        final World world = channelConfig.getWorld();
        final Persona persona = channelConfig.getPersona();
        inputProcessor.addRule(s -> Pattern.compile("\\{0\\}")
                .matcher(s)
                .replaceAll(r -> persona.getName()));
        inputProcessor.addRule(s -> Pattern.compile(eventData.getBot()
                .getName())
                .matcher(s)
                .replaceAll(r -> persona.getName()));
        outputProcessor.addRule(s -> Pattern.compile("\\bAs " + persona.getName() + ", (\\w)")
                .matcher(s)
                .replaceAll(r -> r.group(1)
                        .toUpperCase()));
        outputProcessor.addRule(s -> Pattern.compile("\\bas " + persona.getName() + ", (\\w)")
                .matcher(s)
                .replaceAll(r -> r.group(1)));
        outputProcessor.addRule(
                s -> Pattern.compile("(?<=[.!?\\n])\"?[^.!?\\n]*(?![.!?\\n])$", Pattern.DOTALL & Pattern.MULTILINE)
                        .matcher(s)
                        .replaceAll(StringUtils.EMPTY));
        final Set<LorebookEntry> entriesFound = messageFormatHelper.handleEntriesMentioned(messages, world);
        if (persona.getIntent()
                .equals("rpg")) {
            messageFormatHelper.handlePlayerCharacterEntries(entriesFound, messages, author, mentions, world);
            messageFormatHelper.processEntriesFoundForRpg(entriesFound, messages, author.getJDA());
        } else {
            messageFormatHelper.processEntriesFoundForChat(entriesFound, messages);
        }
        final List<String> chatMessages = messageFormatHelper.formatMessages(messages, eventData, inputProcessor);
        final String chatifiedMessage = messageFormatHelper.chatifyMessages(chatMessages, eventData, inputProcessor);
        final TextCompletionRequest request = textCompletionRequestTranslator.buildRequest(chatifiedMessage,
                eventData.getChannelDefinitions()
                        .getChannelConfig());
        return openAiService.callGptApi(request, eventData)
                .map(response -> {
                    final String responseText = response.getChoices()
                            .get(0)
                            .getText();
                    if (StringUtils.isBlank(responseText)) {
                        throw new ModelResponseBlankException();
                    }
                    return outputProcessor.process(responseText.trim());
                })
                .doOnError(ModelResponseBlankException.class::isInstance,
                        e -> commonErrorHandler.handleEmptyResponse(eventData));
    }
}
