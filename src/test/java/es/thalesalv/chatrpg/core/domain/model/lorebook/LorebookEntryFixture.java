package es.thalesalv.chatrpg.core.domain.model.lorebook;

public class LorebookEntryFixture {

    public static LorebookEntry.Builder sampleLorebookEntry() {

        LorebookEntry.Builder builder = LorebookEntry.builder();
        builder.id("857345HAA");
        builder.name("White River");
        builder.description("The White River goes through Falkreath, Whiterun and ends in Eastmarch.");
        builder.regex("[Ww]hite [Rr]iver");

        return builder;
    }

    public static LorebookEntry.Builder samplePlayerCharacterLorebookEntry() {

        LorebookEntry.Builder builder = LorebookEntry.builder();
        builder.id("45534453");
        builder.name("Volin Habar");
        builder.description("Volin Habar is a warrior that fights with a sword.");
        builder.regex("[Vv]olin [Hh]abar|[Vv]oha");
        builder.playerDiscordId("2423423423423");

        return builder;
    }
}
