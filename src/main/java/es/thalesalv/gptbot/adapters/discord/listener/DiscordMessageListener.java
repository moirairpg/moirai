package es.thalesalv.gptbot.adapters.discord.listener;

import javax.annotation.Nonnull;

import org.springframework.stereotype.Service;

import es.thalesalv.gptbot.adapters.data.ContextDatastore;
import es.thalesalv.gptbot.application.config.BotConfig;
import es.thalesalv.gptbot.application.usecases.RPGUseCase;
import es.thalesalv.gptbot.application.usecases.ReplyQuoteUseCase;
import es.thalesalv.gptbot.application.usecases.TextGenerationUseCase;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Service
@RequiredArgsConstructor
public class DiscordMessageListener extends ListenerAdapter {

    private final BotConfig botConfig;
    private final RPGUseCase rpgUseCase;
    private final ReplyQuoteUseCase replyQuoteUseCase;
    private final TextGenerationUseCase textGenerationUseCase;
    private final ContextDatastore contextDatastore;

    private static final String RPG_CHANNEL = "rpg";
    private static final String CHAT_CHANNEL = "chat";

    @Override
    public void onMessageReceived(final @Nonnull MessageReceivedEvent event) {

        final SelfUser bot = event.getJDA().getSelfUser();
        final Message message = event.getMessage();
        final MessageChannelUnion channel = event.getChannel();
        final User author = event.getAuthor();

        if (!author.isBot()) {
            final Message replyMessage = message.getReferencedMessage();
            botConfig.getChannels().forEach(channelConfig -> {
                final boolean isCurrentChannel = channelConfig.getChannelIds().stream().anyMatch(id -> channel.getId().equals(id));
                if (isCurrentChannel) {
                    contextDatastore.setCurrentChannel(channelConfig);
                    if (channelConfig.getChannelPurpose().equals(RPG_CHANNEL) && message.getMentions().isMentioned(bot, Message.MentionType.USER)) {
                        rpgUseCase.generateResponse(bot, author, message.getMentions(), channel);
                    } else if (channelConfig.getChannelPurpose().equals(CHAT_CHANNEL)) {
                        if (replyMessage != null) {
                            replyQuoteUseCase.generateResponse(bot, author, message, replyMessage, channel);
                        } else {
                            textGenerationUseCase.generateResponse(bot, message, channel);
                        }
                    }
                }
            });
        }
    }
}
