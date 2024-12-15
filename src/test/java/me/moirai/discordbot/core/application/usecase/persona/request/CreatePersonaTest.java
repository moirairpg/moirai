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

        CreatePersona.Builder createPersonaBuilder = CreatePersona.builder()
                .name(name)
                .personality(personality)
                .visibility(visibility)
                .requesterDiscordId(requesterDiscordId)
                .usersAllowedToWrite(usersAllowedToWrite)
                .usersAllowedToRead(usersAllowedToRead);

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

        CreatePersona.Builder createPersonaBuilder = CreatePersona.builder()
                .name(name)
                .personality(personality)
                .visibility(visibility)
                .requesterDiscordId(requesterDiscordId)
                .usersAllowedToWrite(usersAllowedToWrite)
                .usersAllowedToRead(usersAllowedToRead);

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

        CreatePersona.Builder createPersonaBuilder = CreatePersona.builder()
                .name(name)
                .personality(personality)
                .visibility(visibility)
                .requesterDiscordId(requesterDiscordId)
                .usersAllowedToWrite(usersAllowedToWrite)
                .usersAllowedToRead(usersAllowedToRead);

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
    }
}
