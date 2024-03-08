package es.thalesalv.chatrpg.core.application.command.channelconfig;

import es.thalesalv.chatrpg.core.application.command.world.WorldLorebookEntry;

public class CreateLorebookEntryFixture {

    public static WorldLorebookEntry.Builder sampleLorebookEntry() {

        WorldLorebookEntry.Builder builder = WorldLorebookEntry.builder();
        builder.name("White River");
        builder.description("The White River goes through Falkreath, Whiterun and ends in Eastmarch.");
        builder.regex("[Ww]hite [Rr]iver");

        return builder;
    }

    public static WorldLorebookEntry.Builder samplePlayerCharacterLorebookEntry() {

        WorldLorebookEntry.Builder builder = WorldLorebookEntry.builder();
        builder.name("Volin Habar");
        builder.description("Volin Habar is a warrior that fights with a sword.");
        builder.regex("[Vv]olin [Hh]abar|[Vv]oha");
        builder.playerDiscordId("2423423423423");

        return builder;
    }
}
