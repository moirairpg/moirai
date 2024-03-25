package es.thalesalv.chatrpg.infrastructure.inbound.api.mapper;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.core.application.command.world.CreateWorldLorebookEntry;
import es.thalesalv.chatrpg.infrastructure.inbound.api.request.WorldLorebookEntryRequest;

@Component
public class WorldLorebookEntryMapper {

    public CreateWorldLorebookEntry toCommand(WorldLorebookEntryRequest request) {

        return CreateWorldLorebookEntry.builder()
                .name(request.getName())
                .description(request.getDescription())
                .playerDiscordId(request.getPlayerDiscordId())
                .regex(request.getRegex())
                .worldId(request.getWorldId())
                .isPlayerCharacter(request.isPlayerCharacter())
                .build();
    }
}
