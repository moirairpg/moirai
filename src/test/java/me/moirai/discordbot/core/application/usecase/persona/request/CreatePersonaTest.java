package me.moirai.discordbot.core.application.usecase.persona.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;

public class CreatePersonaTest {

    @Test
    public void buildObject_whenAllValuesAreSupplied_thenBuildObject() {

        // Given
        String name = "name";
        String personality = "personality";
        String visibility = "PRIVATE";
        String requesterDiscordId = "123123";
        List<String> usersAllowedToRead = Lists.list("123123", "123123");
        List<String> usersAllowedToWrite = Lists.list("123123", "123123");
        String nudgeContent = "nudge";
        String nudgeRole = "SYSTEM";
        String bumpContent = "bump";
        String bumpRole = "SYSTEM";
        int bumpFrequency = 3;

        CreatePersona.Builder createPersonaBuilder = CreatePersona.builder()
                .name(name)
                .personality(personality)
                .visibility(visibility)
                .requesterDiscordId(requesterDiscordId)
                .usersAllowedToWrite(usersAllowedToWrite)
                .usersAllowedToRead(usersAllowedToRead)
                .nudgeContent(nudgeContent)
                .nudgeRole(nudgeRole)
                .bumpContent(bumpContent)
                .bumpRole(bumpRole)
                .bumpFrequency(bumpFrequency);

        // When
        CreatePersona createPersona = createPersonaBuilder.build();

        // Then
        assertThat(createPersona).isNotNull();
        assertThat(createPersona.getName()).isNotNull().isNotEmpty().isEqualTo(name);
        assertThat(createPersona.getPersonality()).isNotNull().isNotEmpty().isEqualTo(personality);
        assertThat(createPersona.getVisibility()).isNotNull().isNotEmpty().isEqualTo(visibility);
        assertThat(createPersona.getRequesterDiscordId()).isNotNull().isNotEmpty().isEqualTo(requesterDiscordId);
        assertThat(createPersona.getUsersAllowedToRead()).isNotNull().isNotEmpty().hasSameElementsAs(usersAllowedToRead);
        assertThat(createPersona.getUsersAllowedToWrite()).isNotNull().isNotEmpty().hasSameElementsAs(usersAllowedToWrite);
        assertThat(createPersona.getNudgeContent()).isNotNull().isNotEmpty().isEqualTo(nudgeContent);
        assertThat(createPersona.getNudgeRole()).isNotNull().isNotEmpty().isEqualTo(nudgeRole);
        assertThat(createPersona.getBumpContent()).isNotNull().isNotEmpty().isEqualTo(bumpContent);
        assertThat(createPersona.getBumpFrequency()).isNotNull().isEqualTo(bumpFrequency);
        assertThat(createPersona.getBumpRole()).isNotNull().isNotEmpty().isEqualTo(bumpRole);
    }

