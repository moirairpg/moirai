package es.thalesalv.chatrpg.core.application.command.world;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.common.exception.AssetNotFoundException;
import es.thalesalv.chatrpg.common.usecases.UseCaseHandler;
import es.thalesalv.chatrpg.core.domain.world.WorldLorebookEntryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class DeleteWorldLorebookEntryHandler extends UseCaseHandler<DeleteWorldLorebookEntry, Void> {

    private static final String ID_CANNOT_BE_NULL_OR_EMPTY = "Lorebook entry ID cannot be null or empty";
    private static final String CANNOT_DELETE_NOT_FOUND = "Cannot delete non-existing lorebook entry";

    private final WorldLorebookEntryRepository repository;

    @Override
    public void validate(DeleteWorldLorebookEntry command) {

        if (StringUtils.isBlank(command.getId())) {
            throw new IllegalArgumentException(ID_CANNOT_BE_NULL_OR_EMPTY);
        }
    }

    @Override
    public Void execute(DeleteWorldLorebookEntry command) {

        repository.findById(command.getId())
                .orElseThrow(() -> new AssetNotFoundException(CANNOT_DELETE_NOT_FOUND));

        repository.deleteById(command.getId());

        return null;
    }
}
