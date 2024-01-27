package es.thalesalv.chatrpg.core.domain.model.world;

import static es.thalesalv.chatrpg.core.domain.model.Visibility.PRIVATE;
import static es.thalesalv.chatrpg.core.domain.model.Visibility.PUBLIC;

import java.util.ArrayList;
import java.util.List;

import es.thalesalv.chatrpg.core.domain.model.PermissionFixture;
import es.thalesalv.chatrpg.core.domain.model.Permissions;

public class WorldFixture {

    public static World.Builder publicWorld() {

        World.Builder builder = World.builder();
        builder.id("857345HAA");
        builder.name("ChatRPG");
        builder.description("This is an RPG world");
        builder.initialPrompt("As you enter the city, people around you start looking at you.");
        builder.visibility(PUBLIC);

        Permissions permissions = PermissionFixture.samplePermissions().build();
        builder.permissions(permissions);

        List<String> lorebook = new ArrayList<>();
        lorebook.add("LBENTRY01");
        lorebook.add("LBENTRY02");
        builder.lorebook(Lorebook.builder().lorebookEntryIds(lorebook).build());

        return builder;
    }

    public static World.Builder privateWorld() {

        World.Builder builder = World.builder();
        builder.id("857345HAA");
        builder.name("ChatRPG");
        builder.description("This is an RPG world");
        builder.initialPrompt("As you enter the city, people around you start looking at you.");
        builder.visibility(PRIVATE);

        Permissions permissions = PermissionFixture.samplePermissions().build();
        builder.permissions(permissions);

        List<String> lorebook = new ArrayList<>();
        lorebook.add("LBENTRY01");
        lorebook.add("LBENTRY02");
        builder.lorebook(Lorebook.builder().lorebookEntryIds(lorebook).build());

        return builder;
    }
}
