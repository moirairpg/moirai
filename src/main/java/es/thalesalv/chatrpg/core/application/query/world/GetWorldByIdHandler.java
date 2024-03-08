package es.thalesalv.chatrpg.core.application.query.world;

import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.common.cqrs.query.QueryHandler;
import es.thalesalv.chatrpg.common.exception.AssetNotFoundException;
import es.thalesalv.chatrpg.core.domain.world.World;
import es.thalesalv.chatrpg.core.domain.world.WorldRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GetWorldByIdHandler extends QueryHandler<GetWorldById, GetWorldResult> {

    private final WorldRepository repository;

    @Override
    public GetWorldResult handle(GetWorldById query) {

        World world = repository.findById(query.getId())
                .orElseThrow(() -> new AssetNotFoundException("World not found"));

        return mapResult(world);
    }

    private GetWorldResult mapResult(World world) {

        return GetWorldResult.builder()
                .id(world.getId())
                .build();
    }
}
