package me.moirai.discordbot.core.domain.channelconfig;

import java.time.OffsetDateTime;

import me.moirai.discordbot.core.domain.PermissionsFixture;
import me.moirai.discordbot.core.domain.Visibility;

public class ChannelConfigFixture {

    private static final String OWNER_DISCORD_ID = "586678721356875";

    public static ChannelConfig.Builder sample() {

        ChannelConfig.Builder builder = ChannelConfig.builder();
        builder.id("CHCONFID");
        builder.name("Name");
        builder.worldId("WRLDID");
        builder.personaId("PRSNID");
        builder.discordChannelId("CHNLID");
        builder.moderation(Moderation.STRICT);
        builder.visibility(Visibility.fromString("PRIVATE"));
        builder.modelConfiguration(ModelConfigurationFixture.gpt4Mini().build());
        builder.permissions(PermissionsFixture.samplePermissions().build());
        builder.creatorDiscordId(OWNER_DISCORD_ID);
        builder.creationDate(OffsetDateTime.now());
        builder.lastUpdateDate(OffsetDateTime.now());
        builder.discordChannelId("12345");
        builder.gameMode(GameMode.RPG);

        return builder;
    }
}
