package me.moirai.discordbot.core.application.usecase.persona.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;

public class UpdatePersonaTest {

    @Test
    public void buildObject_whenAllValuesAreSupplied_thenBuildObject() {

        // Given
        String name = "name";
        String personality = "personality";
        String visibility = "PRIVATE";
        String requesterDiscordId = "123123";
        List<String> usersAllowedToReadToAdd = Lists.list("123123", "123123");
        List<String> usersAllowedToWriteToAdd = Lists.list("123123", "123123");
        List<String> usersAllowedToReadToRemove = Lists.list("123123", "123123");
        List<String> usersAllowedToWriteToRemove = Lists.list("123123", "123123");

        UpdatePersona.Builder updatePersonaBuilder = UpdatePersona.builder()
                .name(name)
                .personality(personality)
                .visibility(visibility)
                .requesterDiscordId(requesterDiscordId)
                .usersAllowedToReadToAdd(usersAllowedToReadToAdd)
                .usersAllowedToWriteToAdd(usersAllowedToWriteToAdd)
                .usersAllowedToReadToRemove(usersAllowedToReadToRemove)
                .usersAllowedToWriteToRemove(usersAllowedToWriteToRemove);

        // When
        UpdatePersona updatePersona = updatePersonaBuilder.build();

        // Then
        assertThat(updatePersona).isNotNull();
        assertThat(updatePersona.getName()).isNotNull().isNotEmpty().isEqualTo(name);
        assertThat(updatePersona.getPersonality()).isNotNull().isNotEmpty().isEqualTo(personality);
        assertThat(updatePersona.getVisibility()).isNotNull().isNotEmpty().isEqualTo(visibility);
        assertThat(updatePersona.getRequesterDiscordId()).isNotNull().isNotEmpty().isEqualTo(requesterDiscordId);
        assertThat(updatePersona.getUsersAllowedToReadToAdd()).isNotNull().isNotEmpty().hasSameElementsAs(usersAllowedToReadToAdd);
        assertThat(updatePersona.getUsersAllowedToWriteToAdd()).isNotNull().isNotEmpty().hasSameElementsAs(usersAllowedToWriteToAdd);
        assertThat(updatePersona.getUsersAllowedToReadToRemove()).isNotNull().isNotEmpty().hasSameElementsAs(usersAllowedToReadToRemove);
        assertThat(updatePersona.getUsersAllowedToWriteToRemove()).isNotNull().isNotEmpty().hasSameElementsAs(usersAllowedToWriteToRemove);
    }

    @Test
    public void buildObject_whenWriterUsersToAddNotSupplied_thenBuildObjectWithEmptyList() {

        // Given
        String name = "name";
        String personality = "personality";
        String visibility = "PRIVATE";
        String requesterDiscordId = "123123";
        List<String> usersAllowedToReadToAdd = Lists.list("123123", "123123");
        List<String> usersAllowedToWriteToAdd = null;
        List<String> usersAllowedToReadToRemove = Lists.list("123123", "123123");
        List<String> usersAllowedToWriteToRemove = Lists.list("123123", "123123");

        UpdatePersona.Builder updatePersonaBuilder = UpdatePersona.builder()
                .name(name)
                .personality(personality)
                .visibility(visibility)
                .requesterDiscordId(requesterDiscordId)
                .usersAllowedToReadToAdd(usersAllowedToReadToAdd)
                .usersAllowedToWriteToAdd(usersAllowedToWriteToAdd)
                .usersAllowedToReadToRemove(usersAllowedToReadToRemove)
                .usersAllowedToWriteToRemove(usersAllowedToWriteToRemove);

        // When
        UpdatePersona updatePersona = updatePersonaBuilder.build();

        // Then
        assertThat(updatePersona).isNotNull();
        assertThat(updatePersona.getName()).isNotNull().isNotEmpty().isEqualTo(name);
        assertThat(updatePersona.getPersonality()).isNotNull().isNotEmpty().isEqualTo(personality);
        assertThat(updatePersona.getVisibility()).isNotNull().isNotEmpty().isEqualTo(visibility);
        assertThat(updatePersona.getRequesterDiscordId()).isNotNull().isNotEmpty().isEqualTo(requesterDiscordId);
        assertThat(updatePersona.getUsersAllowedToReadToAdd()).isNotNull().isNotEmpty().hasSameElementsAs(usersAllowedToReadToAdd);
        assertThat(updatePersona.getUsersAllowedToWriteToAdd()).isNotNull().isEmpty();
        assertThat(updatePersona.getUsersAllowedToReadToRemove()).isNotNull().isNotEmpty().hasSameElementsAs(usersAllowedToReadToRemove);
        assertThat(updatePersona.getUsersAllowedToWriteToRemove()).isNotNull().isNotEmpty().hasSameElementsAs(usersAllowedToWriteToRemove);
    }

