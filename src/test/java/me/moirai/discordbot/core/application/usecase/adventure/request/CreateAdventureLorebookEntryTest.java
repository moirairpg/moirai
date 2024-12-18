package me.moirai.discordbot.core.application.usecase.adventure.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class CreateAdventureLorebookEntryTest {

    @Test
    public void createEntryCommand_whenValidData_thenBuildNewInstance() {

        // Given
        CreateAdventureLorebookEntry.Builder builder =  CreateAdventureLorebookEntry.builder()
                .name("Volin Habar")
                .description("Volin Habar is a warrior that fights with a sword.")
                .regex("[Vv]olin [Hh]abar|[Vv]oha")
                .playerDiscordId("2423423423423")
                .adventureId("ADVID")
                .requesterDiscordId("1234");

        // When
        CreateAdventureLorebookEntry command = builder.build();

        // Then
        assertThat(command).isNotNull();
        assertThat(command.getAdventureId()).isEqualTo("ADVID");
        assertThat(command.getName()).isEqualTo("Volin Habar");
        assertThat(command.getDescription()).isEqualTo("Volin Habar is a warrior that fights with a sword.");
        assertThat(command.getRegex()).isEqualTo("[Vv]olin [Hh]abar|[Vv]oha");
        assertThat(command.getPlayerDiscordId()).isEqualTo("2423423423423");
        assertThat(command.getRequesterDiscordId()).isEqualTo("1234");
    }
}
