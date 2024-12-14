package me.moirai.discordbot.infrastructure.inbound.api.request;

public class UpdateLorebookEntryRequest {

    private String name;
    private String regex;
    private String description;
    private String playerDiscordId;
    private boolean isPlayerCharacter;

    public UpdateLorebookEntryRequest() {
    }

    public String getName() {
        return name;
    }

    public String getRegex() {
        return regex;
    }

    public String getDescription() {
        return description;
    }

    public String getPlayerDiscordId() {
        return playerDiscordId;
    }

    public boolean isPlayerCharacter() {
        return isPlayerCharacter;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPlayerDiscordId(String playerDiscordId) {
        this.playerDiscordId = playerDiscordId;
    }

    public void setIsPlayerCharacter(boolean isPlayerCharacter) {
        this.isPlayerCharacter = isPlayerCharacter;
    }
}