package me.moirai.storyengine.core.application.command.message;

import static org.apache.commons.lang3.StringUtils.isBlank;

import org.springframework.context.ApplicationEventPublisher;

import me.moirai.storyengine.common.annotation.Authorize;
import me.moirai.storyengine.common.annotation.CommandHandler;
import me.moirai.storyengine.common.cqs.command.AbstractCommandHandler;
import me.moirai.storyengine.common.dto.MessageSummary;
import me.moirai.storyengine.common.enums.MessageAuthorRole;
import me.moirai.storyengine.common.exception.NotFoundException;
import me.moirai.storyengine.common.security.authorization.AuthorizationOperation;
import me.moirai.storyengine.core.application.event.message.MessageSentEvent;
import me.moirai.storyengine.core.application.event.message.MessageTranscriptChangedEvent;
import me.moirai.storyengine.core.domain.message.Message;
import me.moirai.storyengine.core.port.inbound.message.SendMessage;
import me.moirai.storyengine.core.port.outbound.adventure.AdventureRepository;
import me.moirai.storyengine.core.port.outbound.adventure.EnrolledCharacterData;
import me.moirai.storyengine.core.port.outbound.message.AdventureMessageUpdate;
import me.moirai.storyengine.core.port.outbound.message.MessageRepository;
import me.moirai.storyengine.core.port.outbound.userdetails.UserRepository;

@CommandHandler
@Authorize(operation = AuthorizationOperation.PLAY_ADVENTURE, fields = "#request.adventureId")
public class SendMessageHandler extends AbstractCommandHandler<SendMessage, Void> {

    private final AdventureRepository adventureRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    public SendMessageHandler(
            AdventureRepository adventureRepository,
            MessageRepository messageRepository,
            UserRepository userRepository,
            ApplicationEventPublisher eventPublisher) {

        this.adventureRepository = adventureRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void validate(SendMessage command) {

        if (command.adventureId() == null) {
            throw new IllegalArgumentException("Adventure ID cannot be null");
        }

        if (isBlank(command.content())) {
            throw new IllegalArgumentException("Message content cannot be blank");
        }
    }

    @Override
    public Void execute(SendMessage command) {

        var adventure = adventureRepository.findByPublicId(command.adventureId())
                .orElseThrow(() -> new NotFoundException("Adventure not found"));

        var author = userRepository.findByUsername(command.username())
                .orElseThrow(() -> new NotFoundException("Message author not found"));

        var enrolledCharacter = adventureRepository
                .findEnrolledCharacter(adventure.getId(), command.username());

        var characterName = enrolledCharacter
                .map(EnrolledCharacterData::characterName)
                .orElse(command.username());

        var playerMessage = messageRepository.save(Message.builder()
                .adventureId(adventure.getId())
                .role(MessageAuthorRole.USER)
                .content(command.content())
                .authorId(author.getId())
                .authorCharacterId(enrolledCharacter.map(EnrolledCharacterData::playerCharacterId).orElse(null))
                .authorCharacterName(characterName)
                .build());

        eventPublisher.publishEvent(new MessageTranscriptChangedEvent(
                adventure.getPublicId(),
                AdventureMessageUpdate.messageAdded(new MessageSummary(
                        playerMessage.getPublicId(),
                        playerMessage.getRole(),
                        playerMessage.getContent(),
                        playerMessage.getStatus(),
                        author.getPublicId(),
                        playerMessage.getAuthorCharacterName(),
                        playerMessage.getCreationDate()), command.generateNarration())));

        if (command.generateNarration()) {
            eventPublisher.publishEvent(new MessageSentEvent(adventure.getPublicId()));
        }

        return null;
    }
}
