package es.thalesalv.chatrpg.core.domain.world;

import es.thalesalv.chatrpg.core.application.command.world.CreateWorld;

public interface WorldDomainService {

    World createFrom(CreateWorld command);
}
