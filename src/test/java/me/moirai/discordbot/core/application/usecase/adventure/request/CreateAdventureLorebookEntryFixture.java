package me.moirai.discordbot.core.application.usecase.adventure.request;

public class CreateAdventureLorebookEntryFixture {

    public static CreateAdventureLorebookEntry.Builder sampleLorebookEntry() {

        CreateAdventureLorebookEntry.Builder builder = CreateAdventureLorebookEntry.builder();
        builder.name("White River");
        builder.description("The White River goes through Falkreath, Whiterun and ends in Eastmarch.");
        builder.regex("[Ww]hite [Rr]iver");

        return builder;
    }

    public static CreateAdventureLorebookEntry.Builder samplePlayerCharacterLorebookEntry() {

        CreateAdventureLorebookEntry.Builder builder = CreateAdventureLorebookEntry.builder();
        builder.name("Volin Habar");
        builder.description("Volin Habar is a warrior that fights with a sword.");
        builder.regex("[Vv]olin [Hh]abar|[Vv]oha");
        builder.playerDiscordId("2423423423423");

        return builder;
    }
}
