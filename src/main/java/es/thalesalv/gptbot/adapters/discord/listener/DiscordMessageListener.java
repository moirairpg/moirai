package es.thalesalv.gptbot.adapters.discord.listener;

import java.text.MessageFormat;

import javax.annotation.Nonnull;

import org.springframework.stereotype.Service;

import es.thalesalv.gptbot.adapters.data.ContextDatastore;
import es.thalesalv.gptbot.application.config.BotConfig;
import es.thalesalv.gptbot.application.usecases.ChatbotUseCase;
import es.thalesalv.gptbot.application.usecases.DungeonMasterUseCase;
import es.thalesalv.gptbot.application.usecases.ReplyQuoteUseCase;
import es.thalesalv.gptbot.domain.exception.ModerationException;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Mentions;
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
    private final ReplyQuoteUseCase replyQuoteUseCase;
    private final ContextDatastore contextDatastore;

    private final ChatbotUseCase chatbotUseCase;
    private final DungeonMasterUseCase dungeonMasterUseCase;

    private static final String RPG_CHANNEL = "rpg";
    private static final String CHAT_CHANNEL = "chat";

    private static final String MESSAGE_FLAGGED = "The message you sent has content that was flagged by OpenAI''s moderation. Message content: \n{0}";

    @Override
    public void onMessageReceived(final @Nonnull MessageReceivedEvent event) {

        final SelfUser bot = event.getJDA().getSelfUser();
        final Message message = event.getMessage();
        final MessageChannelUnion channel = event.getChannel();
        final User messageAuthor = event.getAuthor();
        final Mentions mentions = message.getMentions();

        try {
            if (!messageAuthor.isBot()) {
                final Message replyMessage = message.getReferencedMessage();
                botConfig.getChannels().forEach(channelConfig -> {
                    final boolean isCurrentChannel = channelConfig.getChannelIds().stream().anyMatch(id -> channel.getId().equals(id));
                    if (isCurrentChannel) {
                        contextDatastore.setCurrentChannel(channelConfig);
                        if (channelConfig.getChannelPurpose().equals(RPG_CHANNEL)
                                && mentions.isMentioned(bot, Message.MentionType.USER)) {
                            dungeonMasterUseCase.generateResponse(bot, messageAuthor, message, channel, mentions);
                        } else if (channelConfig.getChannelPurpose().equals(CHAT_CHANNEL)) {
                            if (replyMessage != null) {
                                replyQuoteUseCase.generateResponse(bot, messageAuthor, message, channel, mentions);
                            } else {
                                chatbotUseCase.generateResponse(bot, messageAuthor, message, channel, mentions);
                            }
                        }
                    }
                });
            }
        } catch (ModerationException e) {
            messageAuthor.openPrivateChannel()
                    .queue(privateChannel -> {
                        message.delete().queue();
                        privateChannel.sendMessage(MessageFormat.format(MESSAGE_FLAGGED, message.getContentDisplay()))
                                .queue();
                    });
        }
    }
}
