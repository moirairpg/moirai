package me.moirai.discordbot.infrastructure.inbound.discord.listener;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import me.moirai.discordbot.common.usecases.UseCaseRunner;
import me.moirai.discordbot.core.application.helper.ChannelConfigHelper;
import me.moirai.discordbot.core.application.usecase.discord.messagereceived.ChatModeRequest;
import me.moirai.discordbot.core.application.usecase.discord.messagereceived.RpgModeRequest;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Component
public class MessageReceivedListener extends ListenerAdapter {

    private final UseCaseRunner useCaseRunner;
    private final ChannelConfigHelper channelConfigHelper;

    public MessageReceivedListener(UseCaseRunner useCaseRunner,
            ChannelConfigHelper channelConfigHelper) {

        this.useCaseRunner = useCaseRunner;
        this.channelConfigHelper = channelConfigHelper;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

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

                    useCaseRunner.run(request).subscribe();
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

                    useCaseRunner.run(request).subscribe();
                }
            }
        }
    }
}
