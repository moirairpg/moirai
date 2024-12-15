package me.moirai.discordbot.core.application.usecase.adventure.request;

import me.moirai.discordbot.common.usecases.UseCase;

public final class UpdateAdventureAuthorsNoteByChannelId extends UseCase<Void> {

    private final String authorsNote;
    private final String channelId;

    private UpdateAdventureAuthorsNoteByChannelId(String authorsNote, String channelId) {
        this.authorsNote = authorsNote;
        this.channelId = channelId;
    }

    public static UpdateAdventureAuthorsNoteByChannelId build(String authorsNote, String channelId) {
        return new UpdateAdventureAuthorsNoteByChannelId(authorsNote, channelId);
    }

    public String getAuthorsNote() {
        return authorsNote;
    }

    public String getChannelId() {
        return channelId;
    }
}