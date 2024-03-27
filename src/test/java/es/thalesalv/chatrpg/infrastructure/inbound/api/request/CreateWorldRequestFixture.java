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
                .writerUsers(world.getWriterUsers())
                .readerUsers(world.getReaderUsers())
                .readerUsers(world.getReaderUsers())
                .writerUsers(world.getWriterUsers());
    }
}
