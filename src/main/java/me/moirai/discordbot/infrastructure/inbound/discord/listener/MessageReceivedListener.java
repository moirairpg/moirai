package me.moirai.discordbot.infrastructure.inbound.discord.listener;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import me.moirai.discordbot.common.exception.AssetNotFoundException;
import me.moirai.discordbot.common.usecases.UseCaseRunner;
import me.moirai.discordbot.core.application.usecase.discord.messagereceived.MessageReceived;
import me.moirai.discordbot.core.application.usecase.discord.messagereceived.RpgModeDto;
import me.moirai.discordbot.core.domain.channelconfig.ChannelConfig;
import me.moirai.discordbot.core.domain.channelconfig.ChannelConfigRepository;
import me.moirai.discordbot.core.domain.channelconfig.GameMode;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Component
public class MessageReceivedListener extends ListenerAdapter {

    private final UseCaseRunner useCaseRunner;
    private final ChannelConfigRepository channelConfigRepository;

    public MessageReceivedListener(UseCaseRunner useCaseRunner,
            ChannelConfigRepository channelConfigRepository) {

        this.useCaseRunner = useCaseRunner;
        this.channelConfigRepository = channelConfigRepository;
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

        GameMode gameMode = channelConfigRepository.findByDiscordChannelId(channelId)
                .map(ChannelConfig::getGameMode)
                .orElseThrow(() -> new AssetNotFoundException("Channel config not found for this channel"));

        if (StringUtils.isNotBlank(messageContent) && !author.getUser().isBot()) {
            String botUsername = bot.getUser().getName();
            String botNickname = StringUtils.isNotBlank(bot.getNickname()) ? bot.getNickname() : botUsername;

            switch (gameMode) {
                case CHAT -> {
                    MessageReceived request = MessageReceived.builder()
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
                case RPG -> {
                    RpgModeDto request = RpgModeDto.builder()
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
