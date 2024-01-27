package es.thalesalv.chatrpg.core.domain.model.world;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import es.thalesalv.chatrpg.common.exception.BusinessException;

public class WorldTest {

    @Test
    public void makeWorldPublic() {

        // Given
        World world = WorldFixture.privateWorld().build();

        // When
        world.makePublic();

        // Then
        assertThat(world.isPublic()).isTrue();
    }

    @Test
    public void makeWorldPrivate() {

        // Given
        World world = WorldFixture.publicWorld().build();

        // When
        world.makePrivate();

        // Then
        assertThat(world.isPublic()).isFalse();
    }

    @Test
    public void updateWorldName() {

        // Given
        World world = WorldFixture.publicWorld().build();

        // When
        world.updateName("New Name");

        // Then
        assertThat(world.getName()).isEqualTo("New Name");
    }

    @Test
    public void updateWorldDescription() {

        // Given
        World world = WorldFixture.publicWorld().build();

        // When
        world.updateDescription("New Description");

        // Then
        assertThat(world.getDescription()).isEqualTo("New Description");
    }

    @Test
    public void updateWorldInitialPrompt() {

        // Given
        World world = WorldFixture.publicWorld().build();

        // When
        world.updateInitialPrompt("New Prompt");

        // Then
        assertThat(world.getInitialPrompt()).isEqualTo("New Prompt");
    }

    @Test
    public void errorWhenCreatingPersonaWithNullName() {

        // Given
        World.Builder worldBuilder = WorldFixture.publicWorld().name(null);

        // Then
        assertThrows(BusinessException.class, worldBuilder::build);
    }

    @Test
    public void errorWhenCreatingPersonaWithEmptyName() {

        // Given
        World.Builder worldBuilder = WorldFixture.publicWorld().name(EMPTY);

        // Then
        assertThrows(BusinessException.class, worldBuilder::build);
    }

    @Test
    public void errorWhenCreatingPersonaWithNullVisibility() {

        // Given
        World.Builder worldBuilder = WorldFixture.publicWorld().permissions(null);

        // Then
        assertThrows(BusinessException.class, worldBuilder::build);
    }

    @Test
    public void errorWhenCreatingPersonaWithNullPermissions() {

        // Given
        World.Builder worldBuilder = WorldFixture.publicWorld().visibility(null);

        // Then
        assertThrows(BusinessException.class, worldBuilder::build);
    }

    @Test
    public void errorWhenModifyingLorebookDirectly() {

        // Given
        World world = WorldFixture.publicWorld().build();
        String lorebookEntryId = "LBENTRYID";

        // Then
        assertThrows(UnsupportedOperationException.class,
                () -> world.getLorebook().getLorebookEntryIds().add(lorebookEntryId));
    }

    @Test
    public void addLorebookEntry() {

        // Given
        World world = WorldFixture.publicWorld().build();
        String lorebookEntryId = "LBENTRYID";

        // When
        world.addLorebookEntryToLorebook(lorebookEntryId);

        // Then
        assertThat(world.getLorebook().getLorebookEntryIds()).isNotNull().isNotEmpty().contains(lorebookEntryId);
    }

    @Test
    public void removeLorebookEntry() {

        // Given
        World world = WorldFixture.publicWorld().build();
        String lorebookEntryId = "LBENTRYID";

        // When
        world.removeLorebookEntryFromLorebook(lorebookEntryId);

        // Then
        assertThat(world.getLorebook().getLorebookEntryIds()).isNotNull().isNotEmpty().doesNotContain(lorebookEntryId);
    }
}
