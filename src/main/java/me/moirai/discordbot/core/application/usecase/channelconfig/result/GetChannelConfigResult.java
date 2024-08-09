package me.moirai.discordbot.core.application.usecase.channelconfig.result;

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class GetChannelConfigResult {

    private final String id;
    private final String name;
    private final String worldId;
    private final String personaId;
    private final String discordChannelId;
    private final String visibility;
    private final String aiModel;
    private final String moderation;
    private final String gameMode;
    private final int maxTokenLimit;
    private final int messageHistorySize;
    private final double temperature;
    private final double frequencyPenalty;
    private final double presencePenalty;
    private final String ownerDiscordId;
    private final OffsetDateTime creationDate;
    private final OffsetDateTime lastUpdateDate;
    private final Map<String, Double> logitBias;
    private final List<String> stopSequences;
    private final List<String> usersAllowedToRead;
    private final List<String> usersAllowedToWrite;

    private GetChannelConfigResult(Builder builder) {

        this.id = builder.id;
        this.name = builder.name;
        this.worldId = builder.worldId;
        this.personaId = builder.personaId;
        this.discordChannelId = builder.discordChannelId;
        this.visibility = builder.visibility;
        this.aiModel = builder.aiModel;
        this.moderation = builder.moderation;
        this.gameMode = builder.gameMode;
        this.maxTokenLimit = builder.maxTokenLimit;
        this.messageHistorySize = builder.messageHistorySize;
        this.temperature = builder.temperature;
        this.frequencyPenalty = builder.frequencyPenalty;
        this.presencePenalty = builder.presencePenalty;
        this.ownerDiscordId = builder.ownerDiscordId;
        this.creationDate = builder.creationDate;
        this.lastUpdateDate = builder.lastUpdateDate;
        this.logitBias = unmodifiableMap(builder.logitBias);
        this.stopSequences = unmodifiableList(builder.stopSequences);
        this.usersAllowedToRead = unmodifiableList(builder.usersAllowedToRead);
        this.usersAllowedToWrite = unmodifiableList(builder.usersAllowedToWrite);
    }

    public static Builder builder() {
        return new Builder();
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

    public String getVisibility() {
        return visibility;
    }

    public String getAiModel() {
        return aiModel;
    }

    public String getModeration() {
        return moderation;
    }

    public String getGameMode() {
        return gameMode;
    }

    public int getMaxTokenLimit() {
        return maxTokenLimit;
    }

    public int getMessageHistorySize() {
        return messageHistorySize;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getFrequencyPenalty() {
        return frequencyPenalty;
    }

    public double getPresencePenalty() {
        return presencePenalty;
    }

    public List<String> getStopSequences() {
        return stopSequences;
    }

    public Map<String, Double> getLogitBias() {
        return logitBias;
    }

    public String getOwnerDiscordId() {
        return ownerDiscordId;
    }

    public List<String> getUsersAllowedToRead() {
        return usersAllowedToRead;
    }

    public List<String> getUsersAllowedToWrite() {
        return usersAllowedToWrite;
    }

    public OffsetDateTime getCreationDate() {
        return creationDate;
    }

    public OffsetDateTime getLastUpdateDate() {
        return lastUpdateDate;
    }

    public static final class Builder {

        private String id;
        private String name;
        private String worldId;
        private String personaId;
        private String discordChannelId;
        private String visibility;
        private String aiModel;
        private String moderation;
        private String gameMode;
        private int maxTokenLimit;
        private int messageHistorySize;
        private double temperature;
        private double frequencyPenalty;
        private double presencePenalty;
        private String ownerDiscordId;
        private OffsetDateTime creationDate;
        private OffsetDateTime lastUpdateDate;
        private Map<String, Double> logitBias = new HashMap<>();
        private List<String> stopSequences = new ArrayList<>();
        private List<String> usersAllowedToRead = new ArrayList<>();
        private List<String> usersAllowedToWrite = new ArrayList<>();

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

        public Builder discordChannelId(String discordChannelId) {
            this.discordChannelId = discordChannelId;
            return this;
        }

        public Builder visibility(String visibility) {
            this.visibility = visibility;
            return this;
        }

        public Builder aiModel(String aiModel) {
            this.aiModel = aiModel;
            return this;
        }

        public Builder moderation(String moderation) {
            this.moderation = moderation;
            return this;
        }

        public Builder gameMode(String gameMode) {
            this.gameMode = gameMode;
            return this;
        }

        public Builder maxTokenLimit(int maxTokenLimit) {
            this.maxTokenLimit = maxTokenLimit;
            return this;
        }

        public Builder messageHistorySize(int messageHistorySize) {
            this.messageHistorySize = messageHistorySize;
            return this;
        }

        public Builder temperature(double temperature) {
            this.temperature = temperature;
            return this;
        }

        public Builder frequencyPenalty(double frequencyPenalty) {
            this.frequencyPenalty = frequencyPenalty;
            return this;
        }

        public Builder presencePenalty(double presencePenalty) {
            this.presencePenalty = presencePenalty;
            return this;
        }

        public Builder stopSequences(List<String> stopSequences) {

            if (stopSequences != null) {
                this.stopSequences = stopSequences;
            }

            return this;
        }

        public Builder logitBias(Map<String, Double> logitBias) {

            if (logitBias != null) {
                this.logitBias = logitBias;
            }

            return this;
        }

        public Builder ownerDiscordId(String ownerDiscordId) {
            this.ownerDiscordId = ownerDiscordId;
            return this;
        }

        public Builder usersAllowedToRead(List<String> usersAllowedToRead) {

            if (usersAllowedToRead != null) {
                this.usersAllowedToRead = usersAllowedToRead;
            }

            return this;
        }

        public Builder usersAllowedToWrite(List<String> usersAllowedToWrite) {

            if (usersAllowedToWrite != null) {
                this.usersAllowedToWrite = usersAllowedToWrite;
            }

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

        public GetChannelConfigResult build() {
            return new GetChannelConfigResult(this);
        }
    }
}