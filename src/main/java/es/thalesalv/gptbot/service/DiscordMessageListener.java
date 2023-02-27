package es.thalesalv.gptbot.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.thalesalv.gptbot.data.ContextDatastore;
import es.thalesalv.gptbot.data.db.repository.CharacterProfileRepository;
import es.thalesalv.gptbot.model.bot.BotSettings;
import es.thalesalv.gptbot.usecases.BotMentionedUseCase;
import es.thalesalv.gptbot.usecases.RPGUseCase;
import es.thalesalv.gptbot.usecases.ReplyQuoteUseCase;
import es.thalesalv.gptbot.usecases.TextGenerationUseCase;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Service
@RequiredArgsConstructor
public class DiscordMessageListener extends ListenerAdapter {

    @Value("classpath:bot-settings.json")
    private Resource botSettingsFile;

    @Value("${config.discord.bot-technical-channel-id}")
    private String botTechnicalChannelId;

    private final BotMentionedUseCase botMentionedUseCase;
    private final ReplyQuoteUseCase replyQuoteUseCase;
    private final TextGenerationUseCase textGenerationUseCase;
    private final RPGUseCase rpgUseCase;

    private final CharacterProfileRepository characterProfileRepository;

    private final ContextDatastore contextDatastore;
    private final ObjectMapper objectMapper;

    // private static final String BOT_TOKENS_PROMPT = "------\n**Prompt:** {0}";
    // private static final String BOT_TOKENS_REPLY = "**Text generated:** {0}";
    // private static final String BOT_TOKENS_DETAILS = "**Prompt tokens:** {0}\n**Generation tokens:** {1}\n**Model:** {2}";
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscordMessageListener.class);

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        try {
            final var bot = event.getJDA().getSelfUser();
            final var message = event.getMessage();
            final var channel = event.getChannel();
            final var author = event.getAuthor();

            final var botSettings = objectMapper.readValue(botSettingsFile.getContentAsString(StandardCharsets.UTF_8), BotSettings.class);
            final var rpgChannel = botSettings.getChannelSettings().get("rpg");
            final var chatChannel = botSettings.getChannelSettings().get("chat");

            if (!author.isBot()) {

                final var replyMessage = message.getReferencedMessage();
                if (rpgChannel.getChannelIds().stream().anyMatch(id -> channel.getId().equals(id))
                        && message.getMentions().isMentioned(bot, Message.MentionType.USER)) {
                    contextDatastore.setCurrentChannel(rpgChannel);
                    rpgUseCase.generateResponse(bot, author, message.getMentions(), channel);
                } else if (chatChannel.getChannelIds().stream().anyMatch(id -> channel.getId().equals(id))) {
                    contextDatastore.setCurrentChannel(chatChannel);
                    if (replyMessage != null) {
                        replyQuoteUseCase.generateResponse(bot, author, message, replyMessage);
                    } else {
                        textGenerationUseCase.generateResponse(bot, message, channel);
                    }
                } else if (message.getMentions().isMentioned(bot, Message.MentionType.USER)) {
                    botMentionedUseCase.generateResponse(message, channel, bot);
                }
            }
        } catch (IOException e) {
            LOGGER.error("Error parsing file or format -> {}", e);
            throw new RuntimeException(e);
        } finally {
            contextDatastore.cleanCurrentChannel();
        }
    }
}
