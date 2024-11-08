package me.moirai.discordbot.infrastructure.inbound.api.mapper;

import org.springframework.stereotype.Component;

import me.moirai.discordbot.core.application.usecase.world.request.CreateWorldLorebookEntry;
import me.moirai.discordbot.core.application.usecase.world.request.DeleteWorldLorebookEntry;
import me.moirai.discordbot.core.application.usecase.world.request.UpdateWorldLorebookEntry;
import me.moirai.discordbot.infrastructure.inbound.api.request.CreateWorldLorebookEntryRequest;
import me.moirai.discordbot.infrastructure.inbound.api.request.UpdateWorldLorebookEntryRequest;

@Component
public class WorldLorebookEntryRequestMapper {

    public CreateWorldLorebookEntry toCommand(CreateWorldLorebookEntryRequest request,
            String worldId, String requesterDiscordId) {

        return CreateWorldLorebookEntry.builder()
                .name(request.getName())
                .description(request.getDescription())
                .playerDiscordId(request.getPlayerDiscordId())
                .regex(request.getRegex())
                .worldId(worldId)
                .requesterDiscordId(requesterDiscordId)
                .build();
    }

    public UpdateWorldLorebookEntry toCommand(UpdateWorldLorebookEntryRequest request, String entryId,
            String worldId, String requesterDiscordId) {

        return UpdateWorldLorebookEntry.builder()
                .id(entryId)
                .name(request.getName())
                .description(request.getDescription())
                .playerDiscordId(request.getPlayerDiscordId())
                .regex(request.getRegex())
                .isPlayerCharacter(request.isPlayerCharacter())
                .requesterDiscordId(requesterDiscordId)
                .worldId(worldId)
                .build();
    }

    public DeleteWorldLorebookEntry toCommand(String entryId, String worldId, String requesterId) {

        return DeleteWorldLorebookEntry.builder()
                .lorebookEntryId(entryId)
                .worldId(worldId)
                .requesterDiscordId(requesterId)
                .build();
    }
}
