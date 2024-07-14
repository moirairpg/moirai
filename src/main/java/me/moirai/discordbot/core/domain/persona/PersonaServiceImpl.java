package me.moirai.discordbot.core.domain.persona;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import me.moirai.discordbot.common.annotation.DomainService;
import me.moirai.discordbot.common.exception.AssetAccessDeniedException;
import me.moirai.discordbot.common.exception.AssetNotFoundException;
import me.moirai.discordbot.common.exception.ModerationException;
import me.moirai.discordbot.core.application.port.TextModerationPort;
import me.moirai.discordbot.core.application.usecase.persona.request.CreatePersona;
import me.moirai.discordbot.core.application.usecase.persona.request.DeletePersona;
import me.moirai.discordbot.core.application.usecase.persona.request.GetPersonaById;
import me.moirai.discordbot.core.application.usecase.persona.request.UpdatePersona;
import me.moirai.discordbot.core.domain.CompletionRole;
import me.moirai.discordbot.core.domain.Permissions;
import me.moirai.discordbot.core.domain.Visibility;
import me.moirai.discordbot.core.domain.channelconfig.Moderation;
import reactor.core.publisher.Mono;

@DomainService
public class PersonaServiceImpl implements PersonaService {

    private static final String PERSONA_FLAGGED_BY_MODERATION = "Persona flagged by moderation";
    private static final String PERSONA_NOT_FOUND = "Persona was not found";
    private static final String PERMISSION_VIEW_DENIED = "User does not have permission to view this persona";
    private static final String PERMISSION_MODIFY_DENIED = "User does not have permission to modify this persona";

    private final TextModerationPort moderationPort;
    private final PersonaRepository repository;

    public PersonaServiceImpl(TextModerationPort moderationPort, PersonaRepository repository) {
        this.moderationPort = moderationPort;
        this.repository = repository;
    }

    @Override
    public Persona getPersonaById(GetPersonaById query) {

        Persona persona = repository.findById(query.getId())
                .orElseThrow(() -> new AssetNotFoundException(PERSONA_NOT_FOUND));

        if (!persona.canUserRead(query.getRequesterDiscordId())) {
            throw new AssetAccessDeniedException(PERMISSION_VIEW_DENIED);
        }

        return persona;
    }

    @Override
    public Persona getPersonaById(String id) {

        Persona persona = repository.findById(id)
                .orElseThrow(() -> new AssetNotFoundException(PERSONA_NOT_FOUND));

        return persona;
    }

    @Override
    public void deletePersona(DeletePersona command) {

        Persona persona = repository.findById(command.getId())
                .orElseThrow(() -> new AssetNotFoundException(PERSONA_NOT_FOUND));

        if (!persona.canUserWrite(command.getRequesterDiscordId())) {
            throw new AssetAccessDeniedException(PERMISSION_MODIFY_DENIED);
        }

        repository.deleteById(command.getId());
    }

    @Override
    public Mono<Persona> createFrom(CreatePersona command) {

        return moderateContent(command.getPersonality())
                .flatMap(__ -> moderateContent(command.getName()))
                .map(__ -> {
                    Persona.Builder personaBuilder = Persona.builder();
                    if (StringUtils.isNotBlank(command.getBumpContent())) {
                        Bump bump = Bump.builder()
                                .content(command.getBumpContent())
                                .frequency(command.getBumpFrequency())
                                .role(CompletionRole.fromString(command.getBumpRole()))
                                .build();

                        personaBuilder.bump(bump);
                    }

                    if (StringUtils.isNotBlank(command.getNudgeContent())) {
                        Nudge nudge = Nudge.builder()
                                .content(command.getNudgeContent())
                                .role(CompletionRole.fromString(command.getNudgeRole()))
                                .build();

                        personaBuilder.nudge(nudge);
                    }

                    Permissions permissions = Permissions.builder()
                            .ownerDiscordId(command.getRequesterDiscordId())
                            .usersAllowedToRead(command.getUsersAllowedToRead())
                            .usersAllowedToWrite(command.getUsersAllowedToWrite())
                            .build();

                    Persona persona = personaBuilder.name(command.getName())
                            .personality(command.getPersonality())
                            .visibility(Visibility.fromString(command.getVisibility()))
                            .permissions(permissions)
                            .gameMode(GameMode.fromString(command.getGameMode()))
                            .build();

                    return repository.save(persona);
                });
    }

    @Override
    public Mono<Persona> update(UpdatePersona command) {

        return moderateContent(command.getPersonality())
                .flatMap(__ -> moderateContent(command.getName()))
                .map(__ -> {
                    Persona persona = repository.findById(command.getId())
                            .orElseThrow(() -> new AssetNotFoundException(PERSONA_NOT_FOUND));

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

                    if (StringUtils.isNotBlank(command.getGameMode())) {
                        persona.updateGameMode(GameMode.fromString(command.getGameMode()));
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
                });
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
