package es.thalesalv.chatrpg.testutils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import es.thalesalv.chatrpg.adapters.data.entity.LorebookEntity;
import es.thalesalv.chatrpg.adapters.data.entity.LorebookEntryEntity;
import es.thalesalv.chatrpg.adapters.data.entity.LorebookEntryRegexEntity;
import es.thalesalv.chatrpg.application.mapper.lorebook.LorebookDTOToEntity;
import es.thalesalv.chatrpg.application.mapper.lorebook.LorebookEntryDTOToEntity;
import es.thalesalv.chatrpg.domain.enums.Visibility;
import es.thalesalv.chatrpg.domain.model.chconf.Lorebook;
import es.thalesalv.chatrpg.domain.model.chconf.LorebookEntry;

public class LorebookTestUtils {

    private static final String NANO_ID = "241OZASGM6CESV7";

    public static Lorebook buildSimplePublicLorebook() {

        final Set<LorebookEntry> entries = new HashSet<>();
        final LorebookEntry entry = LorebookEntry.builder()
                .name("Test entry")
                .description("Test description")
                .build();

        entries.add(entry);

        return Lorebook.builder()
                .id(NANO_ID)
                .name("Test lorebook")
                .owner("1083867535658725536")
                .visibility("public")
                .description("This is a test lorebook")
                .entries(entries)
                .writePermissions(new ArrayList<String>())
                .readPermissions(new ArrayList<String>())
                .build();
    }

    public static LorebookEntity buildSimplePublicLorebookEntity() {

        final List<LorebookEntryRegexEntity> entries = new ArrayList<>();
        final LorebookEntryEntity entry = LorebookEntryEntity.builder()
                .name("Test entry")
                .description("Test description")
                .build();

        final LorebookEntryRegexEntity regexEntity = LorebookEntryRegexEntity.builder()
                .regex("test")
                .lorebookEntry(entry)
                .build();

        final LorebookEntity lorebook = LorebookEntity.builder()
                .id(NANO_ID)
                .name("Test lorebook")
                .owner("1083867535658725536")
                .visibility("public")
                .description("This is a test lorebook")
                .entries(entries)
                .writePermissions(new ArrayList<String>())
                .readPermissions(new ArrayList<String>())
                .build();

        regexEntity.setLorebook(lorebook);
        entries.add(regexEntity);

        return lorebook;
    }

    public static List<Lorebook> buildSimplePublicLorebookList() {

        final List<String> permissions = new ArrayList<>();
        permissions.add("302796314822049793");

        final Lorebook privateLorebook = buildSimplePublicLorebook();
        final Lorebook someonesElsesPublicLorebook = buildSimplePublicLorebook();
        final Lorebook someonesElsesPrivateLorebook = buildSimplePublicLorebook();
        final Lorebook someonesElsesPrivateLorebookWithWritePersmissions = buildSimplePublicLorebook();
        final Lorebook someonesElsesPrivateLorebookWithReadPermissions = buildSimplePublicLorebook();
        final Lorebook someonesElsesPrivateLorebookWithAllPermissions = buildSimplePublicLorebook();
        final Lorebook someonesElsesPublicLorebookWithAllPermissions = buildSimplePublicLorebook();
        final Lorebook ownPublicLorebookWithAllPermissions = buildSimplePublicLorebook();
        final Lorebook ownPrivateLorebookWithAllPermissions = buildSimplePublicLorebook();

        privateLorebook.setVisibility("private");
        someonesElsesPrivateLorebook.setVisibility("private");
        someonesElsesPublicLorebook.setOwner("463004243411075072");
        someonesElsesPrivateLorebook.setOwner("463004243411075072");

        ownPublicLorebookWithAllPermissions.setWritePermissions(permissions);
        ownPublicLorebookWithAllPermissions.setReadPermissions(permissions);
        ownPublicLorebookWithAllPermissions.setOwner("302796314822049793");
        ownPublicLorebookWithAllPermissions.setVisibility("private");

        ownPrivateLorebookWithAllPermissions.setWritePermissions(permissions);
        ownPrivateLorebookWithAllPermissions.setReadPermissions(permissions);
        ownPrivateLorebookWithAllPermissions.setOwner("302796314822049793");
        ownPrivateLorebookWithAllPermissions.setVisibility("private");

        someonesElsesPrivateLorebookWithAllPermissions.setWritePermissions(permissions);
        someonesElsesPrivateLorebookWithAllPermissions.setReadPermissions(permissions);
        someonesElsesPrivateLorebookWithAllPermissions.setOwner("463004243411075072");
        someonesElsesPrivateLorebookWithAllPermissions.setVisibility("private");

        someonesElsesPublicLorebookWithAllPermissions.setWritePermissions(permissions);
        someonesElsesPublicLorebookWithAllPermissions.setReadPermissions(permissions);
        someonesElsesPublicLorebookWithAllPermissions.setOwner("463004243411075072");
        someonesElsesPublicLorebookWithAllPermissions.setVisibility("public");

        someonesElsesPrivateLorebookWithWritePersmissions.setVisibility("private");
        someonesElsesPrivateLorebookWithReadPermissions.setVisibility("private");
        someonesElsesPrivateLorebookWithWritePersmissions.setOwner("463004243411075072");
        someonesElsesPrivateLorebookWithReadPermissions.setOwner("463004243411075072");
        someonesElsesPrivateLorebookWithWritePersmissions.setWritePermissions(permissions);
        someonesElsesPrivateLorebookWithReadPermissions.setReadPermissions(permissions);

        final List<Lorebook> lorebooks = new ArrayList<>();
        lorebooks.add(buildSimplePublicLorebook());
        lorebooks.add(privateLorebook);
        lorebooks.add(someonesElsesPublicLorebook);
        lorebooks.add(someonesElsesPrivateLorebook);
        lorebooks.add(someonesElsesPrivateLorebookWithWritePersmissions);
        lorebooks.add(someonesElsesPrivateLorebookWithReadPermissions);
        lorebooks.add(someonesElsesPrivateLorebookWithAllPermissions);
        lorebooks.add(someonesElsesPublicLorebookWithAllPermissions);
        lorebooks.add(ownPublicLorebookWithAllPermissions);
        lorebooks.add(ownPrivateLorebookWithAllPermissions);

        return lorebooks;
    }

    public static List<LorebookEntity> buildSimplePublicLorebookEntityList() {

        return buildSimplePublicLorebookList().stream()
                .map(new LorebookDTOToEntity(new LorebookEntryDTOToEntity()))
                .collect(Collectors.toList());
    }

    public static boolean hasReadPermissions(final Lorebook lorebook, final String userId) {

        final boolean isPublic = Visibility.isPublic(lorebook.getVisibility());
        final boolean isOwner = lorebook.getOwner()
                .equals(userId);

        final List<String> readPermissions = Optional.ofNullable(lorebook.getReadPermissions())
                .orElse(Collections.emptyList());

        final List<String> writePermissions = Optional.ofNullable(lorebook.getWritePermissions())
                .orElse(Collections.emptyList());

        final boolean canRead = readPermissions.contains(userId) || writePermissions.contains(userId);
        return isPublic || (isOwner || canRead);
    }

    public static boolean hasWritePermissions(final Lorebook lorebook, final String userId) {

        final List<String> writePermissions = Optional.ofNullable(lorebook.getWritePermissions())
                .orElse(Collections.emptyList());

        final boolean isOwner = lorebook.getOwner()
                .equals(userId);

        final boolean canWrite = writePermissions.contains(userId);
        return isOwner || canWrite;
    }
}
