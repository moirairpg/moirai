package me.moirai.discordbot.core.application.usecase.persona;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.exception.AssetAccessDeniedException;
import me.moirai.discordbot.common.exception.ModerationException;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.port.TextModerationPort;
import me.moirai.discordbot.core.application.usecase.persona.request.UpdatePersona;
import me.moirai.discordbot.core.application.usecase.persona.result.UpdatePersonaResult;
import me.moirai.discordbot.core.domain.CompletionRole;
import me.moirai.discordbot.core.domain.Visibility;
import me.moirai.discordbot.core.domain.channelconfig.Moderation;
import me.moirai.discordbot.core.domain.persona.Persona;
import me.moirai.discordbot.core.domain.persona.PersonaDomainRepository;
import me.moirai.discordbot.core.domain.persona.PersonaService;
import reactor.core.publisher.Mono;

@UseCaseHandler
public class UpdatePersonaHandler extends AbstractUseCaseHandler<UpdatePersona, Mono<UpdatePersonaResult>> {

    private static final String PERSONA_FLAGGED_BY_MODERATION = "Persona flagged by moderation";
    private static final String ID_CANNOT_BE_NULL_OR_EMPTY = "Persona ID cannot be null or empty";
    private static final String PERMISSION_MODIFY_DENIED = "User does not have permission to modify this persona";

    private final PersonaDomainRepository repository;
    private final PersonaService domainService;
    private final TextModerationPort moderationPort;

    public UpdatePersonaHandler(PersonaDomainRepository repository,
            PersonaService domainService,
            TextModerationPort moderationPort) {

        this.repository = repository;
        this.domainService = domainService;
        this.moderationPort = moderationPort;
    }

    @Override
    public void validate(UpdatePersona command) {

        if (StringUtils.isBlank(command.getId())) {
            throw new IllegalArgumentException(ID_CANNOT_BE_NULL_OR_EMPTY);
        }
    }

    @Override
    public Mono<UpdatePersonaResult> execute(UpdatePersona command) {

        return moderateContent(command.getPersonality())
                .flatMap(__ -> moderateContent(command.getName()))
                .map(__ -> {
                    Persona persona = domainService.getById(command.getId());
                    if (!persona.canUserWrite(command.getRequesterDiscordId())) {
                        throw new AssetAccessDeniedException(PERMISSION_MODIFY_DENIED);
                    }

                    return persona;
                })
                .map(persona -> updatePersona(command, persona))
                .map(personaUpdated -> UpdatePersonaResult.build(personaUpdated.getLastUpdateDate()));
    }

    private Persona updatePersona(UpdatePersona command, Persona persona) {

        if (!persona.canUserWrite(command.getRequesterDiscordId())) {
            throw new AssetAccessDeniedException(PERMISSION_MODIFY_DENIED);
        }

        if (StringUtils.isNotBlank(command.getName())) {
            persona.updateName(command.getName());
        }

        if (StringUtils.isNotBlank(command.getPersonality())) {
            persona.updatePersonality(command.getPersonality());
        }

        if (StringUtils.isNotBlank(command.getNudgeRole())) {
            persona.updateNudgeRole(CompletionRole.fromString(command.getNudgeRole()));
        }

        if (StringUtils.isNotBlank(command.getNudgeContent())) {
            persona.updateNudgeContent(command.getNudgeContent());
        }

        if (StringUtils.isNotBlank(command.getBumpRole())) {
            persona.updateBumpRole(CompletionRole.fromString(command.getBumpRole()));
        }

        if (StringUtils.isNotBlank(command.getBumpContent())) {
            persona.updateBumpContent(command.getBumpContent());
        }

        if (command.getBumpFrequency() != null) {
            persona.updateBumpFrequency(command.getBumpFrequency());
        }

        if (StringUtils.isNotBlank(command.getVisibility())) {
            if (command.getVisibility().equalsIgnoreCase(Visibility.PUBLIC.name())) {
                persona.makePublic();
            } else if (command.getVisibility().equalsIgnoreCase(Visibility.PRIVATE.name())) {
                persona.makePrivate();
            }
        }

        CollectionUtils.emptyIfNull(command.getUsersAllowedToReadToAdd())
                .stream()
                .filter(userId -> !persona.canUserRead(userId))
                .forEach(persona::addReaderUser);

        CollectionUtils.emptyIfNull(command.getUsersAllowedToWriteToAdd())
                .stream()
                .filter(userId -> !persona.canUserWrite(userId))
                .forEach(persona::addWriterUser);

        CollectionUtils.emptyIfNull(command.getUsersAllowedToReadToRemove())
                .forEach(persona::removeReaderUser);

        CollectionUtils.emptyIfNull(command.getUsersAllowedToWriteToRemove())
                .forEach(persona::removeWriterUser);

        return repository.save(persona);
    }

    private Mono<List<String>> moderateContent(String personality) {

        if (StringUtils.isBlank(personality)) {
            return Mono.just(Collections.emptyList());
        }

        return getTopicsFlaggedByModeration(personality)
                .map(flaggedTopics -> {
                    if (CollectionUtils.isNotEmpty(flaggedTopics)) {
                        throw new ModerationException(PERSONA_FLAGGED_BY_MODERATION, flaggedTopics);
                    }

                    return flaggedTopics;
                });
    }

    private Mono<List<String>> getTopicsFlaggedByModeration(String input) {

        return moderationPort.moderate(input)
                .map(result -> result.getModerationScores()
                        .entrySet()
                        .stream()
                        .filter(this::isTopicFlagged)
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList()));
    }

    private boolean isTopicFlagged(Entry<String, Double> entry) {
        return entry.getValue() > Moderation.PERMISSIVE.getThresholds().get(entry.getKey());
    }
}
