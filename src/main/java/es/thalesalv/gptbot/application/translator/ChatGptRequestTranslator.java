package es.thalesalv.gptbot.application.translator;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import es.thalesalv.gptbot.application.config.Persona;
import es.thalesalv.gptbot.domain.model.openai.gpt.ChatGptMessage;
import es.thalesalv.gptbot.domain.model.openai.gpt.ChatGptRequest;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.SelfUser;

@Component
@RequiredArgsConstructor
public class ChatGptRequestTranslator {

    @Value("${config.bot.generation.default-max-tokens}")
    private int defaultMaxTokens;

    @Value("${config.bot.generation.default-temperature}")
    private double defaultTemperature;

    @Value("${config.bot.generation.default-presence-penalty}")
    private double defaultPresencePenalty;

    @Value("${config.bot.generation.default-frequency-penalty}")
    private double defaultFrequencyPenalty;

    private static final String ROLE_SYSTEM = "system";
    private static final String ROLE_ASSISTANT = "assistant";
    private static final String ROLE_USER = "user";
    private static final String DUNGEON_MASTER = "Dungeon Master";

    private final JDA jda;

    public ChatGptRequest buildRequest(List<String> messages, String model, Persona persona) {

        final SelfUser bot = jda.getSelfUser();
        final String personality = persona.getPersonality().replace("{0}", bot.getName());
        final int maxTokens = persona == null ? defaultMaxTokens : persona.getMaxTokens();
        final double temperature = persona == null ? defaultTemperature : persona.getTemperature();
        final double presencePenalty = persona == null ? defaultPresencePenalty : persona.getPresencePenalty();
        final double frequencyPenalty = persona == null ? defaultFrequencyPenalty : persona.getFrequencyPenalty();

        List<ChatGptMessage> chatGptMessages = messages.stream()
                .filter(msg -> !msg.trim().equals((bot.getName() + " said:").trim()))
                .map(msg -> {
                    String role = determineRole(msg, bot);
                    msg = msg.replace(bot.getName() + " said: ", StringUtils.EMPTY)
                    .replaceAll("Dungeon Master says: ", StringUtils.EMPTY);
                    return ChatGptMessage.builder()
                            .role(role)
                            .content(msg)
                            .build();
                })
                .collect(Collectors.toList());

        chatGptMessages.add(0, ChatGptMessage.builder()
                .role(ROLE_SYSTEM)
                .content(MessageFormat.format(personality, bot.getName())
                        .replace("@" + bot.getName(), StringUtils.EMPTY).trim())
                .build());

        return ChatGptRequest.builder()
            .messages(chatGptMessages)
            .model(model)
            .maxTokens(maxTokens)
            .temperature(temperature)
            .presencePenalty(presencePenalty)
            .frequencyPenalty(frequencyPenalty)
            .build();
    }

    private String determineRole(String message, SelfUser bot) {
        
        if (message.startsWith(bot.getName()) || message.startsWith(DUNGEON_MASTER)) {
            return ROLE_ASSISTANT;
        } else {
            return ROLE_USER;
        }
    }
}
