package me.moirai.discordbot.core.application.usecase.adventure.request;

public class UpdateAdventureLorebookEntryFixture {

    public static UpdateAdventureLorebookEntry.Builder sampleLorebookEntry() {

        return UpdateAdventureLorebookEntry.builder()
                .name("Volin Habar")
                .description("Volin Habar is a warrior that fights with a sword.")
                .regex("[Vv]olin [Hh]abar|[Vv]oha")
                .adventureId("ADVID")
                .requesterDiscordId("1234")
                .adventureId("123123")
                .id("123123");
    }

    public static UpdateAdventureLorebookEntry.Builder samplePlayerCharacterLorebookEntry() {

        return UpdateAdventureLorebookEntry.builder()
                .name("Volin Habar")
                .description("Volin Habar is a warrior that fights with a sword.")
                .regex("[Vv]olin [Hh]abar|[Vv]oha")
                .playerDiscordId("2423423423423")
                .adventureId("ADVID")
                .requesterDiscordId("1234")
                .adventureId("123123")
                .id("123123");
    }
}
