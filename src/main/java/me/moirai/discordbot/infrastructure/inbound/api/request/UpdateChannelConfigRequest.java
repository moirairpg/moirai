package me.moirai.discordbot.infrastructure.inbound.api.request;

import java.util.List;
import java.util.Map;

public class UpdateChannelConfigRequest {

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
    private List<String> stopSequencesToAdd;
    private List<String> stopSequencesToRemove;
    private Map<String, Double> logitBiasToAdd;
    private List<String> logitBiasToRemove;
    private List<String> usersAllowedToWriteToAdd;
    private List<String> usersAllowedToWriteToRemove;
    private List<String> usersAllowedToReadToAdd;
    private List<String> usersAllowedToReadToRemove;

    public UpdateChannelConfigRequest() {
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

    public void setName(String name) {
        this.name = name;
    }

    public void setGameMode(String gameMode) {
        this.gameMode = gameMode;
    }

    public void setWorldId(String worldId) {
        this.worldId = worldId;
    }

    public void setPersonaId(String personaId) {
        this.personaId = personaId;
    }

    public void setDiscordChannelId(String discordChannelId) {
        this.discordChannelId = discordChannelId;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public void setAiModel(String aiModel) {
        this.aiModel = aiModel;
    }

    public void setModeration(String moderation) {
        this.moderation = moderation;
    }

    public void setMaxTokenLimit(Integer maxTokenLimit) {
        this.maxTokenLimit = maxTokenLimit;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public void setFrequencyPenalty(Double frequencyPenalty) {
        this.frequencyPenalty = frequencyPenalty;
    }

    public void setPresencePenalty(Double presencePenalty) {
        this.presencePenalty = presencePenalty;
    }

    public void setStopSequencesToAdd(List<String> stopSequencesToAdd) {
        this.stopSequencesToAdd = stopSequencesToAdd;
    }

    public void setStopSequencesToRemove(List<String> stopSequencesToRemove) {
        this.stopSequencesToRemove = stopSequencesToRemove;
    }

    public void setLogitBiasToAdd(Map<String, Double> logitBiasToAdd) {
        this.logitBiasToAdd = logitBiasToAdd;
    }

    public void setLogitBiasToRemove(List<String> logitBiasToRemove) {
        this.logitBiasToRemove = logitBiasToRemove;
    }

    public void setUsersAllowedToWriteToAdd(List<String> usersAllowedToWriteToAdd) {
        this.usersAllowedToWriteToAdd = usersAllowedToWriteToAdd;
    }

    public void setUsersAllowedToWriteToRemove(List<String> usersAllowedToWriteToRemove) {
        this.usersAllowedToWriteToRemove = usersAllowedToWriteToRemove;
    }

    public void setUsersAllowedToReadToAdd(List<String> usersAllowedToReadToAdd) {
        this.usersAllowedToReadToAdd = usersAllowedToReadToAdd;
    }

    public void setUsersAllowedToReadToRemove(List<String> usersAllowedToReadToRemove) {
        this.usersAllowedToReadToRemove = usersAllowedToReadToRemove;
    }
}
