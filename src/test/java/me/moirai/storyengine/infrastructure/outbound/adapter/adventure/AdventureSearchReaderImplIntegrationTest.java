package me.moirai.storyengine.infrastructure.outbound.adapter.adventure;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import me.moirai.storyengine.AbstractDatabaseIntegrationTest;
import me.moirai.storyengine.common.enums.SearchView;
import me.moirai.storyengine.core.domain.adventure.Adventure;
import me.moirai.storyengine.core.domain.adventure.AdventureFixture;
import me.moirai.storyengine.core.domain.world.World;
import me.moirai.storyengine.core.domain.world.WorldFixture;
import me.moirai.storyengine.core.port.inbound.adventure.SearchAdventures;
import me.moirai.storyengine.core.port.outbound.adventure.AdventureSearchReader;

public class AdventureSearchReaderImplIntegrationTest extends AbstractDatabaseIntegrationTest {

    private static final Long OWNER_ID = AdventureFixture.OWNER_ID;

    @Autowired
    private AdventureSearchReader reader;

    @BeforeEach
    public void before() {
        clearDatabase();
    }

    @Test
    public void search_whenMyStuffAndOwnerExists_thenReturnResults() {

        // Given
        var world = insert(WorldFixture.publicWorld().build(), World.class);
        var adventure = AdventureFixture.publicAdventure()
                .worldId(world.getPublicId())
                .build();

        insert(adventure, Adventure.class);

        var query = new SearchAdventures(null, null, null, null,
                SearchView.MY_STUFF, null, null, null, null, OWNER_ID);

        // When
        var result = reader.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.items()).isEqualTo(1);
        assertThat(result.data()).hasSize(1);
    }

    @Test
    public void search_whenExploreAndPublicAdventureExists_thenReturnResults() {

        // Given
        var world = insert(WorldFixture.publicWorld().build(), World.class);
        var adventure = AdventureFixture.publicAdventure()
                .worldId(world.getPublicId())
                .build();
        insert(adventure, Adventure.class);

        var query = new SearchAdventures(null, null, null, null,
                SearchView.EXPLORE, null, null, null, null, OWNER_ID);

        // When
        var result = reader.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.items()).isEqualTo(1);
        assertThat(result.data()).hasSize(1);
    }

    @Test
    public void search_whenNoResults_thenReturnEmpty() {

        // Given
        var query = new SearchAdventures(null, null, null, null,
                SearchView.EXPLORE, null, null, null, null, OWNER_ID);

        // When
        var result = reader.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.items()).isEqualTo(0);
        assertThat(result.data()).isEmpty();
    }

    @Test
    public void search_whenFilterByName_thenReturnMatchingResults() {

        // Given
        var world = insert(WorldFixture.publicWorld().build(), World.class);
        var adventure = AdventureFixture.publicAdventure()
                .worldId(world.getPublicId())
                .build();

        insert(adventure, Adventure.class);

        var query = new SearchAdventures("Name", null, null, null,
                SearchView.MY_STUFF, null, null, null, null, OWNER_ID);

        // When
        var result = reader.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.items()).isEqualTo(1);
        assertThat(result.data().get(0).name()).isEqualTo("Name");
    }

    @Test
    public void search_whenUserIsOwner_thenUserPermissionIsOwner() {

        // Given
        var world = insert(WorldFixture.publicWorld().build(), World.class);
        var adventure = AdventureFixture.publicAdventure()
                .worldId(world.getPublicId())
                .build();

        insert(adventure, Adventure.class);

        var query = new SearchAdventures(null, null, null, null,
                SearchView.EXPLORE, null, null, null, null, OWNER_ID);

        // When
        var result = reader.search(query);

        // Then
        assertThat(result.data()).hasSize(1);
        assertThat(result.data().get(0).userPermission()).isEqualTo("OWNER");
    }

    @Test
    public void search_whenUserHasNoPermission_thenUserPermissionIsNull() {

        // Given
        var world = insert(WorldFixture.publicWorld().build(), World.class);
        var adventure = AdventureFixture.publicAdventure()
                .worldId(world.getPublicId())
                .build();

        insert(adventure, Adventure.class);

        var query = new SearchAdventures(null, null, null, null,
                SearchView.EXPLORE, null, null, null, null, 999999L);

        // When
        var result = reader.search(query);

        // Then
        assertThat(result.data()).hasSize(1);
        assertThat(result.data().get(0).userPermission()).isNull();
    }

    @Test
    public void shouldReturnTheSavedImagePositionWhenSearchingAdventures() {

        // Given
        var world = insert(WorldFixture.publicWorld().build(), World.class);
        var adventure = AdventureFixture.publicAdventure()
                .worldId(world.getPublicId())
                .build();

        var saved = insert(adventure, Adventure.class);
        saved.updateUiImagePosition(0.3, 0.7);
        update(saved, saved.getId(), Adventure.class);

        var query = new SearchAdventures(null, null, null, null,
                SearchView.MY_STUFF, null, null, null, null, OWNER_ID);

        // When
        var result = reader.search(query);

        // Then
        assertThat(result.data()).hasSize(1);
        assertThat(result.data().get(0).uiImagePositionX()).isEqualTo(0.3);
        assertThat(result.data().get(0).uiImagePositionY()).isEqualTo(0.7);
    }

    @Test
    public void shouldReturnNoImagePositionWhenTheAdventureHasNoneSaved() {

        // Given
        var world = insert(WorldFixture.publicWorld().build(), World.class);
        var adventure = AdventureFixture.publicAdventure()
                .worldId(world.getPublicId())
                .build();

        insert(adventure, Adventure.class);

        var query = new SearchAdventures(null, null, null, null,
                SearchView.MY_STUFF, null, null, null, null, OWNER_ID);

        // When
        var result = reader.search(query);

        // Then
        assertThat(result.data()).hasSize(1);
        assertThat(result.data().get(0).uiImagePositionX()).isNull();
        assertThat(result.data().get(0).uiImagePositionY()).isNull();
    }
}
