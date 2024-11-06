package me.moirai.discordbot.infrastructure.outbound.adapter.request;

import java.util.*;

import org.apache.commons.collections4.CollectionUtils;

import me.moirai.discordbot.core.application.usecase.discord.DiscordMessageData;

public class StoryGenerationRequest {

    private final String botId;
    private final String botUsername;
    private final String botNickname;
    private final String channelId;
    private final String guildId;
    private final String worldId;
    private final String personaId;
    private final String gameMode;
    private final ModelConfigurationRequest modelConfiguration;
    private final ModerationConfigurationRequest moderation;
    private final List<DiscordMessageData> messageHistory;

    protected StoryGenerationRequest(Builder builder) {

        this.botId = builder.botId;
        this.botUsername = builder.botUsername;
        this.botNickname = builder.botNickname;
        this.channelId = builder.channelId;
        this.guildId = builder.guildId;
        this.worldId = builder.worldId;
        this.personaId = builder.personaId;
        this.gameMode = builder.gameMode;
        this.modelConfiguration = builder.modelConfiguration;
        this.moderation = builder.moderation;
        this.messageHistory = Collections.unmodifiableList(builder.messageHistory);
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getBotId() {
        return botId;
    }

    public String getBotUsername() {
        return botUsername;
    }

    public String getBotNickname() {
        return botNickname;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getGuildId() {
        return guildId;
    }

    public String getWorldId() {
        return worldId;
    }

    public String getPersonaId() {
        return personaId;
    }

    public String getGameMode() {
        return gameMode;
    }

    public ModelConfigurationRequest getModelConfiguration() {
        return modelConfiguration;
    }

    public ModerationConfigurationRequest getModeration() {
        return moderation;
    }

    public List<DiscordMessageData> getMessageHistory() {
        return messageHistory;
    }

    public static final class Builder {

        private String botId;
        private String botUsername;
        private String botNickname;
        private String channelId;
        private String guildId;
        private String worldId;
        private String personaId;
        private String gameMode;
        private ModelConfigurationRequest modelConfiguration;
        private ModerationConfigurationRequest moderation;
        private List<DiscordMessageData> messageHistory = new ArrayList<>();

        private Builder() {
        }

        public Builder channelId(String channelId) {
            this.channelId = channelId;
            return this;
        }

        public Builder guildId(String guildId) {
            this.guildId = guildId;
            return this;
        }

        public Builder botUsername(String botUsername) {
            this.botUsername = botUsername;
            return this;
        }

        public Builder botId(String botId) {
            this.botId = botId;
            return this;
        }

        public Builder worldId(String worldId) {
            this.worldId = worldId;
            return this;
        }

        public Builder personaId(String personaId) {
            this.personaId = personaId;
            return this;
        }

        public Builder gameMode(String gameMode) {
            this.gameMode = gameMode;
            return this;
        }

        public Builder modelConfiguration(ModelConfigurationRequest modelConfiguration) {
            this.modelConfiguration = modelConfiguration;
            return this;
        }

        public Builder moderation(ModerationConfigurationRequest moderation) {
            this.moderation = moderation;
            return this;
        }

        public Builder botNickname(String botNickname) {
            this.botNickname = botNickname;
            return this;
        }

        public Builder messageHistory(List<DiscordMessageData> messageHistory) {

            if (CollectionUtils.isNotEmpty(messageHistory)) {
                this.messageHistory.addAll(messageHistory);
            }

            return this;
        }

        public StoryGenerationRequest build() {
            return new StoryGenerationRequest(this);
        }
    }
}