    @Test
    public void buildObject_whenWriterUsersNotSupplied_thenBuildObjectWithEmptyList() {

        // Given
        String name = "name";
        String personality = "personality";
        String visibility = "PRIVATE";
        String requesterDiscordId = "123123";
        List<String> usersAllowedToRead = Lists.list("123123", "123123");
        List<String> usersAllowedToWrite = null;
        String nudgeContent = "nudge";
        String nudgeRole = "SYSTEM";
        String bumpContent = "bump";
        String bumpRole = "SYSTEM";
        int bumpFrequency = 3;

        CreatePersona.Builder createPersonaBuilder = CreatePersona.builder()
                .name(name)
                .personality(personality)
                .visibility(visibility)
                .requesterDiscordId(requesterDiscordId)
                .usersAllowedToWrite(usersAllowedToWrite)
                .usersAllowedToRead(usersAllowedToRead)
                .nudgeContent(nudgeContent)
                .nudgeRole(nudgeRole)
                .bumpContent(bumpContent)
                .bumpRole(bumpRole)
                .bumpFrequency(bumpFrequency);

        // When
        CreatePersona createPersona = createPersonaBuilder.build();

        // Then
        assertThat(createPersona).isNotNull();
        assertThat(createPersona.getName()).isNotNull().isNotEmpty().isEqualTo(name);
        assertThat(createPersona.getPersonality()).isNotNull().isNotEmpty().isEqualTo(personality);
        assertThat(createPersona.getVisibility()).isNotNull().isNotEmpty().isEqualTo(visibility);
        assertThat(createPersona.getRequesterDiscordId()).isNotNull().isNotEmpty().isEqualTo(requesterDiscordId);
        assertThat(createPersona.getUsersAllowedToRead()).isNotNull().isNotEmpty().hasSameElementsAs(usersAllowedToRead);
        assertThat(createPersona.getUsersAllowedToWrite()).isNotNull().isEmpty();
        assertThat(createPersona.getNudgeContent()).isNotNull().isNotEmpty().isEqualTo(nudgeContent);
        assertThat(createPersona.getNudgeRole()).isNotNull().isNotEmpty().isEqualTo(nudgeRole);
        assertThat(createPersona.getBumpContent()).isNotNull().isNotEmpty().isEqualTo(bumpContent);
        assertThat(createPersona.getBumpFrequency()).isNotNull().isEqualTo(bumpFrequency);
        assertThat(createPersona.getBumpRole()).isNotNull().isNotEmpty().isEqualTo(bumpRole);
    }

    @Test
    public void buildObject_whenReaderUsersNotSupplied_thenBuildObjectWithEmptyList() {

        // Given
        String name = "name";
        String personality = "personality";
        String visibility = "PRIVATE";
        String requesterDiscordId = "123123";
        List<String> usersAllowedToRead = null;
        List<String> usersAllowedToWrite = Lists.list("123123", "123123");
        String nudgeContent = "nudge";
        String nudgeRole = "SYSTEM";
        String bumpContent = "bump";
        String bumpRole = "SYSTEM";
        int bumpFrequency = 3;

        CreatePersona.Builder createPersonaBuilder = CreatePersona.builder()
                .name(name)
                .personality(personality)
                .visibility(visibility)
                .requesterDiscordId(requesterDiscordId)
                .usersAllowedToWrite(usersAllowedToWrite)
                .usersAllowedToRead(usersAllowedToRead)
                .nudgeContent(nudgeContent)
                .nudgeRole(nudgeRole)
                .bumpContent(bumpContent)
                .bumpRole(bumpRole)
                .bumpFrequency(bumpFrequency);

        // When
        CreatePersona createPersona = createPersonaBuilder.build();

        // Then
        assertThat(createPersona).isNotNull();
        assertThat(createPersona.getName()).isNotNull().isNotEmpty().isEqualTo(name);
        assertThat(createPersona.getPersonality()).isNotNull().isNotEmpty().isEqualTo(personality);
        assertThat(createPersona.getVisibility()).isNotNull().isNotEmpty().isEqualTo(visibility);
        assertThat(createPersona.getRequesterDiscordId()).isNotNull().isNotEmpty().isEqualTo(requesterDiscordId);
        assertThat(createPersona.getUsersAllowedToRead()).isNotNull().isEmpty();
        assertThat(createPersona.getUsersAllowedToWrite()).isNotNull().isNotEmpty().hasSameElementsAs(usersAllowedToWrite);
        assertThat(createPersona.getNudgeContent()).isNotNull().isNotEmpty().isEqualTo(nudgeContent);
        assertThat(createPersona.getNudgeRole()).isNotNull().isNotEmpty().isEqualTo(nudgeRole);
        assertThat(createPersona.getBumpContent()).isNotNull().isNotEmpty().isEqualTo(bumpContent);
        assertThat(createPersona.getBumpFrequency()).isNotNull().isEqualTo(bumpFrequency);
        assertThat(createPersona.getBumpRole()).isNotNull().isNotEmpty().isEqualTo(bumpRole);
    }
}
