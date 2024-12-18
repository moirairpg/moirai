package me.moirai.discordbot.infrastructure.inbound.api.request;

import java.util.List;

public class CreatePersonaRequest {

    private String name;
    private String personality;
    private String visibility;
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

    public String getVisibility() {
        return visibility;
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

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public void setUsersAllowedToWrite(List<String> usersAllowedToWrite) {
        this.usersAllowedToWrite = usersAllowedToWrite;
    }

    public void setUsersAllowedToRead(List<String> usersAllowedToRead) {
        this.usersAllowedToRead = usersAllowedToRead;
    }
}
