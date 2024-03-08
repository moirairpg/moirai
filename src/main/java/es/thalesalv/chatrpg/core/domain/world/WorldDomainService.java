package es.thalesalv.chatrpg.core.domain.world;

import es.thalesalv.chatrpg.core.application.command.world.CreateWorld;
import es.thalesalv.chatrpg.core.application.command.world.UpdateWorld;

public interface WorldDomainService {

    World createFrom(CreateWorld command);

    World update(UpdateWorld command);
}
