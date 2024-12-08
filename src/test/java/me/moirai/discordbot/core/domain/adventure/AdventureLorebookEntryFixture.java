package me.moirai.discordbot.core.domain.adventure;

import java.time.OffsetDateTime;

public class AdventureLorebookEntryFixture {

    public static AdventureLorebookEntry.Builder sampleLorebookEntry() {

        AdventureLorebookEntry.Builder builder = AdventureLorebookEntry.builder();
        builder.id("857345HAA");
        builder.name("White River");
        builder.description("The White River goes through Falkreath, Whiterun and ends in Eastmarch.");
        builder.regex("[Ww]hite [Rr]iver");
        builder.creatorDiscordId("CRTID");
        builder.adventureId("WRLDID");
        builder.creationDate(OffsetDateTime.now());
        builder.lastUpdateDate(OffsetDateTime.now());
        builder.isPlayerCharacter(false);
        builder.version(1);

        return builder;
    }

    public static AdventureLorebookEntry.Builder samplePlayerCharacterLorebookEntry() {

        AdventureLorebookEntry.Builder builder = AdventureLorebookEntry.builder();
        builder.id("45534453");
        builder.name("Volin Habar");
        builder.description("Volin Habar is a warrior that fights with a sword.");
        builder.regex("[Vv]olin [Hh]abar|[Vv]oha");
        builder.playerDiscordId("2423423423423");
        builder.creatorDiscordId("CRTID");
        builder.adventureId("WRLDID");
        builder.creationDate(OffsetDateTime.now());
        builder.lastUpdateDate(OffsetDateTime.now());
        builder.isPlayerCharacter(true);
        builder.version(1);

        return builder;
    }
}
