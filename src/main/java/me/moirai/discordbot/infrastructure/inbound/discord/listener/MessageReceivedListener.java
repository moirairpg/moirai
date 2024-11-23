package me.moirai.discordbot.infrastructure.inbound.discord.listener;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import me.moirai.discordbot.common.usecases.UseCaseRunner;
import me.moirai.discordbot.core.application.helper.ChannelConfigHelper;
import me.moirai.discordbot.core.application.usecase.discord.messagereceived.AuthorModeRequest;
import me.moirai.discordbot.core.application.usecase.discord.messagereceived.ChatModeRequest;
import me.moirai.discordbot.core.application.usecase.discord.messagereceived.RpgModeRequest;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Component
public class MessageReceivedListener extends ListenerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(SlashCommandListener.class);

    private static final String SOMETHING_WENT_WRONG = "Something went wrong. Please try again.";

    private final UseCaseRunner useCaseRunner;
    private final ChannelConfigHelper channelConfigHelper;

    public MessageReceivedListener(UseCaseRunner useCaseRunner,
            ChannelConfigHelper channelConfigHelper) {

        this.useCaseRunner = useCaseRunner;
        this.channelConfigHelper = channelConfigHelper;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        try {
            Message message = event.getMessage();
            Member author = event.getMember();
            Member bot = event.getGuild().getMember(event.getJDA().getSelfUser());
            List<String> mentions = message.getMentions()
                    .getMembers()
                    .stream()
                    .map(Member::getId)
                    .toList();

            String guildId = event.getGuild().getId();
            String channelId = event.getChannel().getId();
            String messageContent = message.getContentRaw();
            String gameMode = channelConfigHelper.getGameModeByDiscordChannelId(channelId);

            if (StringUtils.isNoneBlank(messageContent, gameMode) && !author.getUser().isBot()) {
                String botUsername = bot.getUser().getName();
                String botNickname = StringUtils.isNotBlank(bot.getNickname()) ? bot.getNickname() : botUsername;

                switch (gameMode) {
                    case "CHAT" -> {
                        ChatModeRequest request = ChatModeRequest.builder()
                                .authordDiscordId(author.getId())
                                .channelId(channelId)
                                .messageId(message.getId())
                                .guildId(guildId)
                                .isBotMentioned(mentions.contains(bot.getId()))
                                .mentionedUsersIds(mentions)
                                .botUsername(botUsername)
                                .botNickname(botNickname)
                                .build();

                        useCaseRunner.run(request)
                                .doOnError(error -> errorNotification(event, error))
                                .subscribe();
                    }
                    case "RPG" -> {
                        RpgModeRequest request = RpgModeRequest.builder()
                                .authordDiscordId(author.getId())
                                .channelId(channelId)
                                .messageId(message.getId())
                                .guildId(guildId)
                                .isBotMentioned(mentions.contains(bot.getId()))
                                .mentionedUsersIds(mentions)
                                .botUsername(botUsername)
                                .botNickname(botNickname)
                                .build();

                        useCaseRunner.run(request)
                                .doOnError(error -> errorNotification(event, error))
                                .subscribe();
                    }
                    case "AUTHOR" -> {
                        AuthorModeRequest request = AuthorModeRequest.builder()
                                .authordDiscordId(author.getId())
                                .channelId(channelId)
                                .messageId(message.getId())
                                .guildId(guildId)
                                .isBotMentioned(mentions.contains(bot.getId()))
                                .mentionedUsersIds(mentions)
                                .botUsername(botUsername)
                                .botNickname(botNickname)
                                .build();

                        useCaseRunner.run(request)
                                .doOnError(error -> errorNotification(event, error))
                                .subscribe();
                    }
                }
            }
        } catch (Exception e) {
            errorNotification(event, e);
        }
    }

    private void errorNotification(MessageReceivedEvent event, Throwable error) {

        LOG.error("An error occured while processing message received from Discord", error);
        event.getChannel()
                .sendMessage(SOMETHING_WENT_WRONG)
                .complete()
                .delete()
                .completeAfter(20, SECONDS);
    }
}
