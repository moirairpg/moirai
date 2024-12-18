package me.moirai.discordbot.infrastructure.inbound.api.request;

import java.util.List;

public class UpdatePersonaRequest {

    private String name;
    private String personality;
    private String visibility;
    private List<String> usersAllowedToWriteToAdd;
    private List<String> usersAllowedToWriteToRemove;
    private List<String> usersAllowedToReadToAdd;
    private List<String> usersAllowedToReadToRemove;

    public UpdatePersonaRequest() {
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

    public void setPersonality(String personality) {
        this.personality = personality;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
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