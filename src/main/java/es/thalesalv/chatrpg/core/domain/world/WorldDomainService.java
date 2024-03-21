package es.thalesalv.chatrpg.core.domain.world;

import es.thalesalv.chatrpg.core.application.command.world.CreateWorld;
import es.thalesalv.chatrpg.core.application.command.world.CreateWorldLorebookEntry;
import es.thalesalv.chatrpg.core.application.command.world.UpdateWorld;
import es.thalesalv.chatrpg.core.application.command.world.UpdateWorldLorebookEntry;

public interface WorldDomainService {

    World createFrom(CreateWorld command);

    World update(UpdateWorld command);

    WorldLorebookEntry createLorebookEntry(CreateWorldLorebookEntry command);

    WorldLorebookEntry updateLorebookEntry(UpdateWorldLorebookEntry command);
}
