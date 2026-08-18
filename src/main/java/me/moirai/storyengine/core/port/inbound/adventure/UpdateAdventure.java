package me.moirai.storyengine.core.port.inbound.adventure;

import java.util.List;
import java.util.UUID;

import me.moirai.storyengine.common.cqs.command.Command;
import me.moirai.storyengine.common.enums.Moderation;
import me.moirai.storyengine.common.util.Functions;

public record UpdateAdventure(
        UUID adventureId,
        String name,
        String description,
        String adventureStart,
        String narratorName,
        String narratorPersonality,
        Moderation moderation,
        Boolean rpgMechanicsEnabled,
        Double uiImagePositionX,
        Double uiImagePositionY,
        ModelConfigurationDto modelConfiguration,
        ContextAttributesDto contextAttributes,
        List<LorebookEntryToAdd> lorebookEntriesToAdd,
        List<LorebookEntryToUpdate> lorebookEntriesToUpdate,
        List<UUID> lorebookEntriesToDelete,
        UUID requesterId)
        implements Command<AdventureDetails> {

    public record LorebookEntryToAdd(String name, String description) {}

    public record LorebookEntryToUpdate(UUID id, String name, String description) {}

    public UpdateAdventure {
        lorebookEntriesToAdd = Functions.mapOrDefault(lorebookEntriesToAdd, List.of(), List::copyOf);
        lorebookEntriesToUpdate = Functions.mapOrDefault(lorebookEntriesToUpdate, List.of(), List::copyOf);
        lorebookEntriesToDelete = Functions.mapOrDefault(lorebookEntriesToDelete, List.of(), List::copyOf);
    }
}
