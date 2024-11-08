package me.moirai.discordbot.infrastructure.outbound.adapter.request;

public class StoryGenerationRequestFixture {

    public static StoryGenerationRequest.Builder create() {

        return StoryGenerationRequest.builder()
                .botId("BOTID")
                .botUsername("TestBot")
                .botNickname("BotNickname")
                .channelId("CHNLID")
                .guildId("GLDID")
                .worldId("WRLDID")
                .personaId("PRSNID")
                .gameMode("CHAT")
                .modelConfiguration(ModelConfigurationRequestFixture.gpt4Mini().build())
                .moderation(ModerationConfigurationRequestFixture.absolute());
    }
}
