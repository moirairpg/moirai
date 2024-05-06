package es.thalesalv.chatrpg.core.application.query.discord.messagereceived;

import static es.thalesalv.chatrpg.core.application.model.request.ChatMessage.Role.ASSISTANT;
import static es.thalesalv.chatrpg.core.application.model.request.ChatMessage.Role.USER;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import es.thalesalv.chatrpg.common.annotation.UseCaseHandler;
import es.thalesalv.chatrpg.common.usecases.AbstractUseCaseHandler;
import es.thalesalv.chatrpg.common.util.DefaultStringProcessors;
import es.thalesalv.chatrpg.common.util.StringProcessor;
import es.thalesalv.chatrpg.core.application.model.request.ChatMessage;
import es.thalesalv.chatrpg.core.application.model.request.TextGenerationRequest;
import es.thalesalv.chatrpg.core.application.model.result.TextGenerationResult;
import es.thalesalv.chatrpg.core.application.port.DiscordChannelPort;
import es.thalesalv.chatrpg.core.application.port.OpenAiPort;
import es.thalesalv.chatrpg.core.application.service.LorebookEnrichmentService;
import es.thalesalv.chatrpg.core.application.service.PersonaEnrichmentService;
import es.thalesalv.chatrpg.core.application.service.StorySummarizationService;
import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfig;
import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfigRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@UseCaseHandler
@RequiredArgsConstructor
public class MessageReceivedHandler extends AbstractUseCaseHandler<MessageReceived, Mono<Void>> {

    private static final String BUMP = "bump";
    private static final String LOREBOOK_ENTRIES = "lorebook";
    private static final String STORY_SUMMARY = "summary";
    private static final String NUDGE = "nudge";
    private static final String PERSONA = "persona";
    private static final String PERSONA_NAME = "personaName";
    private static final String MESSAGE_HISTORY = "messageHistory";
    private static final String SAID = " said: ";
    private static final String SENTENCE_EXPRESSION = "((\\. |))(?:[ A-Za-z0-9-\"'&(),:;<>\\/\\\\]|\\.(?! ))+[\\?\\.\\!\\;'\"]$";
    private static final String PERIOD = ".";
    private static final int DISCORD_MAX_LENGTH = 2000;

    private final StorySummarizationService summarizationService;
    private final LorebookEnrichmentService lorebookEnrichmentService;
    private final PersonaEnrichmentService personaEnrichmentService;
    private final ChannelConfigRepository channelConfigRepository;
    private final DiscordChannelPort discordChannelOperationsPort;
    private final OpenAiPort openAiPort;

    @Override
    public Mono<Void> execute(MessageReceived query) {

        return channelConfigRepository.findByDiscordChannelId(query.getMessageChannelId())
                .filter(channelConfig -> channelConfig.getDiscordChannelId().equals(query.getMessageChannelId()))
                .map(channelConfig -> summarizationService
                        .summarizeWith(query.getMessageGuildId(), query.getMessageChannelId(), query.getMessageId(),
                                query.getBotName(), channelConfig.getModelConfiguration(), query.getMentionedUsersIds())
                        .flatMap(context -> lorebookEnrichmentService.enrich(channelConfig.getWorldId(),
                                context, channelConfig.getModelConfiguration()))
                        .flatMap(context -> personaEnrichmentService.enrich(channelConfig.getPersonaId(),
                                query.getBotName(), context, channelConfig.getModelConfiguration()))
                        .map(unsortedContext -> buildContextAsChatMessages(unsortedContext, query.getBotName()))
                        .map(processedContext -> buildTextGenerationRequest(channelConfig, processedContext))
                        .flatMap(openAiPort::generateTextFrom)
                        .flatMap(generationResult -> sendOutputToChannel(query, generationResult)))
                .orElseGet(() -> Mono.empty());
    }

    @SuppressWarnings("unchecked")
    private List<ChatMessage> extractMessageHistoryFrom(Map<String, Object> unsortedContext,
            String botName, String personaName) {

        List<String> messageHistory = (List<String>) unsortedContext.get(MESSAGE_HISTORY);
        return messageHistory.stream()
                .map(message -> {
                    StringProcessor processor = new StringProcessor();
                    processor.addRule(DefaultStringProcessors.replaceBotNameWithPersonaName(personaName, botName));

                    String modifiedContent = processor.process(message);
                    String senderName = message.substring(0, message.indexOf(SAID));
                    ChatMessage.Role senderRole = senderName.equals(botName) ? ASSISTANT : USER;

                    return ChatMessage.build(senderRole, modifiedContent);
                })
                .toList();
    }

