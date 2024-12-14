package me.moirai.discordbot.infrastructure.outbound.persistence.adventure;

public class AdventureLorebookEntryEntityFixture {

    public static AdventureLorebookEntryEntity.Builder sampleLorebookEntry() {

        AdventureLorebookEntryEntity.Builder builder = AdventureLorebookEntryEntity.builder();
        builder.id("857345HAA");
        builder.name("White River");
        builder.description("The White River goes through Falkreath, Whiterun and ends in Eastmarch.");
        builder.regex("[Ww]hite [Rr]iver");
        builder.adventureId("WRLDID");
        builder.creatorDiscordId("CRTRID");

        return builder;
    }

    public static AdventureLorebookEntryEntity.Builder samplePlayerCharacterLorebookEntry() {

        AdventureLorebookEntryEntity.Builder builder = AdventureLorebookEntryEntity.builder();
        builder.id("45534453");
        builder.name("Volin Habar");
        builder.description("Volin Habar is a warrior that fights with a sword.");
        builder.regex("[Vv]olin [Hh]abar|[Vv]oha");
        builder.playerDiscordId("2423423423423");
        builder.adventureId("WRLDID");
        builder.creatorDiscordId("CRTRID");

        return builder;
    }
}
