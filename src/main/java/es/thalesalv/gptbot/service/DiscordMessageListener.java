package es.thalesalv.gptbot.service;

import es.thalesalv.gptbot.usecases.ReplyQuoteUseCase;
import es.thalesalv.gptbot.usecases.TextGenerationUseCase;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiscordMessageListener extends ListenerAdapter {

    @Value("#{'${config.discord.bot-channel-id}'.split(',')}")
    private List<String> botChannelId;

    @Value("${config.discord.bot-instructions}")
    private String botInstructions;

    private final GptService gptService;
    private final ReplyQuoteUseCase replyQuoteUseCase;
    private final TextGenerationUseCase textGenerationUseCase;

    private static final Logger LOGGER = LoggerFactory.getLogger(DiscordMessageListener.class);

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        final var bot = event.getJDA().getSelfUser();
        final var message = event.getMessage();
        final var channel = event.getChannel();
        final var author = event.getAuthor();

        final var isCorrectChannel = botChannelId.stream().anyMatch(id -> channel.getId().equals(id));
        final var isAuthorBot = author.isBot();

        if (isCorrectChannel && !isAuthorBot) {

            LOGGER.info("{} said in {}: {}", event.getAuthor().getName(), channel.getName(), message.getContentDisplay());
            final var messages = new ArrayList<String>();
            final var replyMessage = message.getReferencedMessage();

            if (replyMessage != null) {
                replyQuoteUseCase.generateResponse(messages, author, message, replyMessage);
            } else {
                textGenerationUseCase.generateResponse(messages, channel);
            }

            gptService.callDaVinci(chatifyMessages(bot, messages))
                .filter(r -> !r.isBlank())
                .map(response -> {
                    event.getChannel().sendMessage(response).queue();
                    return response;
                }).subscribe();
        }
    }

    private String chatifyMessages(User bot, List<String> messages) {

        messages.add(0, botInstructions.replace("<BOT>", bot.getAsTag()).replace("<BOTNICK>", bot.getName()));
        return new StringBuilder()
                .append(messages.stream().collect(Collectors.joining("\n")))
                .append(StringUtils.LF)
                .append(bot.getAsTag() + " said: ")
                .toString().trim();
    }
}