    private List<ChatMessage> extractBumpFrom(Map<String, Object> unsortedContext, List<ChatMessage> processedContext) {

        String bump = (String) unsortedContext.get(BUMP);
        if (StringUtils.isNotBlank(bump)) {
            int bumpFrequency = 5;
            for (int index = processedContext.size() - 1 - bumpFrequency; index > 0; index = index - bumpFrequency) {
                processedContext.add(index, ChatMessage.build(ChatMessage.Role.SYSTEM, bump));
            }
        }

        return processedContext;
    }

    private TextGenerationRequest buildTextGenerationRequest(ChannelConfig channelConfig,
            List<ChatMessage> processedContext) {

        return TextGenerationRequest.builder()
                .frequencyPenalty(channelConfig.getModelConfiguration().getFrequencyPenalty())
                .presencePenalty(channelConfig.getModelConfiguration().getPresencePenalty())
                .temperature(channelConfig.getModelConfiguration().getTemperature())
                .model(channelConfig.getModelConfiguration().getAiModel().getOfficialModelName())
                .logitBias(channelConfig.getModelConfiguration().getLogitBias())
                .maxTokens(channelConfig.getModelConfiguration().getMaxTokenLimit())
                .stopSequences(channelConfig.getModelConfiguration().getStopSequences())
                .messages(processedContext)
                .build();
    }

    private String processSummarization(String summary, String botName, String personaName) {

        StringProcessor processor = new StringProcessor();
        processor.addRule(DefaultStringProcessors.stripChatPrefix());
        processor.addRule(DefaultStringProcessors.stripTrailingFragment());
        processor.addRule(DefaultStringProcessors.replaceBotNameWithPersonaName(personaName, botName));

        return processor.process(summary);
    }

    private List<ChatMessage> buildContextAsChatMessages(Map<String, Object> unsortedContext, String botName) {

        String persona = (String) unsortedContext.get(PERSONA);
        String personaName = (String) unsortedContext.get(PERSONA_NAME);
        String nudge = (String) unsortedContext.get(NUDGE);
        String storySummary = (String) unsortedContext.get(STORY_SUMMARY);
        String lorebookEntries = (String) unsortedContext.get(LOREBOOK_ENTRIES);

        List<ChatMessage> processedContext = new ArrayList<>();
        processedContext.add(
                ChatMessage.build(ChatMessage.Role.SYSTEM, processSummarization(storySummary, botName, personaName)));

        processedContext.addAll(extractMessageHistoryFrom(unsortedContext, botName, personaName));

        if (StringUtils.isNotBlank(lorebookEntries)) {
            processedContext.add(0, ChatMessage.build(ChatMessage.Role.SYSTEM, lorebookEntries));
        }

        processedContext.add(0, ChatMessage.build(ChatMessage.Role.SYSTEM, persona));

        if (StringUtils.isNotBlank(nudge)) {
            processedContext.add(ChatMessage.build(ChatMessage.Role.SYSTEM, nudge));
        }

        return extractBumpFrom(unsortedContext, processedContext);
    }

    private Mono<Void> sendOutputToChannel(MessageReceived query, TextGenerationResult generationResult) {

        StringProcessor processor = new StringProcessor();
        processor.addRule(DefaultStringProcessors.stripAsNamePrefixForLowercase(query.getBotName()));
        processor.addRule(DefaultStringProcessors.stripAsNamePrefixForUppercase(query.getBotName()));
        processor.addRule(DefaultStringProcessors.stripChatPrefix());
        processor.addRule(DefaultStringProcessors.stripTrailingFragment());

        String output = processor.process(generationResult.getOutputText());
        int outputSize = output.length();

        while (outputSize > DISCORD_MAX_LENGTH) {
            output = output.replaceAll(SENTENCE_EXPRESSION, PERIOD).trim();
            output = output.equals(PERIOD) ? EMPTY : output;
            outputSize = output.length();
        }

        return discordChannelOperationsPort.sendMessage(query.getMessageChannelId(), output);
    }
}
