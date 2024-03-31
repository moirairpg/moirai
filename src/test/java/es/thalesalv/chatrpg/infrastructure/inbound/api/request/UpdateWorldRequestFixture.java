package es.thalesalv.chatrpg.infrastructure.inbound.api.request;

import es.thalesalv.chatrpg.core.domain.world.World;
import es.thalesalv.chatrpg.core.domain.world.WorldFixture;

public class UpdateWorldRequestFixture {

    public static  UpdateWorldRequest.Builder createPrivateWorld() {

        World world = WorldFixture.privateWorld().build();

        return  UpdateWorldRequest.builder()
                .name(world.getName())
                .description(world.getDescription())
                .adventureStart(world.getAdventureStart())
                .visibility(world.getVisibility().toString())
                .usersAllowedToWriteToAdd(world.getUsersAllowedToWrite())
                .usersAllowedToReadToAdd(world.getUsersAllowedToRead())
                .usersAllowedToWriteToRemove(world.getUsersAllowedToWrite())
                .usersAllowedToReadToRemove(world.getUsersAllowedToRead());
    }
}
