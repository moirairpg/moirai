package es.thalesalv.chatrpg.core.domain.world;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import es.thalesalv.chatrpg.core.domain.Permissions;
import es.thalesalv.chatrpg.core.domain.PermissionsFixture;
import es.thalesalv.chatrpg.core.domain.Visibility;

public class WorldFixture {

    public static World.Builder publicWorld() {

        World.Builder builder = World.builder();
        builder.id("857345HAA");
        builder.name("ChatRPG");
        builder.description("This is an RPG world");
        builder.adventureStart("As you enter the city, people around you start looking at you.");
        builder.visibility(Visibility.fromString("PUBLIC"));
        builder.creatorDiscordId("CRTID");
        builder.creationDate(OffsetDateTime.now());
        builder.lastUpdateDate(OffsetDateTime.now());

        Permissions permissions = PermissionsFixture.samplePermissions().build();
        builder.permissions(permissions);

        List<WorldLorebookEntry> lorebook = new ArrayList<>();
        lorebook.add(WorldLorebookEntryFixture.sampleLorebookEntry().build());
        lorebook.add(WorldLorebookEntryFixture.samplePlayerCharacterLorebookEntry().build());
        builder.lorebook(lorebook);

        return builder;
    }

    public static World.Builder privateWorld() {

        World.Builder builder = World.builder();
        builder.id("857345HAA");
        builder.name("ChatRPG");
        builder.description("This is an RPG world");
        builder.adventureStart("As you enter the city, people around you start looking at you.");
        builder.visibility(Visibility.fromString("PRIVATE"));
        builder.creatorDiscordId("CRTID");
        builder.creationDate(OffsetDateTime.now());
        builder.lastUpdateDate(OffsetDateTime.now());

        Permissions permissions = PermissionsFixture.samplePermissions().build();
        builder.permissions(permissions);

        List<WorldLorebookEntry> lorebook = new ArrayList<>();
        lorebook.add(WorldLorebookEntryFixture.sampleLorebookEntry().build());
        lorebook.add(WorldLorebookEntryFixture.samplePlayerCharacterLorebookEntry().build());
        builder.lorebook(lorebook);

        return builder;
    }
}
