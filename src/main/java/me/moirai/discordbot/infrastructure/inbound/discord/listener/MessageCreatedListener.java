// package me.moirai.discordbot.infrastructure.inbound.discord.listener;

// import java.util.List;

// import org.apache.commons.lang3.StringUtils;
// import org.springframework.stereotype.Component;

// import discord4j.common.util.Snowflake;
// import discord4j.core.event.domain.message.MessageCreateEvent;
// import discord4j.core.object.entity.Guild;
// import discord4j.core.object.entity.Member;
// import discord4j.core.object.entity.Message;
// import me.moirai.discordbot.common.usecases.UseCaseRunner;
// import me.moirai.discordbot.common.util.DefaultStringProcessors;
// import me.moirai.discordbot.core.application.usecase.discord.messagereceived.MessageReceived;
// import reactor.core.publisher.Mono;

// @Component
// public class MessageCreatedListener implements DiscordEventListener<MessageCreateEvent> {

//     private final UseCaseRunner useCaseRunner;

//     public MessageCreatedListener(UseCaseRunner useCaseRunner) {
//         this.useCaseRunner = useCaseRunner;
//     }

//     @Override
//     public Class<MessageCreateEvent> eventType() {

//         return MessageCreateEvent.class;
//     }

//     @Override
//     public Mono<Void> onEvent(MessageCreateEvent event) {

//         Message message = event.getMessage();
//         String messageContent = message.getContent();
//         String channelId = message.getChannelId().asString();
//         List<String> mentions = DefaultStringProcessors.extractDiscordIds().apply(messageContent);
//         Snowflake guildId = event.getGuildId()
//                 .orElseThrow(() -> new IllegalStateException("Guild ID not found"));

//         if (StringUtils.isNotBlank(messageContent)) {
//             return Mono
//                     .zip(event.getGuild(), message.getAuthorAsMember(), event.getClient().getSelfMember(guildId))
//                     .filter(zipped -> !zipped.getT2().isBot())
//                     .map(zipped -> {
//                         Guild guild = zipped.getT1();
//                         Member author = zipped.getT2();
//                         Member bot = zipped.getT3();

//                         return MessageReceived.builder()
//                                 .authordDiscordId(author.getId().asString())
//                                 .messageChannelId(channelId)
//                                 .messageId(message.getId().asString())
//                                 .messageGuildId(guild.getId().asString())
//                                 .isBotMentioned(mentions.contains(bot.getId().asString()))
//                                 .mentionedUsersIds(mentions)
//                                 .botUsername(bot.getUsername())
//                                 .botNickname(bot.getNickname()
//                                         .orElse(bot.getUsername()))
//                                 .build();
//                     })
//                     .flatMap(useCaseRunner::run);
//         }

//         return Mono.empty();
//     }
// }
