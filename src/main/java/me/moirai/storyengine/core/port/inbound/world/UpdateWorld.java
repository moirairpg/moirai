package me.moirai.storyengine.core.port.inbound.world;

import java.util.List;
import java.util.UUID;

import me.moirai.storyengine.common.cqs.command.Command;
import me.moirai.storyengine.common.util.Functions;

public record UpdateWorld(
        UUID worldId,
        String name,
        String description,
        String adventureStart,
        String narratorName,
        String narratorPersonality,
        Double uiImagePositionX,
        Double uiImagePositionY,
        List<LorebookEntryToAdd> lorebookEntriesToAdd,
        List<LorebookEntryToUpdate> lorebookEntriesToUpdate,
        List<UUID> lorebookEntriesToDelete,
        UUID requesterId)
        implements Command<WorldDetails> {

    public record LorebookEntryToAdd(String name, String description) {}

    public record LorebookEntryToUpdate(UUID id, String name, String description) {}

    public UpdateWorld {
        lorebookEntriesToAdd = Functions.mapOrDefault(lorebookEntriesToAdd, List.of(), List::copyOf);
        lorebookEntriesToUpdate = Functions.mapOrDefault(lorebookEntriesToUpdate, List.of(), List::copyOf);
        lorebookEntriesToDelete = Functions.mapOrDefault(lorebookEntriesToDelete, List.of(), List::copyOf);
    }
}
