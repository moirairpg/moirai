package me.moirai.discordbot.core.domain.adventure;

import java.util.List;

import me.moirai.discordbot.core.application.usecase.adventure.request.CreateAdventureLorebookEntry;
import me.moirai.discordbot.core.application.usecase.adventure.request.DeleteAdventureLorebookEntry;
import me.moirai.discordbot.core.application.usecase.adventure.request.GetAdventureLorebookEntryById;
import me.moirai.discordbot.core.application.usecase.adventure.request.UpdateAdventureLorebookEntry;
import reactor.core.publisher.Mono;

public interface AdventureService {

    Mono<AdventureLorebookEntry> createLorebookEntry(CreateAdventureLorebookEntry command);

    Mono<AdventureLorebookEntry> updateLorebookEntry(UpdateAdventureLorebookEntry command);

    List<AdventureLorebookEntry> findAllLorebookEntriesByRegex(String adventureId, String valueToSearch);

    AdventureLorebookEntry findLorebookEntryByPlayerDiscordId(String adventureId, String playerDiscordId);

    AdventureLorebookEntry findLorebookEntryById(GetAdventureLorebookEntryById query);

    void deleteLorebookEntry(DeleteAdventureLorebookEntry command);
}
