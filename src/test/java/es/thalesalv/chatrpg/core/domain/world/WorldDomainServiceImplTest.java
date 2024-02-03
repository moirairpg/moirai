package es.thalesalv.chatrpg.core.domain.world;

import static es.thalesalv.chatrpg.core.domain.Visibility.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import es.thalesalv.chatrpg.common.exception.BusinessRuleViolationException;
import es.thalesalv.chatrpg.core.domain.PermissionsFixture;
import es.thalesalv.chatrpg.core.domain.Permissions;
import es.thalesalv.chatrpg.core.domain.Visibility;
import es.thalesalv.chatrpg.core.domain.port.TokenizerPort;

@SuppressWarnings("null")
@ExtendWith(MockitoExtension.class)
public class WorldDomainServiceImplTest {

    @Mock
    private WorldRepository repository;

    @Mock
    private TokenizerPort tokenizerPort;

    @InjectMocks
    private WorldDomainServiceImpl service;

    @Test
    public void createWorldSuccessfully() {

        // Given
        String name = "Eldrida";
        String description = "Eldrida is a fantasy world";
        String initialPrompt = "You have arrived at the world of Eldrida.";
        Permissions permissions = PermissionsFixture.samplePermissions().build();
        Visibility visibility = PRIVATE;

        World.Builder expectedWorldBuilder = WorldFixture.publicWorld()
                .name(name)
                .description(description)
                .initialPrompt(initialPrompt)
                .visibility(visibility)
                .permissions(permissions);

        World expectedWorld = expectedWorldBuilder.build();

        when(repository.save(any(World.class))).thenReturn(expectedWorld);

        // When
        World createdWorld = service.createWorld(expectedWorldBuilder);

        // Then
        assertThat(createdWorld).isNotNull().isEqualTo(expectedWorld);
        assertThat(createdWorld.getName()).isEqualTo(expectedWorld.getName());
        assertThat(createdWorld.getPermissions()).isEqualTo(expectedWorld.getPermissions());
        assertThat(createdWorld.getDescription()).isEqualTo(expectedWorld.getDescription());
        assertThat(createdWorld.getInitialPrompt()).isEqualTo(expectedWorld.getInitialPrompt());
        assertThat(createdWorld.getVisibility()).isEqualTo(expectedWorld.getVisibility());
    }

    @Test
    public void errorWhenInitialPromptTokenLimitIsSurpassed() {

        // Given
        String name = "Eldrida";
        String description = "Eldrida is a fantasy world";
        String initialPrompt = "You have arrived at the world of Eldrida.";
        Permissions permissions = PermissionsFixture.samplePermissions().build();
        Visibility visibility = PRIVATE;

        World.Builder expectedWorldBuilder = WorldFixture.publicWorld()
                .name(name)
                .description(description)
                .initialPrompt(initialPrompt)
                .visibility(visibility)
                .permissions(permissions);

        ReflectionTestUtils.setField(service, "initialPromptTokenLimit", 2);
        when(tokenizerPort.getTokenCountFrom(anyString())).thenReturn(10);

        // Then
        assertThrows(BusinessRuleViolationException.class, () -> service.createWorld(expectedWorldBuilder));
    }
}
