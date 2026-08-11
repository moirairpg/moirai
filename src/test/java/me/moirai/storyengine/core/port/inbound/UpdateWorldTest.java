package me.moirai.storyengine.core.port.inbound;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import me.moirai.storyengine.core.domain.userdetails.UserFixture;
import me.moirai.storyengine.core.port.inbound.world.UpdateWorld;

public class UpdateWorldTest {

    @Test
    public void buildObject_whenAllValuesAreValid_thenCreateInstance() {

        // Given
        UpdateWorld result = new UpdateWorld(
                null,
                "SomeName",
                "SomeDesc",
                "SomeStart",
                null,
                null,                null,
                null,
                List.of(),
                List.of(),
                List.of(),
                UserFixture.PUBLIC_ID);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.name()).isNotNull();
        assertThat(result.adventureStart()).isNotNull();
        assertThat(result.description()).isNotNull();
    }

    @Test
    public void buildObject_whenLorebookListsAreNull_thenListsAreEmpty() {

        // Given
        UpdateWorld result = new UpdateWorld(
                null,
                "SomeName",
                "SomeDesc",
                "SomeStart",
                null,
                null,                null,
                null,
                null,
                null,
                null,
                UserFixture.PUBLIC_ID);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.lorebookEntriesToAdd()).isEmpty();
        assertThat(result.lorebookEntriesToUpdate()).isEmpty();
        assertThat(result.lorebookEntriesToDelete()).isEmpty();
    }
}
