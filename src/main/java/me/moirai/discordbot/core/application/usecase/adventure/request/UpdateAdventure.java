package me.moirai.discordbot.core.application.usecase.adventure.request;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;

import me.moirai.discordbot.common.usecases.UseCase;
import me.moirai.discordbot.core.application.usecase.adventure.result.UpdateAdventureResult;

public final class UpdateAdventure extends UseCase<UpdateAdventureResult> {

    private final String id;
    private final String description;
    private final String adventureStart;
    private final String name;
    private final String worldId;
    private final String personaId;
    private final String discordChannelId;
    private final String visibility;
    private final String aiModel;
    private final String moderation;
    private final String requesterDiscordId;
    private final String gameMode;
    private final String nudge;
    private final String remember;
    private final String authorsNote;
    private final String bump;
    private final Integer bumpFrequency;
    private final Integer maxTokenLimit;
    private final Double temperature;
    private final Double frequencyPenalty;
    private final Double presencePenalty;
    private final Map<String, Double> logitBiasToAdd;
    private final List<String> stopSequencesToAdd;
    private final List<String> stopSequencesToRemove;
    private final List<String> logitBiasToRemove;
    private final List<String> usersAllowedToWriteToAdd;
    private final List<String> usersAllowedToWriteToRemove;
    private final List<String> usersAllowedToReadToAdd;
    private final List<String> usersAllowedToReadToRemove;
    private final boolean isMultiplayer;

    private UpdateAdventure(Builder builder) {

        this.id = builder.id;
        this.name = builder.name;
        this.adventureStart = builder.adventureStart;
        this.description = builder.description;
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
        this.isMultiplayer = builder.isMultiplayer;
        this.nudge = builder.nudge;
        this.remember = builder.remember;
        this.authorsNote = builder.authorsNote;
        this.bump = builder.bump;
        this.bumpFrequency = builder.bumpFrequency;

        this.logitBiasToAdd = MapUtils.isEmpty(builder.logitBiasToAdd) ? emptyMap()
                : unmodifiableMap(builder.logitBiasToAdd);

        this.logitBiasToRemove = isEmpty(builder.logitBiasToRemove) ? emptyList()
                : unmodifiableList(builder.logitBiasToRemove);

        this.stopSequencesToAdd = isEmpty(builder.stopSequencesToAdd) ? emptyList()
                : unmodifiableList(builder.stopSequencesToAdd);

        this.stopSequencesToRemove = isEmpty(builder.stopSequencesToRemove) ? emptyList()
                : unmodifiableList(builder.stopSequencesToRemove);

        this.usersAllowedToWriteToAdd = isEmpty(builder.usersAllowedToWriteToAdd) ? emptyList()
                : unmodifiableList(builder.usersAllowedToWriteToAdd);

        this.usersAllowedToWriteToRemove = isEmpty(builder.usersAllowedToWriteToRemove) ? emptyList()
                : unmodifiableList(builder.usersAllowedToWriteToRemove);

        this.usersAllowedToReadToAdd = isEmpty(builder.usersAllowedToReadToAdd) ? emptyList()
                : unmodifiableList(builder.usersAllowedToReadToAdd);

        this.usersAllowedToReadToRemove = isEmpty(builder.usersAllowedToReadToRemove) ? emptyList()
                : unmodifiableList(builder.usersAllowedToReadToRemove);
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

    public String getAdventureStart() {
        return adventureStart;
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

    public boolean isMultiplayer() {
        return isMultiplayer;
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

    public String getNudge() {
        return nudge;
    }

    public String getRemember() {
        return remember;
    }

    public String getAuthorsNote() {
        return authorsNote;
    }

    public String getBump() {
        return bump;
    }

    public Integer getBumpFrequency() {
        return bumpFrequency;
    }

    public String getDescription() {
        return description;
    }

    public static final class Builder {

        private String id;
        private String name;
        private String description;
        private String adventureStart;
        private String worldId;
        private String personaId;
        private String discordChannelId;
        private String visibility;
        private String aiModel;
        private String moderation;
        private String gameMode;
        private String requesterDiscordId;
        private String nudge;
        private String remember;
        private String authorsNote;
        private String bump;
        private Integer bumpFrequency;
        private Double temperature;
        private Double frequencyPenalty;
        private Double presencePenalty;
        private Integer maxTokenLimit;
        private Map<String, Double> logitBiasToAdd;
        private List<String> logitBiasToRemove;
        private List<String> stopSequencesToAdd;
        private List<String> stopSequencesToRemove;
        private List<String> usersAllowedToWriteToAdd;
        private List<String> usersAllowedToWriteToRemove;
        private List<String> usersAllowedToReadToAdd;
        private List<String> usersAllowedToReadToRemove;
        private boolean isMultiplayer;

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

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder adventureStart(String adventureStart) {
            this.adventureStart = adventureStart;
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

        public Builder nudge(String nudge) {
            this.nudge = nudge;
            return this;
        }

        public Builder remember(String remember) {
            this.remember = remember;
            return this;
        }

        public Builder authorsNote(String authorsNote) {
            this.authorsNote = authorsNote;
            return this;
        }

        public Builder bump(String bump) {
            this.bump = bump;
            return this;
        }

        public Builder bumpFrequency(Integer bumpFrequency) {
            this.bumpFrequency = bumpFrequency;
            return this;
        }

        public Builder isMultiplayer(boolean isMultiplayer) {
            this.isMultiplayer = isMultiplayer;
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

        public UpdateAdventure build() {
            return new UpdateAdventure(this);
        }
    }
}