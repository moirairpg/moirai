package me.moirai.storyengine.infrastructure.inbound.websocket.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.security.Principal;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import me.moirai.storyengine.common.cqs.command.CommandRunner;
import me.moirai.storyengine.common.security.authentication.MoiraiPrincipal;
import me.moirai.storyengine.common.security.authentication.MoiraiSecurityContext;
import me.moirai.storyengine.core.port.inbound.message.SendMessage;
import me.moirai.storyengine.infrastructure.inbound.websocket.request.WebSocketMessageRequest;

@ExtendWith(MockitoExtension.class)
public class AdventureWebSocketControllerTest {

    @Mock
    private CommandRunner commandRunner;

    @InjectMocks
    private AdventureWebSocketController controller;

    @Test
    void shouldRunCommandWhenPayloadIsValid() {

        // given
        var adventureId = UUID.randomUUID();
        var request = new WebSocketMessageRequest("hello");
        var moiraiPrincipal = new MoiraiPrincipal(UUID.randomUUID(), 99999L, "discordId",
                "alice", "alice@test.com", "token", "refresh", null, null);
        var principal = new UsernamePasswordAuthenticationToken(moiraiPrincipal, null);

        // when
        controller.sendMessage(adventureId, request, principal);

        // then
        var commandCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(commandRunner).run(commandCaptor.capture());
        assertThat(commandCaptor.getValue().adventureId()).isEqualTo(adventureId);
        assertThat(commandCaptor.getValue().content()).isEqualTo("hello");
        assertThat(commandCaptor.getValue().username()).isEqualTo("alice");
    }

    @AfterEach
    void tearDown() {
        MoiraiSecurityContext.clear();
    }

    @Test
    void shouldExposeThePrincipalThroughMoiraiSecurityContextWhileTheCommandRuns() {

        // given
        var moiraiPrincipal = principal();
        var seenDuringCommand = new AtomicReference<MoiraiPrincipal>();

        doAnswer(invocation -> {
            seenDuringCommand.set(MoiraiSecurityContext.getAuthenticatedUser());
            return null;
        }).when(commandRunner).run(any());

        // when
        controller.sendMessage(
                UUID.randomUUID(),
                new WebSocketMessageRequest("hello"),
                new UsernamePasswordAuthenticationToken(moiraiPrincipal, null));

        // then
        assertThat(seenDuringCommand.get()).isSameAs(moiraiPrincipal);
    }

    @Test
    void shouldClearTheSecurityContextAfterTheCommandSucceeds() {

        // when
        controller.sendMessage(
                UUID.randomUUID(),
                new WebSocketMessageRequest("hello"),
                new UsernamePasswordAuthenticationToken(principal(), null));

        // then
        assertThat(MoiraiSecurityContext.getAuthenticatedUser()).isNull();
    }

    @Test
    void shouldClearTheSecurityContextWhenTheCommandThrows() {

        // given
        doThrow(new IllegalStateException("boom")).when(commandRunner).run(any());

        // when
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> controller.sendMessage(
                        UUID.randomUUID(),
                        new WebSocketMessageRequest("hello"),
                        new UsernamePasswordAuthenticationToken(principal(), null)));

        // then
        assertThat(MoiraiSecurityContext.getAuthenticatedUser()).isNull();
    }

    @Test
    void shouldFailWithClassCastExceptionWhenThePrincipalIsNotAUsernamePasswordAuthenticationToken() {

        // given
        Principal foreignPrincipal = () -> "alice";

        // when
        assertThatExceptionOfType(ClassCastException.class)
                .isThrownBy(() -> controller.sendMessage(
                        UUID.randomUUID(), new WebSocketMessageRequest("hello"), foreignPrincipal));

        // then
        verifyNoInteractions(commandRunner);
    }

    @Test
    void shouldFailWithClassCastExceptionWhenTheWrappedPrincipalIsNotAMoiraiPrincipal() {

        // given
        var tokenWithForeignPrincipal = new UsernamePasswordAuthenticationToken("alice", null);

        // when
        assertThatExceptionOfType(ClassCastException.class)
                .isThrownBy(() -> controller.sendMessage(
                        UUID.randomUUID(),
                        new WebSocketMessageRequest("hello"),
                        tokenWithForeignPrincipal));

        // then
        verifyNoInteractions(commandRunner);
    }

    private MoiraiPrincipal principal() {
        return new MoiraiPrincipal(UUID.randomUUID(), 99999L, "discordId",
                "alice", "alice@test.com", "token", "refresh", null, null);
    }
}
