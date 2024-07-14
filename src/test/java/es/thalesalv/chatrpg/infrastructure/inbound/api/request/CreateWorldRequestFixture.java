package es.thalesalv.chatrpg.infrastructure.inbound.api.request;

import es.thalesalv.chatrpg.core.domain.world.World;
import es.thalesalv.chatrpg.core.domain.world.WorldFixture;

public class CreateWorldRequestFixture {

    public static CreateWorldRequest createPrivateWorld() {

        World world = WorldFixture.privateWorld().build();
        CreateWorldRequest request = new CreateWorldRequest();

        request.setName(world.getName());
        request.setDescription(world.getDescription());
        request.setAdventureStart(world.getAdventureStart());
        request.setVisibility(world.getVisibility().toString());
        request.setUsersAllowedToWrite(world.getUsersAllowedToWrite());
        request.setUsersAllowedToRead(world.getUsersAllowedToRead());
        request.setUsersAllowedToRead(world.getUsersAllowedToRead());
        request.setUsersAllowedToWrite(world.getUsersAllowedToWrite());

        return request;
    }
}
