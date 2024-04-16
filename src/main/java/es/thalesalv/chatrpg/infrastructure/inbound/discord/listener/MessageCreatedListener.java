package es.thalesalv.chatrpg.infrastructure.inbound.discord.listener;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.PartialMember;
import discord4j.core.object.entity.User;
import es.thalesalv.chatrpg.common.usecases.UseCaseRunner;
import es.thalesalv.chatrpg.core.application.query.discord.messagereceived.MessageReceived;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class MessageCreatedListener implements DiscordEventListener<MessageCreateEvent> {

    private final UseCaseRunner useCaseRunner;

    @Override
    public Class<MessageCreateEvent> getEventType() {

        return MessageCreateEvent.class;
    }

    @Override
    public Mono<Void> onEvent(MessageCreateEvent event) {

        Message message = event.getMessage();
        String messageContent = message.getContent();
        String channelId = message.getChannelId().asString();

        if (StringUtils.isNotBlank(messageContent)) {
            return Mono.zip(event.getGuild(), message.getAuthorAsMember(), event.getClient().getSelf())
                    .filter(zipped -> !zipped.getT2().isBot())
                    .map(zipped -> {
                        Guild guild = (Guild) zipped.getT1();
                        Member author = (Member) zipped.getT2();
                        User bot = (User) zipped.getT3();

                        List<String> mentions = message.getMemberMentions()
                                .stream()
                                .map(PartialMember::getId)
                                .map(Snowflake::asString)
                                .toList();

                        return MessageReceived.builder()
                                .authordDiscordId(author.getId().asString())
                                .messageChannelId(channelId)
                                .messageId(message.getId().asString())
                                .messageContent(messageContent)
                                .messageGuildId(guild.getId().asString())
                                .isBotMentioned(mentions.contains(bot.getId().asString()))
                                .mentionedUsersIds(mentions)
                                .botName(bot.getUsername())
                                .build();
                    })
                    .flatMap(useCaseRunner::run);
        }

        return Mono.empty();
    }
}
