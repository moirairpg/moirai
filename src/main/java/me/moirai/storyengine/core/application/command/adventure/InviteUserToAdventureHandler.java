package me.moirai.storyengine.core.application.command.adventure;

import java.util.ArrayList;

import org.springframework.context.ApplicationEventPublisher;

import me.moirai.storyengine.common.annotation.CommandHandler;
import me.moirai.storyengine.common.cqs.command.AbstractCommandHandler;
import me.moirai.storyengine.common.exception.BusinessRuleViolationException;
import me.moirai.storyengine.common.exception.NotFoundException;
import me.moirai.storyengine.core.port.inbound.adventure.InviteUserToAdventure;
import me.moirai.storyengine.core.port.inbound.adventure.InviteUserToAdventureResult;
import me.moirai.storyengine.core.port.outbound.adventure.AdventureRepository;
import me.moirai.storyengine.core.port.outbound.userdetails.UserRepository;

@CommandHandler
public class InviteUserToAdventureHandler
        extends AbstractCommandHandler<InviteUserToAdventure, InviteUserToAdventureResult> {

    private final AdventureRepository adventureRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    public InviteUserToAdventureHandler(
            AdventureRepository adventureRepository,
            UserRepository userRepository,
            ApplicationEventPublisher eventPublisher) {

        this.adventureRepository = adventureRepository;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void validate(InviteUserToAdventure command) {

        if (command.inviteeUsernames().isEmpty()) {
            throw new BusinessRuleViolationException("At least one username is required");
        }
    }

    @Override
    public InviteUserToAdventureResult execute(InviteUserToAdventure command) {

        var adventure = adventureRepository.findByPublicId(command.adventureId())
                .orElseThrow(() -> new NotFoundException("Adventure not found"));

        var resolved = userRepository.findAllByUsernameIn(command.inviteeUsernames());

        var invited = new ArrayList<String>();

        for (var invitee : resolved) {

            adventure.invite(invitee.getId());
            invited.add(invitee.getUsername());
        }

        adventureRepository.save(adventure);
        adventure.drainEvents().forEach(eventPublisher::publishEvent);

        return new InviteUserToAdventureResult(invited);
    }
}
