package me.moirai.discordbot.core.application.usecase.channelconfig.request;

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.moirai.discordbot.common.usecases.UseCase;
import me.moirai.discordbot.core.application.usecase.channelconfig.result.CreateChannelConfigResult;

public final class CreateChannelConfig extends UseCase<CreateChannelConfigResult> {

    private final String name;
    private final String worldId;
    private final String personaId;
    private final String discordChannelId;
    private final String visibility;
    private final String aiModel;
    private final String moderation;
    private final Integer maxTokenLimit;
    private final Double temperature;
    private final Double frequencyPenalty;
    private final Double presencePenalty;
    private final List<String> stopSequences;
    private final Map<String, Double> logitBias;
    private final List<String> usersAllowedToWrite;
    private final List<String> usersAllowedToRead;
    private final String requesterDiscordId;
    private final String gameMode;

    private CreateChannelConfig(Builder builder) {

        this.name = builder.name;
        this.worldId = builder.worldId;
        this.personaId = builder.personaId;
        this.discordChannelId = builder.discordChannelId;
        this.visibility = builder.visibility;
        this.aiModel = builder.aiModel;
        this.moderation = builder.moderation;
        this.maxTokenLimit = builder.maxTokenLimit;
        this.temperature = builder.temperature;
        this.frequencyPenalty = builder.frequencyPenalty;
        this.presencePenalty = builder.presencePenalty;
        this.requesterDiscordId = builder.requesterDiscordId;
        this.gameMode = builder.gameMode;
        this.logitBias = unmodifiableMap(builder.logitBias);
        this.stopSequences = unmodifiableList(builder.stopSequences);
        this.usersAllowedToWrite = unmodifiableList(builder.usersAllowedToWrite);
        this.usersAllowedToRead = unmodifiableList(builder.usersAllowedToRead);
    }

    public static Builder builder() {
        return new Builder();
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

    public Integer getMaxTokenLimit() {
        return maxTokenLimit;
    }

    public Double getTemperature() {
        return temperature;
    }

    public Double getFrequencyPenalty() {
        return frequencyPenalty;
    }

    public Double getPresencePenalty() {
        return presencePenalty;
    }

    public List<String> getStopSequences() {
        return stopSequences;
    }

    public Map<String, Double> getLogitBias() {
        return logitBias;
    }

    public List<String> getUsersAllowedToWrite() {
        return usersAllowedToWrite;
    }

    public List<String> getUsersAllowedToRead() {
        return usersAllowedToRead;
    }

    public String getRequesterDiscordId() {
        return requesterDiscordId;
    }

    public static final class Builder {

        private String name;
        private String worldId;
        private String personaId;
        private String discordChannelId;
        private String visibility;
        private String aiModel;
        private String moderation;
        private String gameMode;
        private Integer maxTokenLimit;
        private Double temperature;
        private Double frequencyPenalty;
        private Double presencePenalty;
        private List<String> stopSequences = new ArrayList<>();
        private Map<String, Double> logitBias = new HashMap<>();
        private List<String> usersAllowedToWrite = new ArrayList<>();
        private List<String> usersAllowedToRead = new ArrayList<>();
        private String requesterDiscordId;

        private Builder() {
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

        public Builder maxTokenLimit(Integer maxTokenLimit) {
            this.maxTokenLimit = maxTokenLimit;
            return this;
        }

        public Builder temperature(Double temperature) {
            this.temperature = temperature;
            return this;
        }

        public Builder frequencyPenalty(Double frequencyPenalty) {
            this.frequencyPenalty = frequencyPenalty;
            return this;
        }

        public Builder presencePenalty(Double presencePenalty) {
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

        public Builder usersAllowedToWrite(List<String> usersAllowedToWrite) {

            if (usersAllowedToWrite != null) {
                this.usersAllowedToWrite = usersAllowedToWrite;
            }

            return this;
        }

        public Builder usersAllowedToRead(List<String> usersAllowedToRead) {

            if (usersAllowedToRead != null) {
                this.usersAllowedToRead = usersAllowedToRead;
            }

            return this;
        }

        public Builder requesterDiscordId(String requesterDiscordId) {
            this.requesterDiscordId = requesterDiscordId;
            return this;
        }

        public CreateChannelConfig build() {
            return new CreateChannelConfig(this);
        }
    }
}