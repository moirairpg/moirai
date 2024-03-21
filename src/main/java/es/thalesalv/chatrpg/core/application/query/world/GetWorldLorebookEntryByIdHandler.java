package es.thalesalv.chatrpg.core.application.query.world;

import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.common.exception.AssetNotFoundException;
import es.thalesalv.chatrpg.common.usecases.UseCaseHandler;
import es.thalesalv.chatrpg.core.domain.world.WorldLorebookEntry;
import es.thalesalv.chatrpg.core.domain.world.WorldLorebookEntryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GetWorldLorebookEntryByIdHandler extends UseCaseHandler<GetWorldLorebookEntryById, GetWorldLorebookEntryResult> {

    private final WorldLorebookEntryRepository repository;

    @Override
    public GetWorldLorebookEntryResult execute(GetWorldLorebookEntryById query) {

        // TODO add check based on world permission
        WorldLorebookEntry entry = repository.findById(query.getId())
                .orElseThrow(() -> new AssetNotFoundException("Lorebook entry not found"));

        return mapResult(entry);
    }

    private GetWorldLorebookEntryResult mapResult(WorldLorebookEntry entry) {

        return GetWorldLorebookEntryResult.builder()
                .id(entry.getId())
                .build();
    }
}
