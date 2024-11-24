package me.moirai.discordbot.core.domain.channelconfig;

import java.time.OffsetDateTime;

import org.apache.commons.lang3.StringUtils;

import me.moirai.discordbot.common.exception.BusinessRuleViolationException;
import me.moirai.discordbot.core.domain.Permissions;
import me.moirai.discordbot.core.domain.ShareableAsset;
import me.moirai.discordbot.core.domain.Visibility;

public class ChannelConfig extends ShareableAsset {

    private String id;
    private String name;
    private String worldId;
    private String personaId;
    private String discordChannelId;
    private ModelConfiguration modelConfiguration;
    private Moderation moderation;
    private GameMode gameMode;
    private boolean isMultiplayer;

    private ChannelConfig(Builder builder) {

        super(builder.creatorDiscordId, builder.creationDate,
                builder.lastUpdateDate, builder.permissions, builder.visibility, builder.version);

        this.id = builder.id;
        this.name = builder.name;
        this.worldId = builder.worldId;
        this.personaId = builder.personaId;
        this.discordChannelId = builder.discordChannelId;
        this.modelConfiguration = builder.modelConfiguration;
        this.moderation = builder.moderation;
        this.gameMode = builder.gameMode;
        this.isMultiplayer = builder.isMultiplayer;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getWorldId() {
        return worldId;
    }

    public String getPersonaId() {
        return personaId;
    }

    public String getDiscordChannelId() {
        return discordChannelId;
    }

    public ModelConfiguration getModelConfiguration() {
        return modelConfiguration;
    }

    public Moderation getModeration() {
        return moderation;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public boolean isMultiplayer() {
        return isMultiplayer;
    }

    public static Builder builder() {

        return new Builder();
    }

    public void updateName(String name) {

        this.name = name;
    }

    public void updatePersona(String personaId) {

        this.personaId = personaId;
    }

    public void updateWorld(String worldId) {

        this.worldId = worldId;
    }

    public void updateDiscordChannel(String discordChannelId) {

        this.discordChannelId = discordChannelId;
    }

    public void updateModeration(Moderation moderation) {

        this.moderation = moderation;
    }

    public void updateGameMode(GameMode gameMode) {

        this.gameMode = gameMode;
    }

    public void updateAiModel(ArtificialIntelligenceModel aiModel) {

        ModelConfiguration newModelConfiguration = this.modelConfiguration.updateAiModel(aiModel);
        this.modelConfiguration = newModelConfiguration;
    }

    public void updateMaxTokenLimit(int maxTokenLimit) {

        ModelConfiguration newModelConfiguration = this.modelConfiguration.updateMaxTokenLimit(maxTokenLimit);
        this.modelConfiguration = newModelConfiguration;
    }

    public void updateTemperature(double temperature) {

        ModelConfiguration newModelConfiguration = this.modelConfiguration.updateTemperature(temperature);
        this.modelConfiguration = newModelConfiguration;
    }

    public void updateFrequencyPenalty(double frequencyPenalty) {

        ModelConfiguration newModelConfiguration = this.modelConfiguration.updateFrequencyPenalty(frequencyPenalty);
        this.modelConfiguration = newModelConfiguration;
    }

    public void updatePresencePenalty(double presencePenalty) {

        ModelConfiguration newModelConfiguration = this.modelConfiguration.updatePresencePenalty(presencePenalty);
        this.modelConfiguration = newModelConfiguration;
    }

    public void addStopSequence(String stopSequence) {

        ModelConfiguration newModelConfiguration = this.modelConfiguration.addStopSequence(stopSequence);
        this.modelConfiguration = newModelConfiguration;
    }

    public void removeStopSequence(String stopSequence) {

        ModelConfiguration newModelConfiguration = this.modelConfiguration.removeStopSequence(stopSequence);
        this.modelConfiguration = newModelConfiguration;
    }

    public void addLogitBias(String token, double bias) {

        ModelConfiguration newModelConfiguration = this.modelConfiguration.addLogitBias(token, bias);
        this.modelConfiguration = newModelConfiguration;
    }

    public void removeLogitBias(String token) {

        ModelConfiguration newModelConfiguration = this.modelConfiguration.removeLogitBias(token);
        this.modelConfiguration = newModelConfiguration;
    }

    public void makeMultiplayer() {
        this.isMultiplayer = true;
    }

    public void makeSinglePlayer() {
        this.isMultiplayer = false;
    }

    public static final class Builder {

        private String id;
        private String name;
        private String worldId;
        private String personaId;
        private String discordChannelId;
        private boolean isMultiplayer;
        private GameMode gameMode;
        private ModelConfiguration modelConfiguration;
        private Moderation moderation;
        private Visibility visibility;
        private Permissions permissions;
        private String creatorDiscordId;
        private OffsetDateTime creationDate;
        private OffsetDateTime lastUpdateDate;
        private int version;

        private Builder() {
        }

        public Builder id(String id) {

            this.id = id;
            return this;
        }

        public Builder name(String name) {

            this.name = name;
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

        public Builder gameMode(GameMode gameMode) {

            this.gameMode = gameMode;
            return this;
        }

        public Builder isMultiplayer(boolean isMultiplayer) {

            this.isMultiplayer = isMultiplayer;
            return this;
        }

        public Builder discordChannelId(String discordChannelId) {

            this.discordChannelId = discordChannelId;
            return this;
        }

        public Builder modelConfiguration(ModelConfiguration modelConfiguration) {

            this.modelConfiguration = modelConfiguration;
            return this;
        }

        public Builder moderation(Moderation moderation) {

            this.moderation = moderation;
            return this;
        }

        public Builder visibility(Visibility visibility) {

            this.visibility = visibility;
            return this;
        }

        public Builder permissions(Permissions permissions) {

            this.permissions = permissions;
            return this;
        }

        public Builder creatorDiscordId(String creatorDiscordId) {

            this.creatorDiscordId = creatorDiscordId;
            return this;
        }

        public Builder creationDate(OffsetDateTime creationDate) {

            this.creationDate = creationDate;
            return this;
        }

        public Builder lastUpdateDate(OffsetDateTime lastUpdateDate) {

            this.lastUpdateDate = lastUpdateDate;
            return this;
        }

        public Builder version(int version) {

            this.version = version;
            return this;
        }

        public ChannelConfig build() {

            if (StringUtils.isBlank(name)) {
                throw new BusinessRuleViolationException("Channel config name cannot be null or empty");
            }

            if (StringUtils.isBlank(discordChannelId)) {
                throw new BusinessRuleViolationException("Discord channel ID cannot be null or empty");
            }

            if (modelConfiguration == null) {
                throw new BusinessRuleViolationException("Model configuration cannot be null");
            }

            if (moderation == null) {
                throw new BusinessRuleViolationException("Moderation cannot be null");
            }

            if (visibility == null) {
                throw new BusinessRuleViolationException("Visibility cannot be null");
            }

            if (permissions == null) {
                throw new BusinessRuleViolationException("Permissions cannot be null");
            }

            if (gameMode == null) {
                throw new BusinessRuleViolationException("Game Mode cannot be null");
            }

            return new ChannelConfig(this);
        }
    }
}
