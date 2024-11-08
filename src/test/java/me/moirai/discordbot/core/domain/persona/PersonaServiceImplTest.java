package me.moirai.discordbot.core.domain.persona;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.moirai.discordbot.common.exception.AssetNotFoundException;
import me.moirai.discordbot.common.exception.ModerationException;
import me.moirai.discordbot.core.application.model.result.TextModerationResultFixture;
import me.moirai.discordbot.core.application.port.TextModerationPort;
import me.moirai.discordbot.core.application.usecase.persona.request.CreatePersona;
import me.moirai.discordbot.core.domain.Visibility;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class PersonaServiceImplTest {

    @Mock
    private TextModerationPort moderationPort;

    @Mock
    private PersonaDomainRepository repository;

    @InjectMocks
    private PersonaServiceImpl service;

    @Test
    public void createPersona_whenValidData_thenPersonaIsCreatedSuccessfully() {

        // Given
        String name = "MoirAI";
        String personality = "I am a chatbot";
        String visibility = "PRIVATE";

        Nudge nudge = NudgeFixture.sample().build();
        Bump bump = BumpFixture.sample().build();

        Persona expectedPersona = PersonaFixture.privatePersona()
                .name(name)
                .personality(personality)
                .visibility(Visibility.fromString(visibility))
                .nudge(nudge)
                .bump(bump)
                .build();

        CreatePersona command = CreatePersona.builder()
                .name(name)
                .personality(personality)
                .nudgeContent(nudge.getContent())
                .nudgeRole(nudge.getRole().toString())
                .bumpContent(bump.getContent())
                .bumpRole(bump.getRole().toString())
                .bumpFrequency(bump.getFrequency())
                .visibility(visibility)
                .build();

        when(moderationPort.moderate(anyString()))
                .thenReturn(Mono.just(TextModerationResultFixture.withoutFlags().build()));

        when(repository.save(any(Persona.class))).thenReturn(expectedPersona);

        // Then
        StepVerifier.create(service.createFrom(command))
                .assertNext(createdPersona -> {
                    assertThat(createdPersona).isNotNull().isEqualTo(expectedPersona);
                    assertThat(createdPersona.getName()).isEqualTo(expectedPersona.getName());
                    assertThat(createdPersona.getOwnerDiscordId()).isEqualTo(expectedPersona.getOwnerDiscordId());
                    assertThat(createdPersona.getUsersAllowedToWrite())
                            .isEqualTo(expectedPersona.getUsersAllowedToWrite());

                    assertThat(createdPersona.getUsersAllowedToRead())
                            .isEqualTo(expectedPersona.getUsersAllowedToRead());

                    assertThat(createdPersona.getPersonality()).isEqualTo(expectedPersona.getPersonality());
                    assertThat(createdPersona.getVisibility()).isEqualTo(expectedPersona.getVisibility());
                })
                .verifyComplete();
    }

    @Test
    public void createPersona_whenBumpIsNull_thenCreatePersonaSuccessfully() {

        // Given
        String name = "MoirAI";
        String personality = "I am a chatbot";
        String visibility = "PRIVATE";

        Nudge nudge = NudgeFixture.sample().build();

        Persona expectedPersona = PersonaFixture.privatePersona()
                .name(name)
                .personality(personality)
                .visibility(Visibility.fromString(visibility))
                .nudge(nudge)
                .build();

        CreatePersona command = CreatePersona.builder()
                .name(name)
                .personality(personality)
                .nudgeContent(nudge.getContent())
                .nudgeRole(nudge.getRole().toString())
                .visibility(visibility)
                .build();

        when(moderationPort.moderate(anyString()))
                .thenReturn(Mono.just(TextModerationResultFixture.withoutFlags().build()));

        when(repository.save(any(Persona.class))).thenReturn(expectedPersona);

        // Then
        StepVerifier.create(service.createFrom(command))
                .assertNext(createdPersona -> {
                    assertThat(createdPersona).isNotNull().isEqualTo(expectedPersona);
                    assertThat(createdPersona.getName()).isEqualTo(expectedPersona.getName());
                    assertThat(createdPersona.getOwnerDiscordId()).isEqualTo(expectedPersona.getOwnerDiscordId());
                    assertThat(createdPersona.getUsersAllowedToWrite())
                            .isEqualTo(expectedPersona.getUsersAllowedToWrite());

                    assertThat(createdPersona.getUsersAllowedToRead())
                            .isEqualTo(expectedPersona.getUsersAllowedToRead());

                    assertThat(createdPersona.getPersonality()).isEqualTo(expectedPersona.getPersonality());
                    assertThat(createdPersona.getVisibility()).isEqualTo(expectedPersona.getVisibility());
                })
                .verifyComplete();
    }

    @Test
    public void createPersona_whenNudgeIsNull_thenCreatePersonaSuccessfully() {

        // Given
        String name = "MoirAI";
        String personality = "I am a chatbot";
        String visibility = "PRIVATE";

        Bump bump = BumpFixture.sample().build();

        Persona expectedPersona = PersonaFixture.privatePersona()
                .name(name)
                .personality(personality)
                .visibility(Visibility.fromString(visibility))
                .bump(bump)
                .build();

        CreatePersona command = CreatePersona.builder()
                .name(name)
                .personality(personality)
                .bumpContent(bump.getContent())
                .bumpRole(bump.getRole().toString())
                .bumpFrequency(bump.getFrequency())
                .visibility(visibility)
                .build();

        when(moderationPort.moderate(anyString()))
                .thenReturn(Mono.just(TextModerationResultFixture.withoutFlags().build()));

        when(repository.save(any(Persona.class))).thenReturn(expectedPersona);

        // Then
        StepVerifier.create(service.createFrom(command))
                .assertNext(createdPersona -> {
                    assertThat(createdPersona).isNotNull().isEqualTo(expectedPersona);
                    assertThat(createdPersona.getName()).isEqualTo(expectedPersona.getName());
                    assertThat(createdPersona.getOwnerDiscordId()).isEqualTo(expectedPersona.getOwnerDiscordId());
                    assertThat(createdPersona.getUsersAllowedToWrite())
                            .isEqualTo(expectedPersona.getUsersAllowedToWrite());

                    assertThat(createdPersona.getUsersAllowedToRead())
                            .isEqualTo(expectedPersona.getUsersAllowedToRead());

                    assertThat(createdPersona.getPersonality()).isEqualTo(expectedPersona.getPersonality());
                    assertThat(createdPersona.getVisibility()).isEqualTo(expectedPersona.getVisibility());
                })
                .verifyComplete();
    }

    @Test
    public void findPersona_whenValidId_thenReturnPersona() {

        // Given
        String id = "CHCONFID";

        Persona persona = PersonaFixture.privatePersona()
                .id(id)
                .name("New name")
                .build();

        when(repository.findById(anyString())).thenReturn(Optional.of(persona));

        // When
        Persona result = service.getById(id);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(persona.getName());
    }

    @Test
    public void findPersona_whenPersonaNotFound_thenThrowException() {

        // Given
        String id = "CHCONFID";

        when(repository.findById(anyString())).thenReturn(Optional.empty());

        // Then
        assertThrows(AssetNotFoundException.class, () -> service.getById(id));
    }

    @Test
    public void createPersona_whenContentIsFlagged_thenThrowException() {

        // Given
        String name = "MoirAI";
        String personality = "I am a chatbot";
        String visibility = "PRIVATE";

        Nudge nudge = NudgeFixture.sample().build();

        CreatePersona command = CreatePersona.builder()
                .name(name)
                .personality(personality)
                .nudgeContent(nudge.getContent())
                .nudgeRole(nudge.getRole().toString())
                .visibility(visibility)
                .build();

        when(moderationPort.moderate(anyString()))
                .thenReturn(Mono.just(TextModerationResultFixture.withFlags().build()));

        // Then
        StepVerifier.create(service.createFrom(command))
                .verifyError(ModerationException.class);
    }
}
