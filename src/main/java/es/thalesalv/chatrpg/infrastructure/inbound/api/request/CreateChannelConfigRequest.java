package es.thalesalv.chatrpg.infrastructure.inbound.api.request;

import java.util.List;
import java.util.Map;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class CreateChannelConfigRequest {

    @NotEmpty(message = "cannot be empty")
    @NotNull(message = "cannot be null")
    private String name;

    @NotEmpty(message = "cannot be empty")
    @NotNull(message = "cannot be null")
    private String worldId;

    @NotEmpty(message = "cannot be empty")
    @NotNull(message = "cannot be null")
    private String personaId;

    @NotEmpty(message = "cannot be empty")
    @NotNull(message = "cannot be null")
    private String discordChannelId;

    @NotEmpty(message = "cannot be empty")
    @NotNull(message = "cannot be null")
    private String visibility;

    @NotEmpty(message = "cannot be empty")
    @NotNull(message = "cannot be null")
    private String aiModel;

    @NotEmpty(message = "cannot be empty")
    @NotNull(message = "cannot be null")
    private String moderation;

    @NotNull(message = "cannot be null")
    @Min(value = 100, message = "cannot be less than 100")
    private Integer maxTokenLimit;

    @NotNull(message = "cannot be null")
    @DecimalMin(value = "0.1", message = "cannot be less than 0.1")
    @DecimalMax(value = "2", message = "cannot be greater than 2")
    private Double temperature;

    @DecimalMin(value = "-2", message = "cannot be less than -2")
    @DecimalMax(value = "2", message = "cannot be greater than 2")
    private Double frequencyPenalty;

    @DecimalMin(value = "-2", message = "cannot be less than -2")
    @DecimalMax(value = "2", message = "cannot be greater than 2")
    private Double presencePenalty;

    private List<String> stopSequences;
    private Map<String, Double> logitBias;
    private List<String> usersAllowedToWrite;
    private List<String> usersAllowedToRead;

    public CreateChannelConfigRequest() {
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

    public void setName(String name) {
        this.name = name;
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

    public void setStopSequences(List<String> stopSequences) {
        this.stopSequences = stopSequences;
    }

    public void setLogitBias(Map<String, Double> logitBias) {
        this.logitBias = logitBias;
    }

    public void setUsersAllowedToWrite(List<String> usersAllowedToWrite) {
        this.usersAllowedToWrite = usersAllowedToWrite;
    }

    public void setUsersAllowedToRead(List<String> usersAllowedToRead) {
        this.usersAllowedToRead = usersAllowedToRead;
    }
}
