package es.thalesalv.chatrpg.infrastructure.inbound.api.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import es.thalesalv.chatrpg.core.application.usecase.persona.request.CreatePersona;
import es.thalesalv.chatrpg.core.application.usecase.persona.request.DeletePersona;
import es.thalesalv.chatrpg.core.application.usecase.persona.request.UpdatePersona;
import es.thalesalv.chatrpg.infrastructure.inbound.api.request.CreatePersonaRequest;
import es.thalesalv.chatrpg.infrastructure.inbound.api.request.CreatePersonaRequestFixture;
import es.thalesalv.chatrpg.infrastructure.inbound.api.request.UpdatePersonaRequest;
import es.thalesalv.chatrpg.infrastructure.inbound.api.request.UpdatePersonaRequestFixture;

@ExtendWith(MockitoExtension.class)
public class PersonaRequestMapperTest {

    @InjectMocks
    private PersonaRequestMapper mapper;

    @Test
    public void creationRequestToCommand() {

        // Given
        String requesterId = "RQSTRID";
        CreatePersonaRequest request = CreatePersonaRequestFixture.createPrivatePersona().build();

        // When
        CreatePersona command = mapper.toCommand(request, requesterId);

        // Then
        assertThat(command).isNotNull();
        assertThat(command.getName()).isEqualTo(request.getName());
        assertThat(command.getPersonality()).isEqualTo(request.getPersonality());
        assertThat(command.getBumpContent()).isEqualTo(request.getBumpContent());
        assertThat(command.getBumpFrequency()).isEqualTo(request.getBumpFrequency());
        assertThat(command.getBumpRole()).isEqualTo(request.getBumpRole());
        assertThat(command.getNudgeContent()).isEqualTo(request.getNudgeContent());
        assertThat(command.getNudgeRole()).isEqualTo(request.getNudgeRole());
        assertThat(command.getVisibility()).isEqualTo(request.getVisibility());
        assertThat(command.getRequesterDiscordId()).isEqualTo(requesterId);
        assertThat(command.getUsersAllowedToWrite()).hasSameElementsAs(request.getUsersAllowedToWrite());
        assertThat(command.getUsersAllowedToRead()).hasSameElementsAs(request.getUsersAllowedToRead());
    }

    @Test
    public void updateRequestToCommand() {

        // Given
        String personaId = "WRLDID";
        String requesterId = "RQSTRID";
        UpdatePersonaRequest request = UpdatePersonaRequestFixture.privatePersona().build();

        // When
        UpdatePersona command = mapper.toCommand(request, personaId, requesterId);

        // Then
        assertThat(command).isNotNull();
        assertThat(command.getName()).isEqualTo(request.getName());
        assertThat(command.getPersonality()).isEqualTo(request.getPersonality());
        assertThat(command.getBumpContent()).isEqualTo(request.getBumpContent());
        assertThat(command.getBumpFrequency()).isEqualTo(request.getBumpFrequency());
        assertThat(command.getBumpRole()).isEqualTo(request.getBumpRole());
        assertThat(command.getNudgeContent()).isEqualTo(request.getNudgeContent());
        assertThat(command.getNudgeRole()).isEqualTo(request.getNudgeRole());
        assertThat(command.getVisibility()).isEqualTo(request.getVisibility());
        assertThat(command.getRequesterDiscordId()).isEqualTo(requesterId);
        assertThat(command.getUsersAllowedToWriteToAdd()).hasSameElementsAs(request.getUsersAllowedToWriteToAdd());
        assertThat(command.getUsersAllowedToWriteToRemove()).isEmpty();
        assertThat(command.getUsersAllowedToReadToAdd()).hasSameElementsAs(request.getUsersAllowedToReadToAdd());
        assertThat(command.getUsersAllowedToReadToRemove()).isEmpty();
    }

    @Test
    public void deleteRequestToCommand() {

        // Given
        String personaId = "WRLDID";
        String requesterId = "RQSTRID";

        // When
        DeletePersona command = mapper.toCommand(personaId, requesterId);

        // Then
        assertThat(command).isNotNull();
        assertThat(command.getId()).isEqualTo(personaId);
        assertThat(command.getRequesterDiscordId()).isEqualTo(requesterId);
    }
}
