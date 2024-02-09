package es.thalesalv.chatrpg.core.application.query.world;

import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.common.cqrs.query.QueryHandler;
import es.thalesalv.chatrpg.common.exception.AssetNotFoundException;
import es.thalesalv.chatrpg.core.domain.world.World;
import es.thalesalv.chatrpg.core.domain.world.WorldRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetWorldByIdHandler extends QueryHandler<GetWorldById, GetWorldByIdResult> {

    private final WorldRepository repository;

    @Override
    public GetWorldByIdResult handle(GetWorldById query) {

        World world = repository.findById(query.getId())
                .orElseThrow(() -> new AssetNotFoundException("World not found"));

        return mapResult(world);
    }

    private GetWorldByIdResult mapResult(World world) {

        return GetWorldByIdResult.builder()
                .id(world.getId())
                .build();
    }
}
