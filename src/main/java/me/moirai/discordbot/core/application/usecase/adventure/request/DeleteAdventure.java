package me.moirai.discordbot.core.application.usecase.adventure.request;

import me.moirai.discordbot.common.usecases.UseCase;

public final class DeleteAdventure extends UseCase<Void> {

    private final String id;
    private final String requesterDiscordId;

    private DeleteAdventure(String id, String requesterDiscordId) {
        this.id = id;
        this.requesterDiscordId = requesterDiscordId;
    }

    public static DeleteAdventure build(String id, String requesterDiscordId) {

        return new DeleteAdventure(id, requesterDiscordId);
    }

    public String getId() {
        return id;
    }

    public String getRequesterDiscordId() {
        return requesterDiscordId;
    }
}
