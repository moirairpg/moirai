package es.thalesalv.chatrpg.core.domain.model.lorebook;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class LorebookEntryTest {

    @Test
    public void updateLorebookEntryName() {

        // Given
        LorebookEntry entry = LorebookEntryFixture.sampleLorebookEntry().build();

        // When
        entry.updateName("New Name");

        // Then
        assertThat(entry.getName()).isEqualTo("New Name");
    }

    @Test
    public void updateLorebookEntryDescription() {

        // Given
        LorebookEntry entry = LorebookEntryFixture.sampleLorebookEntry().build();

        // When
        entry.updateDescription("New Description");

        // Then
        assertThat(entry.getDescription()).isEqualTo("New Description");
    }

    @Test
    public void updateLorebookEntryRegex() {

        // Given
        LorebookEntry entry = LorebookEntryFixture.sampleLorebookEntry().build();

        // When
        entry.updateRegex("[Rr]egex");

        // Then
        assertThat(entry.getRegex()).isEqualTo("[Rr]egex");
    }

    @Test
    public void assignUserToLorebookEntry() {

        // Given
        String expectedPlayerDiscordId = "4234234234";
        LorebookEntry entry = LorebookEntryFixture.sampleLorebookEntry().build();

        // When
        entry.assignPlayer(expectedPlayerDiscordId);

        // Then
        assertThat(expectedPlayerDiscordId).isEqualTo(entry.getPlayerDiscordId());
    }

    @Test
    public void unassignUserToLorebookEntry() {

        // Given
        LorebookEntry entry = LorebookEntryFixture.samplePlayerCharacterLorebookEntry().build();

        // When
        entry.unassignPlayer();

        // Then
        assertThat(entry.getPlayerDiscordId()).isBlank();
    }
}
