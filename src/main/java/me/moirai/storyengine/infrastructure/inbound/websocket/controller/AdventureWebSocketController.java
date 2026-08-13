package me.moirai.storyengine.infrastructure.inbound.websocket.controller;

import java.util.UUID;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import me.moirai.storyengine.common.cqs.command.CommandRunner;
import me.moirai.storyengine.common.web.SecurityContextAware;
import me.moirai.storyengine.core.port.inbound.message.DeleteMessage;
import me.moirai.storyengine.core.port.inbound.message.EditMessage;
import me.moirai.storyengine.core.port.inbound.message.EditMessageAndGenerateOutput;
import me.moirai.storyengine.core.port.inbound.message.Go;
import me.moirai.storyengine.core.port.inbound.message.Retry;
import me.moirai.storyengine.core.port.inbound.message.RetryFromMessage;
import me.moirai.storyengine.core.port.inbound.message.Say;
import me.moirai.storyengine.core.port.inbound.message.SendMessage;
import me.moirai.storyengine.core.port.inbound.message.StartAdventure;
import me.moirai.storyengine.infrastructure.inbound.websocket.request.SendMessageRequest;
import me.moirai.storyengine.infrastructure.inbound.websocket.request.WebSocketMessageRequest;
import me.moirai.storyengine.infrastructure.inbound.websocket.response.WebSocketErrorResponse;

@Controller
public class AdventureWebSocketController extends SecurityContextAware {

    private static final String ERROR_QUEUE = "/queue/adventures/errors";

    private final CommandRunner commandRunner;

    public AdventureWebSocketController(CommandRunner commandRunner) {
        this.commandRunner = commandRunner;
    }

    @MessageMapping("/adventures/{adventureId}/messages")
    public void sendMessage(
            @DestinationVariable UUID adventureId,
            @Payload SendMessageRequest request) {

        commandRunner.run(new SendMessage(
                adventureId, request.content(), authenticatedUsername(), request.generateNarration()));
    }

    @MessageMapping("/adventures/{adventureId}/start")
    public void start(@DestinationVariable UUID adventureId) {

        commandRunner.run(new StartAdventure(adventureId));
    }

    @MessageMapping("/adventures/{adventureId}/go")
    public void go(@DestinationVariable UUID adventureId) {

        commandRunner.run(new Go(adventureId));
    }

    @MessageMapping("/adventures/{adventureId}/retry")
    public void retry(@DestinationVariable UUID adventureId) {

        commandRunner.run(new Retry(adventureId));
    }

    @MessageMapping("/adventures/{adventureId}/say")
    public void say(
            @DestinationVariable UUID adventureId,
            @Payload WebSocketMessageRequest request) {

        commandRunner.run(new Say(adventureId, request.content()));
    }

    @MessageMapping("/adventures/{adventureId}/messages/{messageId}/retry")
    public void retryFromMessage(
            @DestinationVariable UUID adventureId,
            @DestinationVariable UUID messageId) {

        commandRunner.run(new RetryFromMessage(adventureId, messageId));
    }

    @MessageMapping("/adventures/{adventureId}/messages/{messageId}/edit")
    public void editMessage(
            @DestinationVariable UUID adventureId,
            @DestinationVariable UUID messageId,
            @Payload WebSocketMessageRequest request) {

        commandRunner.run(new EditMessage(adventureId, messageId, request.content()));
    }

    @MessageMapping("/adventures/{adventureId}/messages/{messageId}/edit-and-generate")
    public void editMessageAndGenerateOutput(
            @DestinationVariable UUID adventureId,
            @DestinationVariable UUID messageId,
            @Payload WebSocketMessageRequest request) {

        commandRunner.run(new EditMessageAndGenerateOutput(
                adventureId, messageId, request.content()));
    }

    @MessageMapping("/adventures/{adventureId}/messages/{messageId}/delete")
    public void deleteMessage(
            @DestinationVariable UUID adventureId,
            @DestinationVariable UUID messageId) {

        commandRunner.run(new DeleteMessage(adventureId, messageId));
    }

    @MessageExceptionHandler
    @SendToUser(ERROR_QUEUE)
    public WebSocketErrorResponse onFailure(Exception exception) {

        return new WebSocketErrorResponse(exception.getMessage());
    }
}
