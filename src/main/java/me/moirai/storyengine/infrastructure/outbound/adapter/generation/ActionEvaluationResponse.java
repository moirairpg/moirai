package me.moirai.storyengine.infrastructure.outbound.adapter.generation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
class ActionEvaluationResponse {

    @JsonProperty("verdict")
    private String verdict;

    @JsonProperty("attribute")
    private String attribute;

    @JsonProperty("skill")
    private String skill;

    @JsonProperty("difficulty")
    private String difficulty;

    @JsonProperty("reason")
    private String reason;

    public ActionEvaluationResponse() {
    }

    public String getVerdict() {
        return verdict;
    }

    public String getAttribute() {
        return attribute;
    }

    public String getSkill() {
        return skill;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getReason() {
        return reason;
    }

    public void setVerdict(String verdict) {
        this.verdict = verdict;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
