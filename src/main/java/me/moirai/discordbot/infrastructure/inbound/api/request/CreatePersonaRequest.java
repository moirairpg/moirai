package me.moirai.discordbot.infrastructure.inbound.api.request;

import java.util.List;

public class CreatePersonaRequest {

    private String name;
    private String personality;
    private String nudgeRole;
    private String nudgeContent;
    private String bumpRole;
    private String bumpContent;
    private String visibility;
    private Integer bumpFrequency;
    private List<String> usersAllowedToWrite;
    private List<String> usersAllowedToRead;

    public CreatePersonaRequest() {
    }

    public String getName() {
        return name;
    }

    public String getPersonality() {
        return personality;
    }

    public String getNudgeRole() {
        return nudgeRole;
    }

    public String getNudgeContent() {
        return nudgeContent;
    }

    public String getBumpRole() {
        return bumpRole;
    }

    public String getBumpContent() {
        return bumpContent;
    }

    public String getVisibility() {
        return visibility;
    }

    public Integer getBumpFrequency() {
        return bumpFrequency;
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

    public void setPersonality(String personality) {
        this.personality = personality;
    }

    public void setNudgeRole(String nudgeRole) {
        this.nudgeRole = nudgeRole;
    }

    public void setNudgeContent(String nudgeContent) {
        this.nudgeContent = nudgeContent;
    }

    public void setBumpRole(String bumpRole) {
        this.bumpRole = bumpRole;
    }

    public void setBumpContent(String bumpContent) {
        this.bumpContent = bumpContent;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public void setBumpFrequency(Integer bumpFrequency) {
        this.bumpFrequency = bumpFrequency;
    }

    public void setUsersAllowedToWrite(List<String> usersAllowedToWrite) {
        this.usersAllowedToWrite = usersAllowedToWrite;
    }

    public void setUsersAllowedToRead(List<String> usersAllowedToRead) {
        this.usersAllowedToRead = usersAllowedToRead;
    }
}
