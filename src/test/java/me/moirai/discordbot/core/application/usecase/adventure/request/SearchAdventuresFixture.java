package me.moirai.discordbot.core.application.usecase.adventure.request;

public class SearchAdventuresFixture {

    public static SearchAdventures.Builder writeAccess() {

        return SearchAdventures.builder()
                .name("Adventure")
                .favorites(false)
                .gameMode("RPG")
                .model("GPT3_TURBO")
                .moderation("PERMISSIVE")
                .multiplayer(false)
                .operation("WRITE")
                .ownerDiscordId("1234")
                .page(1)
                .persona("1234")
                .requesterDiscordId("1234")
                .size(10)
                .sortingField("name")
                .visibility("PUBLIC")
                .world("1234");
    }

    public static SearchAdventures.Builder readAccess() {

        return SearchAdventures.builder()
                .name("Adventure")
                .favorites(false)
                .gameMode("RPG")
                .model("GPT3_TURBO")
                .moderation("PERMISSIVE")
                .multiplayer(false)
                .operation("READ")
                .ownerDiscordId("1234")
                .page(1)
                .persona("1234")
                .requesterDiscordId("1234")
                .size(10)
                .sortingField("name")
                .visibility("PUBLIC")
                .world("1234");
    }

    public static SearchAdventures.Builder favorites() {

        return SearchAdventures.builder()
                .name("Adventure")
                .favorites(true)
                .gameMode("RPG")
                .model("GPT3_TURBO")
                .moderation("PERMISSIVE")
                .multiplayer(false)
                .operation("READ")
                .ownerDiscordId("1234")
                .page(1)
                .persona("1234")
                .requesterDiscordId("1234")
                .size(10)
                .sortingField("name")
                .visibility("PUBLIC")
                .world("1234");
    }
}
