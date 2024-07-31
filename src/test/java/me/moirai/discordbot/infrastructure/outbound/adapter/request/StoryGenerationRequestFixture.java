package me.moirai.discordbot.infrastructure.outbound.adapter.request;

import org.assertj.core.util.Lists;

public class StoryGenerationRequestFixture {

    public static StoryGenerationRequest.Builder create() {

        return StoryGenerationRequest.builder()
                .botId("BOTID")
                .botUsername("TestBot")
                .botNickname("BotNickname")
                .mentionedUsersIds(Lists.list("USR1", "USR2"))
                .channelId("CHNLID")
                .guildId("GLDID")
                .worldId("WRLDID")
                .personaId("PRSNID")
                .modelConfiguration(ModelConfigurationRequestFixture.gpt4Mini().build())
                .moderation(ModerationConfigurationRequestFixture.absolute());
    }
}
