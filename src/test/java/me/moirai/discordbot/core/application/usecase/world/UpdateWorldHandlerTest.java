package me.moirai.discordbot.core.application.usecase.world;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.moirai.discordbot.common.exception.AssetAccessDeniedException;
import me.moirai.discordbot.common.exception.ModerationException;
import me.moirai.discordbot.core.application.model.result.TextModerationResultFixture;
import me.moirai.discordbot.core.application.port.TextModerationPort;
import me.moirai.discordbot.core.application.usecase.world.request.UpdateWorld;
import me.moirai.discordbot.core.domain.PermissionsFixture;
import me.moirai.discordbot.core.domain.Visibility;
import me.moirai.discordbot.core.domain.world.World;
import me.moirai.discordbot.core.domain.world.WorldDomainRepository;
import me.moirai.discordbot.core.domain.world.WorldFixture;
import me.moirai.discordbot.core.domain.world.WorldService;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class UpdateWorldHandlerTest {

    @Mock
    private WorldDomainRepository repository;

    @Mock
    private WorldService service;

    @Mock
    private TextModerationPort moderationPort;

    @InjectMocks
    private UpdateWorldHandler handler;

    @Test
    public void updateWorld() {

        // Given
        String id = "WRDID";
        String requesterId = "RQSTRID";
        String newName = "NEW NAME";

        UpdateWorld command = UpdateWorld.builder()
                .id(id)
                .name("MoirAI")
                .description("This is an RPG world")
                .adventureStart("As you enter the city, people around you start looking at you.")
                .visibility("PUBLIC")
                .requesterDiscordId(requesterId)
                .build();

        World expectedUpdatedWorld = WorldFixture.privateWorld()
                .id(id)
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(requesterId)
                        .build())
                .build();

        World unchangedWorld = WorldFixture.privateWorld()
                .id(id)
                .name(newName)
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(requesterId)
                        .build())
                .build();

        when(service.getWorldById(anyString())).thenReturn(unchangedWorld);
        when(repository.save(any(World.class))).thenReturn(expectedUpdatedWorld);
        when(moderationPort.moderate(anyString()))
                .thenReturn(Mono.just(TextModerationResultFixture.withoutFlags().build()));

        // Then
        StepVerifier.create(handler.handle(command))
                .assertNext(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result.getLastUpdatedDateTime()).isEqualTo(expectedUpdatedWorld.getLastUpdateDate());
                })
                .verifyComplete();
    }

    @Test
    public void updateWorld_whenValidData_thenWorldIsUpdated() {

        // Given
        String id = "CHCONFID";

        UpdateWorld command = UpdateWorld.builder()
                .id(id)
                .name("MoirAI")
                .description("This is an RPG world")
                .adventureStart("As you enter the city, people around you start looking at you.")
                .visibility("PUBLIC")
                .requesterDiscordId("586678721356875")
                .build();

        World unchangedWorld = WorldFixture.privateWorld().build();

        World expectedUpdatedWorld = WorldFixture.privateWorld()
                .id(id)
                .name("MoirAI")
                .description("This is an RPG world")
                .adventureStart("As you enter the city, people around you start looking at you.")
                .visibility(Visibility.PUBLIC)
                .build();

        when(moderationPort.moderate(anyString()))
                .thenReturn(Mono.just(TextModerationResultFixture.withoutFlags().build()));

        when(service.getWorldById(anyString())).thenReturn(unchangedWorld);
        when(repository.save(any(World.class))).thenReturn(expectedUpdatedWorld);
        when(moderationPort.moderate(anyString()))
                .thenReturn(Mono.just(TextModerationResultFixture.withoutFlags().build()));

        // Then
        StepVerifier.create(handler.handle(command))
                .assertNext(result -> {
                    assertThat(result).isNotNull();
                }).verifyComplete();
    }

    @Test
    public void updateWorld_whenEmptyUpdateFields_thenWorldIsNotChanged() {

        // Given
        String id = "CHCONFID";

        UpdateWorld command = UpdateWorld.builder()
                .id(id)
                .name(null)
                .description(null)
                .adventureStart(null)
                .visibility(null)
                .requesterDiscordId("586678721356875")
                .build();

        World unchangedWorld = WorldFixture.privateWorld().build();

        when(service.getWorldById(anyString())).thenReturn(unchangedWorld);
        when(repository.save(any(World.class))).thenReturn(unchangedWorld);

        // Then
        StepVerifier.create(handler.handle(command))
                .assertNext(result -> {
                    assertThat(result).isNotNull();
                }).verifyComplete();
    }

    @Test
    public void updateWorld_whenPublicToBeMadePrivate_thenWorldIsMadePrivate() {

        // Given
        String id = "CHCONFID";

        UpdateWorld command = UpdateWorld.builder()
                .id(id)
                .visibility("private")
                .requesterDiscordId("586678721356875")
                .build();

        World unchangedWorld = WorldFixture.publicWorld().build();
        World expectedWorld = WorldFixture.privateWorld().build();

        when(service.getWorldById(anyString())).thenReturn(unchangedWorld);
        when(repository.save(any(World.class))).thenReturn(expectedWorld);

        // Then
        StepVerifier.create(handler.handle(command))
                .assertNext(result -> {
                    assertThat(result).isNotNull();
                }).verifyComplete();
    }

    @Test
    public void updateWorld_whenInvalidVisibility_thenNothingIsChanged() {

        // Given
        String id = "CHCONFID";

        UpdateWorld command = UpdateWorld.builder()
                .id(id)
                .visibility("invalid")
                .requesterDiscordId("586678721356875")
                .build();

        World unchangedWorld = WorldFixture.privateWorld().build();

        when(service.getWorldById(anyString())).thenReturn(unchangedWorld);
        when(repository.save(any(World.class))).thenReturn(unchangedWorld);

        // Then
        StepVerifier.create(handler.handle(command))
                .assertNext(result -> {
                    assertThat(result).isNotNull();
                }).verifyComplete();
    }

    @Test
    public void updateWorld_whenNotEnoughPermission_thenThrowException() {

        // Given
        String id = "CHCONFID";

        UpdateWorld command = UpdateWorld.builder()
                .id(id)
                .requesterDiscordId("USRID")
                .build();

        World world = WorldFixture.privateWorld()
                .id(id)
                .name("New name")
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId("ANTHRUSR")
                        .build())
                .build();

        when(service.getWorldById(anyString())).thenReturn(world);

        // Then
        StepVerifier.create(handler.handle(command))
                .verifyError(AssetAccessDeniedException.class);
    }

    @Test
    public void updateWorld_whenInvalidPermission_thenThrowException() {

        // Given
        String id = "CHCONFID";

        UpdateWorld command = UpdateWorld.builder()
                .id(id)
                .name("MoirAI")
                .description("This is an RPG world")
                .adventureStart("As you enter the city, people around you start looking at you.")
                .visibility("PUBLIC")
                .requesterDiscordId("INVLDUSR")
                .build();

        World unchangedWorld = WorldFixture.privateWorld().build();

        when(moderationPort.moderate(anyString()))
                .thenReturn(Mono.just(TextModerationResultFixture.withoutFlags().build()));

        when(service.getWorldById(anyString())).thenReturn(unchangedWorld);

        // Then
        StepVerifier.create(handler.handle(command))
                .verifyError(AssetAccessDeniedException.class);
    }

    @Test
    public void updateWorld_whenContentIsFlagged_thenExceptionIsThrown() {

        // Given
        String id = "CHCONFID";
        UpdateWorld command = UpdateWorld.builder()
                .id(id)
                .name("MoirAI")
                .description("This is an RPG world")
                .adventureStart("As you enter the city, people around you start looking at you.")
                .visibility("PUBLIC")
                .requesterDiscordId("586678721356875")
                .build();

        when(moderationPort.moderate(anyString()))
                .thenReturn(Mono.just(TextModerationResultFixture.withFlags().build()));

        // Then
        StepVerifier.create(handler.handle(command))
                .verifyError(ModerationException.class);
    }
}
