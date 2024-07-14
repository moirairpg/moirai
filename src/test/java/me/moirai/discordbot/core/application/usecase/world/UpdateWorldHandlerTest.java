package me.moirai.discordbot.core.application.usecase.world;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.moirai.discordbot.core.application.usecase.world.request.UpdateWorld;
import me.moirai.discordbot.core.domain.world.World;
import me.moirai.discordbot.core.domain.world.WorldFixture;
import me.moirai.discordbot.core.domain.world.WorldServiceImpl;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class UpdateWorldHandlerTest {

    @Mock
    private WorldServiceImpl service;

    @InjectMocks
    private UpdateWorldHandler handler;

    @Test
    public void updateWorld() {

        // Given
        String id = "WRDID";

        UpdateWorld command = UpdateWorld.builder()
                .id(id)
                .name("MoirAI")
                .description("This is an RPG world")
                .adventureStart("As you enter the city, people around you start looking at you.")
                .visibility("PUBLIC")
                .requesterDiscordId("CRTID")
                .build();

        World expectedUpdatedWorld = WorldFixture.privateWorld().build();

        when(service.update(any(UpdateWorld.class)))
                .thenReturn(Mono.just(expectedUpdatedWorld));

        // Then
        StepVerifier.create(handler.handle(command))
                .assertNext(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result.getLastUpdatedDateTime()).isEqualTo(expectedUpdatedWorld.getLastUpdateDate());
                })
                .verifyComplete();
    }
}
