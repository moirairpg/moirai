package es.thalesalv.chatrpg.core.application.usecase.channelconfig.request;

import java.util.List;
import java.util.Map;

import es.thalesalv.chatrpg.common.usecases.UseCase;
import es.thalesalv.chatrpg.core.application.usecase.channelconfig.result.UpdateChannelConfigResult;

public final class UpdateChannelConfig extends UseCase<UpdateChannelConfigResult> {

    private final String id;
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
    private final List<String> stopSequencesToAdd;
    private final List<String> stopSequencesToRemove;
    private final Map<String, Double> logitBiasToAdd;
    private final List<String> logitBiasToRemove;
    private final List<String> usersAllowedToWriteToAdd;
    private final List<String> usersAllowedToWriteToRemove;
    private final List<String> usersAllowedToReadToAdd;
    private final List<String> usersAllowedToReadToRemove;
    private final String requesterDiscordId;

    private UpdateChannelConfig(Builder builder) {
        this.id = builder.id;
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
        this.stopSequencesToAdd = builder.stopSequencesToAdd;
        this.stopSequencesToRemove = builder.stopSequencesToRemove;
        this.logitBiasToAdd = builder.logitBiasToAdd;
        this.logitBiasToRemove = builder.logitBiasToRemove;
        this.usersAllowedToWriteToAdd = builder.usersAllowedToWriteToAdd;
        this.usersAllowedToWriteToRemove = builder.usersAllowedToWriteToRemove;
        this.usersAllowedToReadToAdd = builder.usersAllowedToReadToAdd;
        this.usersAllowedToReadToRemove = builder.usersAllowedToReadToRemove;
        this.requesterDiscordId = builder.requesterDiscordId;
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

    public List<String> getStopSequencesToAdd() {
        return stopSequencesToAdd;
    }

    public List<String> getStopSequencesToRemove() {
        return stopSequencesToRemove;
    }

    public Map<String, Double> getLogitBiasToAdd() {
        return logitBiasToAdd;
    }

    public List<String> getLogitBiasToRemove() {
        return logitBiasToRemove;
    }

    public List<String> getUsersAllowedToWriteToAdd() {
        return usersAllowedToWriteToAdd;
    }

    public List<String> getUsersAllowedToWriteToRemove() {
        return usersAllowedToWriteToRemove;
    }

    public List<String> getUsersAllowedToReadToAdd() {
        return usersAllowedToReadToAdd;
    }

    public List<String> getUsersAllowedToReadToRemove() {
        return usersAllowedToReadToRemove;
    }

    public String getRequesterDiscordId() {
        return requesterDiscordId;
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
        private Integer maxTokenLimit;
        private Double temperature;
        private Double frequencyPenalty;
        private Double presencePenalty;
        private List<String> stopSequencesToAdd;
        private List<String> stopSequencesToRemove;
        private Map<String, Double> logitBiasToAdd;
        private List<String> logitBiasToRemove;
        private List<String> usersAllowedToWriteToAdd;
        private List<String> usersAllowedToWriteToRemove;
        private List<String> usersAllowedToReadToAdd;
        private List<String> usersAllowedToReadToRemove;
        private String requesterDiscordId;

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

        public Builder stopSequencesToAdd(List<String> stopSequencesToAdd) {
            this.stopSequencesToAdd = stopSequencesToAdd;
            return this;
        }

        public Builder stopSequencesToRemove(List<String> stopSequencesToRemove) {
            this.stopSequencesToRemove = stopSequencesToRemove;
            return this;
        }

        public Builder logitBiasToAdd(Map<String, Double> logitBiasToAdd) {
            this.logitBiasToAdd = logitBiasToAdd;
            return this;
        }

        public Builder logitBiasToRemove(List<String> logitBiasToRemove) {
            this.logitBiasToRemove = logitBiasToRemove;
            return this;
        }

        public Builder usersAllowedToWriteToAdd(List<String> usersAllowedToWriteToAdd) {
            this.usersAllowedToWriteToAdd = usersAllowedToWriteToAdd;
            return this;
        }

        public Builder usersAllowedToWriteToRemove(List<String> usersAllowedToWriteToRemove) {
            this.usersAllowedToWriteToRemove = usersAllowedToWriteToRemove;
            return this;
        }

        public Builder usersAllowedToReadToAdd(List<String> usersAllowedToReadToAdd) {
            this.usersAllowedToReadToAdd = usersAllowedToReadToAdd;
            return this;
        }

        public Builder usersAllowedToReadToRemove(List<String> usersAllowedToReadToRemove) {
            this.usersAllowedToReadToRemove = usersAllowedToReadToRemove;
            return this;
        }

        public Builder requesterDiscordId(String requesterDiscordId) {
            this.requesterDiscordId = requesterDiscordId;
            return this;
        }

        public UpdateChannelConfig build() {
            return new UpdateChannelConfig(this);
        }
    }
}