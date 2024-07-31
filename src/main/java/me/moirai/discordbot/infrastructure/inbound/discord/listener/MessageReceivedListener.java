package me.moirai.discordbot.infrastructure.inbound.discord.listener;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import me.moirai.discordbot.common.usecases.UseCaseRunner;
import me.moirai.discordbot.core.application.usecase.discord.messagereceived.MessageReceived;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Component
public class MessageReceivedListener extends ListenerAdapter {

    private final UseCaseRunner useCaseRunner;

    public MessageReceivedListener(UseCaseRunner useCaseRunner) {
        this.useCaseRunner = useCaseRunner;
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

        if (StringUtils.isNotBlank(messageContent) && !author.getUser().isBot()) {
            MessageReceived request = MessageReceived.builder()
                    .authordDiscordId(author.getId())
                    .channelId(channelId)
                    .messageId(message.getId())
                    .guildId(guildId)
                    .isBotMentioned(mentions.contains(bot.getId()))
                    .mentionedUsersIds(mentions)
                    .botUsername(bot.getUser().getName())
                    .botNickname(bot.getNickname())
                    .build();

            useCaseRunner.run(request).subscribe();
        }
    }
}