    @Test
    public void buildObject_whenReaderUsersToAddNotSupplied_thenBuildObjectWithEmptyList() {

        // Given
        String name = "name";
        String personality = "personality";
        String visibility = "PRIVATE";
        String requesterDiscordId = "123123";
        List<String> usersAllowedToReadToAdd = null;
        List<String> usersAllowedToWriteToAdd = Lists.list("123123", "123123");
        List<String> usersAllowedToReadToRemove = Lists.list("123123", "123123");
        List<String> usersAllowedToWriteToRemove = Lists.list("123123", "123123");

        UpdatePersona.Builder updatePersonaBuilder = UpdatePersona.builder()
                .name(name)
                .personality(personality)
                .visibility(visibility)
                .requesterDiscordId(requesterDiscordId)
                .usersAllowedToReadToAdd(usersAllowedToReadToAdd)
                .usersAllowedToWriteToAdd(usersAllowedToWriteToAdd)
                .usersAllowedToReadToRemove(usersAllowedToReadToRemove)
                .usersAllowedToWriteToRemove(usersAllowedToWriteToRemove);

        // When
        UpdatePersona updatePersona = updatePersonaBuilder.build();

        // Then
        assertThat(updatePersona).isNotNull();
        assertThat(updatePersona.getName()).isNotNull().isNotEmpty().isEqualTo(name);
        assertThat(updatePersona.getPersonality()).isNotNull().isNotEmpty().isEqualTo(personality);
        assertThat(updatePersona.getVisibility()).isNotNull().isNotEmpty().isEqualTo(visibility);
        assertThat(updatePersona.getRequesterDiscordId()).isNotNull().isNotEmpty().isEqualTo(requesterDiscordId);
        assertThat(updatePersona.getRequesterDiscordId()).isNotNull().isNotEmpty().isEqualTo(requesterDiscordId);
        assertThat(updatePersona.getUsersAllowedToReadToAdd()).isNotNull().isEmpty();
        assertThat(updatePersona.getUsersAllowedToWriteToAdd()).isNotNull().isNotEmpty().hasSameElementsAs(usersAllowedToWriteToAdd);
        assertThat(updatePersona.getUsersAllowedToReadToRemove()).isNotNull().isNotEmpty().hasSameElementsAs(usersAllowedToReadToRemove);
        assertThat(updatePersona.getUsersAllowedToWriteToRemove()).isNotNull().isNotEmpty().hasSameElementsAs(usersAllowedToWriteToRemove);
    }

