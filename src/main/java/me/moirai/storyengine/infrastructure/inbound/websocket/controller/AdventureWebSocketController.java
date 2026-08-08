package me.moirai.storyengine.infrastructure.inbound.websocket.controller;

import java.security.Principal;
import java.util.UUID;
import java.util.function.Function;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;

import me.moirai.storyengine.common.cqs.command.Command;
import me.moirai.storyengine.common.cqs.command.CommandRunner;
import me.moirai.storyengine.common.security.authentication.MoiraiPrincipal;
import me.moirai.storyengine.common.security.authentication.MoiraiSecurityContext;
import me.moirai.storyengine.core.port.inbound.message.DeleteMessage;
import me.moirai.storyengine.core.port.inbound.message.EditMessage;
import me.moirai.storyengine.core.port.inbound.message.EditMessageAndGenerateOutput;
import me.moirai.storyengine.core.port.inbound.message.Go;
import me.moirai.storyengine.core.port.inbound.message.Retry;
import me.moirai.storyengine.core.port.inbound.message.RetryFromMessage;
import me.moirai.storyengine.core.port.inbound.message.Say;
import me.moirai.storyengine.core.port.inbound.message.SendMessage;
import me.moirai.storyengine.core.port.inbound.message.StartAdventure;
import me.moirai.storyengine.infrastructure.inbound.websocket.request.WebSocketMessageRequest;
import me.moirai.storyengine.infrastructure.inbound.websocket.response.WebSocketErrorResponse;

@Controller
public class AdventureWebSocketController {

    private static final String ERROR_QUEUE = "/queue/adventures/errors";

    private final CommandRunner commandRunner;

    public AdventureWebSocketController(CommandRunner commandRunner) {
        this.commandRunner = commandRunner;
    }

    @MessageMapping("/adventures/{adventureId}/messages")
    public void sendMessage(
            @DestinationVariable UUID adventureId,
            @Payload WebSocketMessageRequest request,
            Principal principal) {

        dispatch(principal, username -> new SendMessage(adventureId, request.content(), username));
    }

    @MessageMapping("/adventures/{adventureId}/start")
    public void start(@DestinationVariable UUID adventureId, Principal principal) {

        dispatch(principal, username -> new StartAdventure(adventureId));
    }

    @MessageMapping("/adventures/{adventureId}/go")
    public void go(@DestinationVariable UUID adventureId, Principal principal) {

        dispatch(principal, username -> new Go(adventureId));
    }

    @MessageMapping("/adventures/{adventureId}/retry")
    public void retry(@DestinationVariable UUID adventureId, Principal principal) {

        dispatch(principal, username -> new Retry(adventureId));
    }

    @MessageMapping("/adventures/{adventureId}/say")
    public void say(
            @DestinationVariable UUID adventureId,
            @Payload WebSocketMessageRequest request,
            Principal principal) {

        dispatch(principal, username -> new Say(adventureId, request.content()));
    }

    @MessageMapping("/adventures/{adventureId}/messages/{messageId}/retry")
    public void retryFromMessage(
            @DestinationVariable UUID adventureId,
            @DestinationVariable UUID messageId,
            Principal principal) {

        dispatch(principal, username -> new RetryFromMessage(adventureId, messageId));
    }

    @MessageMapping("/adventures/{adventureId}/messages/{messageId}/edit")
    public void editMessage(
            @DestinationVariable UUID adventureId,
            @DestinationVariable UUID messageId,
            @Payload WebSocketMessageRequest request,
            Principal principal) {

        dispatch(principal, username -> new EditMessage(adventureId, messageId, request.content()));
    }

    @MessageMapping("/adventures/{adventureId}/messages/{messageId}/edit-and-generate")
    public void editMessageAndGenerateOutput(
            @DestinationVariable UUID adventureId,
            @DestinationVariable UUID messageId,
            @Payload WebSocketMessageRequest request,
            Principal principal) {

        dispatch(principal, username -> new EditMessageAndGenerateOutput(
                adventureId, messageId, request.content()));
    }

    @MessageMapping("/adventures/{adventureId}/messages/{messageId}/delete")
    public void deleteMessage(
            @DestinationVariable UUID adventureId,
            @DestinationVariable UUID messageId,
            Principal principal) {

        dispatch(principal, username -> new DeleteMessage(adventureId, messageId));
    }

    @MessageExceptionHandler
    @SendToUser(ERROR_QUEUE)
    public WebSocketErrorResponse onFailure(Exception exception) {

        return new WebSocketErrorResponse(exception.getMessage());
    }

    private void dispatch(Principal principal, Function<String, Command<Void>> commandFactory) {

        var auth = (UsernamePasswordAuthenticationToken) principal;
        var moiraiPrincipal = (MoiraiPrincipal) auth.getPrincipal();

        try {
            MoiraiSecurityContext.set(moiraiPrincipal);
            commandRunner.run(commandFactory.apply(moiraiPrincipal.username()));
        } finally {
            MoiraiSecurityContext.clear();
        }
    }
}
