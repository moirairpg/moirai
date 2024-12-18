package me.moirai.discordbot.core.domain.adventure;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.Test;

public class AdventureLorebookEntryTest {

    @Test
    public void createLorebookEntry_whenValidData_thenInstanceIsCreated() {

        // Given
        AdventureLorebookEntry.Builder builder = AdventureLorebookEntry.builder()
                .id("857345HAA")
                .name("White River")
                .description("The White River goes through Falkreath, Whiterun and ends in Eastmarch.")
                .regex("[Ww]hite [Rr]iver")
                .creatorDiscordId("CRTID")
                .adventureId("WRLDID")
                .creationDate(OffsetDateTime.now())
                .lastUpdateDate(OffsetDateTime.now())
                .isPlayerCharacter(true)
                .playerDiscordId("2423423423423")
                .version(1);

        // When
        AdventureLorebookEntry entry = builder.build();

        // Then
        assertThat(entry).isNotNull();
        assertThat(entry.getCreationDate()).isNotNull();
        assertThat(entry.getLastUpdateDate()).isNotNull();
        assertThat(entry.getDescription()).isEqualTo("The White River goes through Falkreath, Whiterun and ends in Eastmarch.");
        assertThat(entry.getId()).isEqualTo("857345HAA");
        assertThat(entry.getName()).isEqualTo("White River");
        assertThat(entry.getPlayerDiscordId()).isEqualTo("2423423423423");
        assertThat(entry.getCreatorDiscordId()).isEqualTo("CRTID");
        assertThat(entry.getRegex()).isEqualTo("[Ww]hite [Rr]iver");
        assertThat(entry.getVersion()).isEqualTo(1);
        assertThat(entry.getAdventureId()).isEqualTo("WRLDID");
        assertThat(entry.isPlayerCharacter()).isTrue();
    }

    @Test
    public void updateLorebookEntryName() {

        // Given
        AdventureLorebookEntry entry = AdventureLorebookEntryFixture.sampleLorebookEntry().build();

        // When
        entry.updateName("New Name");

        // Then
        assertThat(entry.getName()).isEqualTo("New Name");
    }

    @Test
    public void updateLorebookEntryDescription() {

        // Given
        AdventureLorebookEntry entry = AdventureLorebookEntryFixture.sampleLorebookEntry().build();

        // When
        entry.updateDescription("New Description");

        // Then
        assertThat(entry.getDescription()).isEqualTo("New Description");
    }

    @Test
    public void updateLorebookEntryRegex() {

        // Given
        AdventureLorebookEntry entry = AdventureLorebookEntryFixture.sampleLorebookEntry().build();

        // When
        entry.updateRegex("[Rr]egex");

        // Then
        assertThat(entry.getRegex()).isEqualTo("[Rr]egex");
    }

    @Test
    public void assignUserToLorebookEntry() {

        // Given
        String expectedPlayerDiscordId = "4234234234";
        AdventureLorebookEntry entry = AdventureLorebookEntryFixture.sampleLorebookEntry().build();

        // When
        entry.assignPlayer(expectedPlayerDiscordId);

        // Then
        assertThat(expectedPlayerDiscordId).isEqualTo(entry.getPlayerDiscordId());
    }

    @Test
    public void unassignUserToLorebookEntry() {

        // Given
        AdventureLorebookEntry entry = AdventureLorebookEntryFixture.samplePlayerCharacterLorebookEntry().build();

        // When
        entry.unassignPlayer();

        // Then
        assertThat(entry.getPlayerDiscordId()).isBlank();
    }
}