    @Test
    public void buildObject_whenReaderUsersToRemoveNotSupplied_thenBuildObjectWithEmptyList() {

        // Given
        String name = "name";
        String personality = "personality";
        String visibility = "PRIVATE";
        String requesterDiscordId = "123123";
        List<String> usersAllowedToReadToAdd = Lists.list("123123", "123123");
        List<String> usersAllowedToWriteToAdd = Lists.list("123123", "123123");
        List<String> usersAllowedToReadToRemove = null;
        List<String> usersAllowedToWriteToRemove = Lists.list("123123", "123123");

        UpdatePersona.Builder updatePersonaBuilder = UpdatePersona.builder()
                .name(name)
                .personality(personality)
                .visibility(visibility)
                .requesterDiscordId(requesterDiscordId)
                .usersAllowedToReadToAdd(usersAllowedToReadToAdd)
                .usersAllowedToWriteToAdd(usersAllowedToWriteToAdd)
                .usersAllowedToReadToRemove(usersAllowedToReadToRemove)
                .usersAllowedToWriteToRemove(usersAllowedToWriteToRemove);

        // When
        UpdatePersona updatePersona = updatePersonaBuilder.build();

        // Then
        assertThat(updatePersona).isNotNull();
        assertThat(updatePersona.getName()).isNotNull().isNotEmpty().isEqualTo(name);
        assertThat(updatePersona.getPersonality()).isNotNull().isNotEmpty().isEqualTo(personality);
        assertThat(updatePersona.getVisibility()).isNotNull().isNotEmpty().isEqualTo(visibility);
        assertThat(updatePersona.getRequesterDiscordId()).isNotNull().isNotEmpty().isEqualTo(requesterDiscordId);
        assertThat(updatePersona.getRequesterDiscordId()).isNotNull().isNotEmpty().isEqualTo(requesterDiscordId);
        assertThat(updatePersona.getUsersAllowedToReadToAdd()).isNotNull().isNotEmpty().hasSameElementsAs(usersAllowedToWriteToAdd);
        assertThat(updatePersona.getUsersAllowedToWriteToAdd()).isNotNull().isNotEmpty().hasSameElementsAs(usersAllowedToWriteToAdd);
        assertThat(updatePersona.getUsersAllowedToReadToRemove()).isNotNull().isEmpty();
        assertThat(updatePersona.getUsersAllowedToWriteToRemove()).isNotNull().isNotEmpty().hasSameElementsAs(usersAllowedToWriteToRemove);
    }

    @Test
    public void buildObject_whenWriterUsersToRemoveNotSupplied_thenBuildObjectWithEmptyList() {

        // Given
        String name = "name";
        String personality = "personality";
        String visibility = "PRIVATE";
        String requesterDiscordId = "123123";
        List<String> usersAllowedToReadToAdd = Lists.list("123123", "123123");
        List<String> usersAllowedToWriteToAdd = Lists.list("123123", "123123");
        List<String> usersAllowedToReadToRemove = Lists.list("123123", "123123");
        List<String> usersAllowedToWriteToRemove = null;

        UpdatePersona.Builder updatePersonaBuilder = UpdatePersona.builder()
                .name(name)
                .personality(personality)
                .visibility(visibility)
                .requesterDiscordId(requesterDiscordId)
                .usersAllowedToReadToAdd(usersAllowedToReadToAdd)
                .usersAllowedToWriteToAdd(usersAllowedToWriteToAdd)
                .usersAllowedToReadToRemove(usersAllowedToReadToRemove)
                .usersAllowedToWriteToRemove(usersAllowedToWriteToRemove);

        // When
        UpdatePersona updatePersona = updatePersonaBuilder.build();

        // Then
        assertThat(updatePersona).isNotNull();
        assertThat(updatePersona.getName()).isNotNull().isNotEmpty().isEqualTo(name);
        assertThat(updatePersona.getPersonality()).isNotNull().isNotEmpty().isEqualTo(personality);
        assertThat(updatePersona.getVisibility()).isNotNull().isNotEmpty().isEqualTo(visibility);
        assertThat(updatePersona.getRequesterDiscordId()).isNotNull().isNotEmpty().isEqualTo(requesterDiscordId);
        assertThat(updatePersona.getRequesterDiscordId()).isNotNull().isNotEmpty().isEqualTo(requesterDiscordId);
        assertThat(updatePersona.getUsersAllowedToReadToAdd()).isNotNull().isNotEmpty().hasSameElementsAs(usersAllowedToWriteToAdd);
        assertThat(updatePersona.getUsersAllowedToWriteToAdd()).isNotNull().isNotEmpty().hasSameElementsAs(usersAllowedToWriteToAdd);
        assertThat(updatePersona.getUsersAllowedToReadToRemove()).isNotNull().isNotEmpty().hasSameElementsAs(usersAllowedToReadToRemove);
        assertThat(updatePersona.getUsersAllowedToWriteToRemove()).isNotNull().isEmpty();
    }
}
