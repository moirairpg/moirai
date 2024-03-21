package es.thalesalv.chatrpg.infrastructure.outbound.persistence.world;

public class WorldLorebookEntryEntityFixture {

    public static WorldLorebookEntryEntity.Builder sampleLorebookEntry() {

        WorldLorebookEntryEntity.Builder builder = WorldLorebookEntryEntity.builder();
        builder.id("857345HAA");
        builder.name("White River");
        builder.description("The White River goes through Falkreath, Whiterun and ends in Eastmarch.");
        builder.regex("[Ww]hite [Rr]iver");
        builder.worldId("WRLDID");

        return builder;
    }

    public static WorldLorebookEntryEntity.Builder samplePlayerCharacterLorebookEntry() {

        WorldLorebookEntryEntity.Builder builder = WorldLorebookEntryEntity.builder();
        builder.id("45534453");
        builder.name("Volin Habar");
        builder.description("Volin Habar is a warrior that fights with a sword.");
        builder.regex("[Vv]olin [Hh]abar|[Vv]oha");
        builder.playerDiscordId("2423423423423");
        builder.worldId("WRLDID");

        return builder;
    }
}
