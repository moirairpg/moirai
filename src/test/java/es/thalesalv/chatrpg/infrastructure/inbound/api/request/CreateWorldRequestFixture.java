package es.thalesalv.chatrpg.infrastructure.inbound.api.request;

import es.thalesalv.chatrpg.core.domain.world.World;
import es.thalesalv.chatrpg.core.domain.world.WorldFixture;

public class CreateWorldRequestFixture {

    public static CreateWorldRequest.Builder createPrivateWorld() {

        World world = WorldFixture.privateWorld().build();

        return CreateWorldRequest.builder()
                .name(world.getName())
                .description(world.getDescription())
                .adventureStart(world.getAdventureStart())
                .visibility(world.getVisibility().toString())
                .usersAllowedToWrite(world.getUsersAllowedToWrite())
                .usersAllowedToRead(world.getUsersAllowedToRead())
                .usersAllowedToRead(world.getUsersAllowedToRead())
                .usersAllowedToWrite(world.getUsersAllowedToWrite());
    }
}
