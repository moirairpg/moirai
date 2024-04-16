package es.thalesalv.chatrpg.infrastructure.outbound.adapter;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.User;
import es.thalesalv.chatrpg.core.application.query.discord.DiscordUserDetails;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class DiscordUserDetailsAdapterTest {

    @Mock
    private GatewayDiscordClient discordClient;

    @Mock
    private User mockUser;

    private DiscordUserDetailsAdapter adapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adapter = new DiscordUserDetailsAdapter(discordClient);
    }

    @Test
    void getUserById_whenCalled_thenShouldReturnUserDetails() {
        // Given
        String userDiscordId = "123456789";
        when(discordClient.getUserById(Snowflake.of(userDiscordId))).thenReturn(Mono.just(mockUser));
        when(mockUser.getId()).thenReturn(Snowflake.of(userDiscordId));
        when(mockUser.getGlobalName()).thenReturn(java.util.Optional.of("GlobalName"));
        when(mockUser.getUsername()).thenReturn("Username");
        when(mockUser.getMention()).thenReturn("@Username");

        // When
        Mono<DiscordUserDetails> result = adapter.getUserById(userDiscordId);

        // Then
        StepVerifier.create(result)
                .assertNext(userDetails -> {
                    assert userDetails.getId().equals(userDiscordId);
                    assert userDetails.getGlobalName().equals("GlobalName");
                    assert userDetails.getUsername().equals("Username");
                    assert userDetails.getMention().equals("@Username");
                })
                .verifyComplete();
    }
}