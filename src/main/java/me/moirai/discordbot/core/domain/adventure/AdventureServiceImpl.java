package me.moirai.discordbot.core.domain.adventure;

import me.moirai.discordbot.common.annotation.DomainService;
import me.moirai.discordbot.common.exception.AssetNotFoundException;

@DomainService
public class AdventureServiceImpl implements AdventureService {

    private final AdventureDomainRepository repository;

    public AdventureServiceImpl(AdventureDomainRepository repository) {
        this.repository = repository;
    }

    @Override
    public Adventure getById(String adventureId) {

        return repository.findById(adventureId)
                .orElseThrow(() -> new AssetNotFoundException("Adventure to be viewed was not found"));
    }
}
